package org.ulithi.jlisp.mem;

/**
 * A {@link PTree} (Parse Tree) is a set of linked cells that are the in-memory representation of
 * a LISP statement.
 */
public class PTree {
    /**
     * The root cell of this PTree.
     */
    private final Cell root;

    public PTree(final Cell cell) {
        assert cell != null : "cell is null";
        this.root = cell;
    }

    public Cell root() {
        return this.root;
    }
}
