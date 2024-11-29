package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.JLispRuntimeException;
import org.ulithi.jlisp.exception.TypeConversionException;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.mem.Ref;

import java.util.ArrayDeque;
import java.util.Deque;

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
     *
     * @param root The root node of the new List.
     */
    private List(final Cell root) {
        this.root = root;
        Cell curr = root;
        // If the cell is the head of a list, traverse to find the
        // list end (where the next top-level element will be inserted.
        // TODO - It might be more efficient to do this on demand, so creating
        // a static list isn't an O(n) operation.
        while (!curr.isTerminal()) {
            curr = curr.getRest().toCell();
        }

        this.end = curr;
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
     * Convenience function that casts the given {@link SExpression} to an atom, list or function,
     * and appends it to this list accordingly.
     *
     * @param sexp The {@code SExpression} to append to this {@code List}.
     * @return This {@code List} with the given {@code SExpression} appended.
     */
    public List add(final SExpression sexp) {
        if (sexp.isAtom()) {
            add(sexp.toAtom());
        } else if (sexp.isList()) {
            add(sexp.toList());
        } else {
            add(sexp.toFunction());
        }

        return this;
    }

    /**
     * Extends this {@link List} with the given {@link Atom}. If this is an empty list, the
     * {@code Atom} becomes the first element in this list. If this is not an empty list, the
     * {@code Atom} is appended via a cell to this {@code list}.
     *
     * @param function The {@code Atom} to append to this {@code List}.
     * @return This {@code List} with the given {@code Atom} appended.
     */
    public List add(final Function function) {
        assert function != null : "function is null";

        final Cell cell = Cell.create(function);

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
     * {@inheritDoc}
     */
    @Override
    public boolean isNil() {
        return root.isNil();
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
        return refToSExpression(ref);
    }

    /**
     * Returns a {@link SExpression} representing the referee of this list's root cell's
     * {@code rest} element.
     * @return The referee of this list's root cell's {@code rest} element.
     */
    public SExpression cdr() {
        final Ref ref = root.getRest();
        return refToSExpression(ref);
    }

    /**
     * Returns a {@link SExpression} representing the {@code car} of the {@code cdr} of this list:
     * i.e., the second element in the list.
     * @return The {@code car} of the {@code cdr} of this list.
     */
    public SExpression cadr() {
        if (isNil() || cdr() == null || cdr().isNil()) {
            throw new EvaluationException("Cannot get cadr of a list with fewer than two elements");
        }

        return cdr().toList().car();
    }

    /**
     * Returns the n-th top-level element of this list, where the {@code car} of the list is the
     * zero-th element. Returns {@code NIL} if the given {@code index} is greater than or equal
     * to the length of this list.
     *
     * @param index The zero-based index of the element to return.
     * @return The list element at the given index.
     */
    public SExpression nth(final int index) {
        if (index <0) {
            throw new EvaluationException("Index " + index + " out of bounds");
        }

        if (root.isNil()) { return Atom.NIL; }
        if (index == 0) { return refToSExpression(root.getFirst()); }

        Ref curr = root;

        for (int i = 0; i < index && !curr.isNil(); i++) {
            curr = curr.toCell().getRest();
        }

        return curr.isNil() ? Atom.NIL : refToSExpression(curr.toCell().getFirst());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ref toRef() {
        if (isNil()) { return NIL; }
        return root;
    }

    /**
     * Attempts to convert the given Ref to an SExpression.
     * @param ref The Ref to convert.
     * @return A NIL, List or Atom representation of the Ref.
     */
    private static SExpression refToSExpression(final Ref ref) {
        if (ref.isNil()) { return List.create(); }
        if (ref.isAtom()) { return ref.toAtom(); }
        if (ref.isList()) { return ref.toList(); }
        if (ref.isCell()) { return List.create(ref); }
        if (ref.isFunction()) { return ref.toFunction(); }
        throw new JLispRuntimeException("Don't know how to convert ref: " + ref);
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
        if (root.isNil()) { return 0; }

        int count = 1;
        Ref curr = root;

        while (!curr.isNil()) {
            if (curr.toCell().isTerminal()) {
                if (!curr.toCell().getRest().isNil()) {
                    count++;
                }
                break;
            }

            curr = curr.toCell().getRest();
            count++;
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
        if (root.isNil()) {
            return 0;
        }

        int count = 0;
        final Deque<Ref> stack = new ArrayDeque<>();
        Cell curr = root;

        while (true) {
            if (curr.hasList()) {
                stack.add(curr.getFirst());
            } else {
                count++;
            }

            if (curr.isTerminal()) {
                if (!curr.getRest().isNil()) {
                    count++;
                }

                if (stack.isEmpty()) {
                    break;
                }

                curr = stack.pop().toCell();
            } else {
                curr = curr.getRest().toCell();
            }
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
