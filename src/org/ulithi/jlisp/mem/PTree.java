package org.ulithi.jlisp.mem;

import java.util.List;

/**
 * A {@link PTree} (Parse Tree) is a set of linked cells that are the in-memory representation of
 * a LISP statement.
 */
public class PTree {
    /**
     * The root cell of this PTree.
     */
    private Cell root;

    public PTree(final Cell cell) {
        assert cell != null : "cell is null";
        this.root = cell;
    }

    public Cell root() {
        return this.root;
    }

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
     */
    public static class Builder {
        private Cell root = null;
        private Cell end = null;

        /**
         * Adds the given {@link Cell} the dotted pair expression being constructed by this builder.
         * If this is the first {@code Cell} to be added, it becomes the root (leftmost) cell of
         * the expression. Otherwise, it is appended to the rightmost cell.
         *
         * @param cell The {@link Cell to be added to the dotted-pair expression.}
         * @return A reference to this {@link Builder}.
         */
        public Builder addCell(final Cell cell) {
            if (root == null) {
                root = cell;
            } else {
                end.setRest(cell);
            }

            end = cell;

            return this;
        }

        /**
         * Constructs and returns a {@link PTree} (parse tree) for this builder's dotted-pair
         * expression.
         *
         * @return A parse tree based on the dotted-pair expression constructed by this builder.
         */
        public PTree build() {
            return new PTree(root);
        }
    }
}
