package org.ulithi.jlisp.mem;

/**
 * A {@link PTree} (Parse Tree) is a set of linked cells that are the in-memory representation of
 * a LISP expression. A {@link PTree} can be directly transformed to dotted-pair notation.
 */
public class PTree {
    /**
     * The root cell of this PTree.
     */
    private Cell root;

    /**
     * The last ("rightmost") cell found by recursively obtaining the cdr of each cell
     * starting at the root.
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
    public final Cell root() {
        return this.root;
    }

    /**
     * Adds a {@link Cell} to this {@link PTree}. If this {@code PTree} is currently empty (root
     * cell is null), the given {@code cell} becomes the root cell of the tree. Otherwise, it is
     * added as the CDR of the "end" cell.
     *
     * @param cell A {@link Cell} to be added to this {@link PTree}.
     */
    public void add(final Cell cell) {
        assert cell != null : "cell is null";
        if (root.isNil()) {
            root = cell;
            end = root;
        } else {
            end.setCdr(cell);
            end = cell;
        }
    }

    /**
     * Adds a list to this {@link PTree}. If this {@code PTree} is currently empty (root cell is
     * null), then the given {@code cell} becomes the CAR of the root cell of the tree. Otherwise,
     * it is added as the CDR of the "end" cell.
     *
     * @param cell A {@link Cell}, representing a list, to be added to this {@link PTree}.
     */
    public void addList(final Cell cell) {
        if (root.isNil()) {
            root = Cell.create(cell);
            end = root;
        } else {
            end.setCdr(cell);
            end = cell;
        }
    }

    /**
     * Evaluates the LISP expression represented by the {@link PTree} and returns the result.
     * @return An {@link SExpression} representing the result of the evaluation.
     */
    public SExpression evaluate() {
        return Atom.NIL;
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
