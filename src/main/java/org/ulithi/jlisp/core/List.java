package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.TypeConversionException;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.Ref;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.ulithi.jlisp.mem.NilReference.NIL;

/**
 * Represents a LISP list. A {@link List} is implemented as a chain of one or more {@link Cell Cells},
 * where the CAR of the {@code Cell} represents an element of the list (and is possible a list itself),
 * and the CDR is a reference to the next {@link Cell} in the list, or is {@code NIL} if the
 * {@code Cell} is the last cell in the list.
 */
public class List implements SExpression {

    /** The root cell of this List. */
    private Cell root;

    /**
     * Reference to the last top-level cell in this list: used to determine where
     * to add a new cell to extend (append to) the list.
     */
    private Cell end = null;

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
    }

    /**
     * Determines the end of this list: where the next top-level element will be inserted.
     * <p>
     * Many lists are created and never modified. Therefore, this O(n) operation is deferred
     * until there is actually a need to modify the end of the list. Once the end of the list
     * has been modified, there is no need to find it again.
     * @return The cell that is the terminal cell of this list.
     */
    private Cell getEnd() {
        if (end == null) {
            Cell curr = root;
            while (!curr.isTerminal()) {
                curr = curr.getRest().toCell();
            }
            end = curr;
        }
        return end;
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
     * Extends this {@link List} with the given {@link Function}. If this is an empty list, the
     * {@code Function} becomes the first element in this list. If this is not an empty list, the
     * {@code Function} is appended via a cell to this {@code list}.
     *
     * @param function The {@code Function} to append to this {@code List}.
     * @return This {@code List} with the given {@code Function} appended.
     */
    public List add(final Function function) {
        assert function != null : "function is null";

        final Cell cell = Cell.create(function);

        if (root.isNil()) {
            root = cell;
            end = root;
        } else {
            getEnd().setRest(cell);
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
            getEnd().setRest(cell);
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
            getEnd().setRest(cell);
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
            getEnd().setRest(list.getRoot());
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
        return (root.getFirst() == NIL);
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
     * {@inheritDoc}
     */
    @Override
    public SExpression toSExpression() {
        return this;
    }

    /**
     * Attempts to convert the given Ref to an SExpression.
     * @param ref The Ref to convert.
     * @return A NIL, List or Atom representation of the Ref.
     */
    private static SExpression refToSExpression(final Ref ref) {
        if (ref.isNil()) { return List.create(); }
        if (ref.isCell()) { return List.create(ref.toCell()); }
        return ref.toSExpression();
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
        if (root.isNil()) { return "NIL"; }
        StringBuilder sb = new StringBuilder();
        toStringImpl(root, sb);
        return sb.toString();
    }

    /**
     * Recursively generates a String representation of this List.
     * @param cell The root cell of the/a List.
     * @param sb StringBuilder used to recursively build a representation.
     */
    private void toStringImpl(final Cell cell, final StringBuilder sb) {
        if (cell == null || cell.isNil()) {
            return;
        }

        sb.append("( ");

        // Walk through the list, processing the current CAR of the list and then recursively
        // processing the CDR.
        Cell curr = cell;

        while (!curr.isNil()) {
            if (curr.hasList()) {
                toStringImpl(curr.getFirst().toCell(), sb);
            } else {
                sb.append(curr.getFirst().toString());
            }

            Ref rest = curr.getRest();

            if (rest.isNil()) {
                break;
            }

            // Handle dotted pairs
            if (curr.isTerminal()) {
                sb.append(" . ").append(rest);
                break;
            }

            sb.append(" ");
            curr = rest.toCell();
        }

        sb.append(" )");
    }
}
