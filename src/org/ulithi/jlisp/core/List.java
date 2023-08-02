package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.JLispRuntimeException;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.Ref;

/**
 * Wrapper class for a LISP list.
 * <p>
 * TODO - Not sure this class is actually needed or useful.
 */
public class List extends SExpression {

    /** The root cell of this List. */
    private final Cell root;

    private Cell end;

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

    public void add(final Atom atom) {
        assert atom != null : "atom is null";

        if (root.isNil()) {
            root.setFirst(atom);
            end = root;
        } else {
            end.setRest(Cell.create());
            end = (Cell)end.getRest();
            end.setFirst(atom);
        }
    }

    public void add(final List list) {
        this.end.setRest(list.getRoot());
        this.end = list.getEnd();
    }

    protected Cell getEnd() {
        return this.end;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNil() { return false; }

    /**
     * Returns the {@link Cell} that is the root node of this {@link List}.
     * @return The root node of this list.
     */
    public Cell getRoot() {
        return root;
    }

    /**
     * Given a Cell, returns an s-expression represent the referee of the cell's first element.
     * TODO - Edit JavaDoc
     *
     * @return The referee of the cell's first element, as an s-expression.
     */
    public SExpression car() {
        final Ref ref = root.getFirst();
        if (ref.isAtom()) { return (Atom)ref; }
        if (ref.isCell()) { return List.create(ref); }
        throw new JLispRuntimeException("Don't know how to fetch CAR of ref: " + ref);
    }

    /**
     * Given a Cell, returns an s-expression represent the referee of the cell's first element.
     * TODO - Edit JavaDoc
     *
     * @return The referee of the cell's first element, as an s-expression.
     */
    public SExpression cdr() {
        final Ref ref = root.getRest();
        if (ref.isAtom()) { return (Atom)ref; }
        if (ref.isCell()) { return List.create(ref); }
        throw new JLispRuntimeException("Don't know how to fetch CDR of ref: " + ref);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return String.valueOf(root);
    }

}
