package org.ulithi.jlisp.mem;

/**
 * A {@link PTree} (Parse Tree) is a linked list of {@link Cell Cells} (some of which may themselves
 * be the head nodes of other linked lists), and is the in-memory representation of a LISP expression.
 * A {@link PTree} can be directly transformed to dotted-pair notation.
 */
public final class PTree {
    /** The root cell of this PTree. */
    private Cell root;

    /**
     * The last ("rightmost") cell in the PTree's linked list of cells, found by recursively
     * dereferencing the {@code rest} field of each cell, start at the {@code root}.
     */
    private Cell end;

    /**
     * Constructs a new, empty PTree.
     */
    public PTree() {
        this.root = Cell.create();
        this.end = null;
    }

    /**
     * Constructs a new {@link PTree} that has the given {@link Cell} as its root cell.
     * @param cell The root cell of the new {@code PTree}.
     */
    public PTree(final Cell cell) {
        assert cell != null : "cell is null";
        this.root = cell;
        this.end = this.root;
    }

    /**
     * Returns the root cell of this {@link PTree}.
     * @return The root cell of this {@link PTree}.
     */
    public Cell root() {
        return this.root;
    }

    /**
     * Adds a {@link Cell} to this {@link PTree}. If this {@code PTree} is currently empty (root
     * cell is the NIL cell), the given {@code Cell} becomes the root cell of the tree. Otherwise,
     * it is added as the {@code rest} element of the "end" cell.
     *
     * @param cell A {@link Cell} to be added to this {@link PTree}.
     */
    public void add(final Cell cell) {
        assert cell != null : "cell is null";
        if (root.isNil()) {
            root = cell;
            end = root;
        } else {
            end.setRest(cell);
            end = cell;
        }
    }

    /**
     * Adds a list to this {@link PTree}. If this {@code PTree} is currently empty (root cell is
     * null), then the given {@code cell} becomes the CAR of the root cell of the tree. Otherwise,
     * it is added as the CDR of the "end" cell.
     * <p>
     * This method is very similar to other {@code add()} methods, but I named it differently to
     * be clear that it is effectively adding a new list/{@code PTree} to this {@code PTree}, not
     * simply extending this {@code PTree's} linked list of cells.
     *
     * @param cell A {@link Cell}, representing a list, to be added to this {@link PTree}.
     */
    public void addList(final Cell cell) {
        if (root.isNil()) {
            root = Cell.createAsList(cell);
            end = root;
        } else {
            end.setRest(Cell.createAsList(cell));
            end = (Cell)end.getRest();
        }
    }

    /**
     * Returns a {@code String} containing a dotted-pair representation of this {@code PTree}.
     * @return A {@code String} representing this {@code PTree}.
     */
    @Override
    public String toString() {
        return String.valueOf(root);
    }
}
