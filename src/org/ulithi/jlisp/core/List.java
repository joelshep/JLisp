package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.JLispRuntimeException;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.Ref;

import static org.ulithi.jlisp.mem.NilReference.NIL;

/**
 * Wrapper class for a LISP list.
 */
public class List extends SExpression {

    /** The root cell of this List. */
    private final Cell root;

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
     * Creates a new list with the given {@link Cell} as its root node.
     * @param root A {@code Cell} representing the root node of this list.
     * @return A new {@link List}.
     */
    public static List create(final Cell root) {
        return new List(root);
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
     * Returns the {@link Cell} that is the root node of this {@link List}.
     * @return The root node of this list.
     */
    public Cell getRoot() {
        return root;
    }

    /**
     * Extends this {@link List} with the given {@link Ref reference}. If this is an empty list,
     * the given {@code ref} becomes the first element in this list. If this is not an empty
     * list, the {@code ref} is appended to the last cell in this {@code list}.
     *
     * @param ref The {@code Ref} to append to this {@code List}.
     * @return This {@code List} with the given {@code ref} appended
     */
    public List add(final Ref ref) {
        assert ref != null : "ref is null";

        if (root.isNil()) {
            root.setFirst(ref);
            root.setRest(NIL);
            end = root;
        } else {
            end.setRest(ref);
            end = (Cell)end.getRest();
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

        if (root.isNil()) {
            root.setFirst(atom);
            root.setRest(NIL);
            end = root;
        } else {
            end.setRest(Cell.create(atom));
            end = (Cell)end.getRest();
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
        if (this.isEmpty()) {
            root.setFirst(list.getRoot());
            root.setRest(NIL);
            end = root;
        } else {
            end.setRest(Cell.createAsList(list.getRoot()));
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
            root.setFirst(list.getRoot());
            root.setRest(NIL);
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
     * Given a Cell, returns an s-expression representing the referee of the cell's first element.
     * @return The referee of the cell's first element, as an s-expression.
     */
    public SExpression car() {
        final Ref ref = root.getFirst();
        if (ref.isNil()) { return List.create(); }
        if (ref.isAtom()) { return (Atom)ref; }
        if (ref.isCell()) { return List.create(ref); }
        throw new JLispRuntimeException("Don't know how to fetch CAR of ref: " + ref);
    }

    /**
     * Returns an s-expression representing the referee of this list's root cell's {@code rest}
     * element.
     * @return The referee of this list's root cell's {@code rest} element, as an s-expression.
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
     * Returns the length of this {@link List}. This is <em>not</em> a recursive function: it
     * returns the number of direct elements of this {@code List}. If some of those elements are
     * themselves {@code Lists}, they are counted as a single element for the purposes of this
     * function.
     *
     * @return An {@link Atom} representing the number of atoms or lists that are direct members
     *         of this list.
     */
    public Atom length() {
        int count = 0;
        Ref curr = root;

        while (!curr.isNil()) {
            count++;
            curr = ((Cell)curr).getRest();
        }

        return Atom.create(count);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return String.valueOf(root);
    }
}
