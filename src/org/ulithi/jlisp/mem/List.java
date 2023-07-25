package org.ulithi.jlisp.mem;

/**
 * Wrapper class for a LISP list.
 * <p>
 * TODO - Not sure this class is actually needed or useful.
 */
public class List implements SExpression {

    /** The root Cell of this List. */
    private final Cell root;

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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAtom() { return false; }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isList() { return true; }

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
     * {@inheritDoc}
     */
    public String toString() {
        return String.valueOf(root);
    }
}
