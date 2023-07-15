package org.ulithi.jlisp.mem;

import static org.ulithi.jlisp.parser.Symbols.*;

/**
 * A {@link Cell} -- a.k.a. a {@code cons} in LISP terminology -- is a pair of {@link SExpression}
 * references: {@code first} (or {@code left}) and {@code rest} (or {@code right}). Both references
 * are <em>required</em>, however the {@code rest} reference can be the special {@code NIL} value.
 */
public class Cell {
    private final SExpression first;
    private final SExpression rest;

    public Cell(final SExpression first, final SExpression rest) {
        assert first != null : "first is null";
        assert rest != null : "rest is null";
        this.first = first;
        this.rest = rest;
    }

    public SExpression first() {
        return this.first;
    }

    public SExpression rest() {
        return this.rest;
    }

    /**
     * Returns a {@code String} representation of this {@link Cell} as a dotted pair.
     * @return A dotted-pair representation of this cell.
     */
    @Override
    public String toString() {
        return new StringBuilder()
                .append(LPAREN)
                .append(first)
                .append(SPACE)
                .append(DOT)
                .append(SPACE)
                .append(rest)
                .toString();
    }
}
