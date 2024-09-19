package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.JLispRuntimeException;
import org.ulithi.jlisp.exception.TypeConversionException;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.mem.Ref;

import java.util.LinkedList;
import java.util.Queue;

import static org.ulithi.jlisp.mem.NilReference.NIL;

/**
 * Wrapper class for a LISP list.
 */
public class List implements SExpression {

    /** The root cell of this List. */
    private Cell root;

    /**
     * Reference to the last top-level cell in this list: used to determine where
     * to add a new cell to extend the list.
     */
    private Cell end;

    /**
     * Creates an empty {@link List}.
     * @return An empty {@code List}.
     */
    public static List create() {
        return new List(Cell.create());
    }

    /**
     * Creates a new list with the given {@link Ref} (a cell or a list) as its root node.
     * @param ref A {@code Cell} or {@code List} representing the root node of this list.
     * @return A new {@link List}.
     */
    public static List create(final Ref ref) {
        assert ref != null: "Ref is null";
        if (ref.isNil()) { return List.create(); }
        if (ref.isCell()) { return new List(ref.toCell()); }
        throw new TypeConversionException("Cannot create List from Atom");
    }

    /**
     * Private constructor: constructs a new List with the given Cell as its root node.
     * @param root The root node of the new List.
     */
    private List(final Cell root) {
        this.root = root;
        this.end = root;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation throws a {@link TypeConversionException} because a {@code List} is not
     * an {@code Atom}.
     */
    @Override
    public Atom toAtom() {
        throw new TypeConversionException("Can't convert List to Atom");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List toList() {
        return this;
    }

    /**
     * Returns the {@link Cell} that is the root node of this {@link List}.
     * @return The root node of this list.
     */
    public Cell getRoot() {
        return root;
    }

    /**
     * Extends this {@link List} with the given {@link Atom}. If this is an empty list, the
     * {@code Atom} becomes the first element in this list. If this is not an empty list, the
     * {@code Atom} is appended via a cell to this {@code list}.
     *
     * @param atom The {@code Atom} to append to this {@code List}.
     * @return This {@code List} with the given {@code Atom} appended.
     */
    public List add(final Atom atom) {
        assert atom != null : "atom is null";

        final Cell cell = Cell.create(atom);

        if (root.isNil()) {
            root = cell;
            end = root;
        } else {
            end.setRest(cell);
            end = cell;
        }

        return this;
    }

    /**
     * Extends this {@link List} by adding a new cell (dotted pair) with the given {@code List} as
     * the first element. E.g. adding (2 . (3 . NIL)) to (1 . NIL) yields (1 . ((2 . (3 . NIL)) . NIL)).
     * If this is an empty list, the {@code list} becomes the first element in this list (i.e. a
     * sublist of this list). If this is not an empty list, the {@code List} is appended via a cell
     * to this {@code list}, effectively extending this list.
     *
     * @param list The {@code List} to add to this {@code List}.
     * @return This {@code List} with the given {@code list} added.
     */
    public List add(final List list) {

        final Cell cell = Cell.createAsList(list.getRoot());

        if (this.isEmpty()) {
            root = cell;
            end = root;
        } else {
            end.setRest(cell);
            end = (Cell)end.getRest();
        }

        return this;
    }

    /**
     * Extends this {@link List} by appending the cells represent the {@code list} to this {@code List}.
     * For example, appending (2 . (3 . NIL)) to (1 . NIL) yields (1 . (2 . (3 . NIL))). If this is
     * an empty list, the {@code list} becomes the first element in this list (i.e. a sublist of this
     * list). If this is not an empty list, the {@code List} is appended via its root cell to this
     * {@code list}.
     *
     * @param list The {@code List} to add to this {@code List}.
     * @return This {@code List} with the given {@code list} appended.
     */
    public List append(final List list) {
        if (this.isEmpty()) {
            root = Cell.createAsList(list.getRoot());
            end = root;
        } else {
            end.setRest(list.getRoot());
            end = list.end;
        }

        return this;
    }

    /**
     * Indicates if this {@link List} is empty.
     * @return True if this list is empty, false otherwise.
     */
    public boolean isEmpty() {
        final Ref first = this.root.getFirst();
        return (first == NIL);
    }

    /**
     * Returns an {@link SExpression} representing this {@code List's} first element.
     * @return This {@code List's} first element.
     */
    public SExpression car() {
        final Ref ref = root.getFirst();
        if (ref.isNil()) { return List.create(); }
        if (ref.isAtom()) { return ref.toAtom(); }
        if (ref.isCell()) { return List.create(ref); }
        throw new JLispRuntimeException("Don't know how to fetch CAR of ref: " + ref);
    }

    /**
     * Returns a {@link SExpression} representing the referee of this list's root cell's
     * {@code rest} element.
     * @return The referee of this list's root cell's {@code rest} element.
     */
    public SExpression cdr() {
        final Ref ref = root.getRest();
        if (ref.isNil()) { return List.create(); }
        if (ref.isCell()) { return List.create(ref); }
        throw new JLispRuntimeException("Don't know how to fetch CDR of ref: " + ref);
    }

    /**
     * Indicates if this {@link List} is terminal (CDR is nil).
     * @return True if this {@code List} is terminal, false otherwise.
     */
    public boolean endp() {
        return root.getRest() == NIL;
    }

    /**
     * Returns the length of this {@link List} as an integer {@code Atom}. See {@link #lengthAsInt()}
     * for details on how the length is calculated. This is <em>not</em> a recursive function: it
     * returns the number of direct elements of this {@code List}. If some of those elements are
     * themselves {@code Lists}, they are counted as a single element for the purposes of this
     * function.
     *
     * @return An {@link Atom} representing the number of atoms or lists that are direct members
     *         of this list.
     */
    public Atom length() {
        return Atom.create(lengthAsInt());
    }

    /**
     * Returns the length of this {@link List} as an integer. This is <em>not</em> a recursive
     * function: it returns the number of direct elements of this {@code List}. If some of those
     * elements are themselves {@code Lists}, they are counted as a single element for the purposes
     * of this function.
     *
     * @return The number of atoms or lists that are direct members of this list.
     */
    public int lengthAsInt() {
        int count = 0;
        Ref curr = root;

        while (!curr.isNil()) {
            count++;
            curr = curr.toCell().getRest();
        }

        return count;
    }

    /**
     * Returns the <em>size</em> of this {@link List} as an integer {@code Atom}. Unlike the
     * {@code length()} method, this method <em>is</em> recursive: returning the total number of
     * {@code atoms} contained in this list and in any nested lists. Note that the returned value
     * does not include the count of lists themselves: only of atoms.
     * <p>
     * For example, the size of {@code (A B C)} is 3 (which is also the length). The size of
     * {@code (1 (2 3) 4)} is 4, while its length is only 3.
     *
     * @return An {@link Atom} representing the number of atoms that are direct members of this
     *         list and any sub-lists.
     */
    public Atom size() {
        return Atom.create(sizeAsInt());
    }

    /**
     * Using a breadth-first algorithm, recursively counts the number of atoms in this list, and
     * in any sub-lists. Sub-lists themselves are not included in the count: only the atoms that
     * they contain.
     *
     * @return The number of atoms that are direct members of this list and any sub-lists.
     */
    private int sizeAsInt() {
        int count = 0;
        Ref curr = root;
        final Queue<Ref> stack = new LinkedList<>();

        while (!curr.isNil()) {
            if (curr.isList()) {
                stack.add(curr.toCell().getFirst());
            } else {
                count++;
            }

            curr = curr.toCell().getRest();
        }

        while (!stack.isEmpty()) {
            count += (new List(stack.remove().toCell())).sizeAsInt();
        }

        return count;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        PTree pTree = new PTree(root);
        return pTree.unparse();
    }
}
