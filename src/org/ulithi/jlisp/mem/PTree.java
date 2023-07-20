package org.ulithi.jlisp.mem;

/**
 * A {@link PTree} (Parse Tree) is a set of linked cells that are the in-memory representation of
 * a LISP statement.
 */
public class PTree {
    /**
     * The root cell of this PTree.
     */
    private Cell root;

    /**
     * Constructs a new {@link PTree} that has the given {@link Cell} as its root cell.
     * @param cell The root cell of the new {@code PTree}.
     */
    public PTree(final Cell cell) {
        assert cell != null : "cell is null";
        this.root = cell;
    }

    /**
     * Returns the root cell of this {@link PTree}.
     * @return The root cell of this {@link PTree}.
     */
    public Cell root() {
        return this.root;
    }

    /**
     * Returns a {@code String} containing a dotted-pair representation of this {@code PTree}.
     * @return A {@code String} representing this {@code PTree}.
     */
    @Override
    public String toString() {
        return root.toString();
    }

    /**
     * A PTree builder. How should this work. Let's consider some examples:
     *
     *   (+ 1 2 4) => (+ *) -> (1 *) -> (2 *) -> (4 NIL)
     *   So, on "(",
     *     1) Make a new PTree builder.
     *     2) On symbol, add cell like (V, NIL). If First cell, stop. If not first cell, chain to
     *        new cell from last cell.
     *     3) On ")" return builder.build().
     * TODO - Write actual class doc.
     */
    public static class Builder {
        /**
         * The root cell of the Ptree that this builder is constructing.
         */
        private Cell root = null;

        /**
         * Reference to the last cell added to the PTree that this builder is constructing.
         */
        private Cell end = null;

        /**
         * Adds the given {@link Cell} to the dotted-pair representation being constructed by this
         * builder. If this is the first {@code Cell} to be added, it becomes the root (leftmost)
         * cell of the representation. Otherwise, it is appended to the rightmost cell.
         *
         * @param cell The {@link} Cell to be added to the dotted-pair representation.
         * @return A reference to this {@link Builder}.
         */
        public Builder addCell(final Cell cell) {
            if (root == null) {
                root = cell;
            } else {
                end.setCdr(cell);
            }

            end = cell;

            return this;
        }

        /**
         * Adds the given {@link Cell} as a list to the dotted-pair representation being constructed
         * by this builder. If this is the first {@code Cell} to be added, it becomes a child cell
         * of the root/leftmost cell of the representation. Otherwise, it is appended to the
         * rightmost cell.

         * @param cell A {@link Cell}, representing a list, to be added to the dotted-pair
         *             representation.
         * @return A reference to this {@link Builder}.
         */
        public Builder addList(final Cell cell) {
            if (root == null) {
                root = Cell.create(cell);
                end = root;
            } else {
                end.setCdr(cell);
                end = cell;
            }

            return this;
        }

        /**
         * Constructs and returns a {@link PTree} (parse tree) for this builder's dotted-pair
         * representation.
         *
         * @return A parse tree based on the dotted-pair representation constructed by this builder.
         */
        public PTree build() {
            return new PTree(root);
        }
    }
}
