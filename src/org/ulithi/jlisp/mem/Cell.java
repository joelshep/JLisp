package org.ulithi.jlisp.mem;

import org.apache.commons.lang3.Validate;

import static org.ulithi.jlisp.parser.Symbols.*;

/**
 * A {@link Cell} -- a.k.a. a {@code cons} in LISP terminology -- is a pair of {@link SExpression}
 * references: {@code first} (or {@code left}) and {@code rest} (or {@code right}). Both references
 * are <em>required</em>, however the {@code rest} reference can be the special {@code NIL} value.
 */
public class Cell implements SExpression {
    /**
     * The first/lhs SExpression in this cell.
     */
    private SExpression first;

    /**
     * The second/rhs SExpression in this cell.
     */
    private SExpression rest;

    /**
     * Constructs a new {@link Cell} with a literal {@link Atom} for the given {@code token} as
     * the first element and NIL for the rest element.
     * @param token A literal used to construct the Atom for the new cell's first element.
     * @return The new {@code Cell}.
     */
    public static Cell create(final String token) {
        return new Cell(Atom.create(token), Atom.NIL);
    }

    /**
     * Constructs a new {@code Cell} with the given {@code first} and {@code rest} elements.
     * @param first The new Cell's {@code first} element.
     * @param rest The new Cell's {@code rest} element.
     */
    private Cell(final SExpression first, final SExpression rest) {
        Validate.notNull(first);
        Validate.notNull(rest);
        this.first = first;
        this.rest = rest;
    }

    /**
     * Returns the {@code first} element of this {@link Cell}.
     * @return The {@code first} element of this {@code Cell}.
     */
    public SExpression getFirst() {
        return this.first;
    }

    /**
     * Sets the {@code first} element of this {@link} cell to the {@link Atom}, Symbol or List
     * represented by the given {@link SExpression}.
     * @param sexp A non-null {@link SExpression}.
     */
    public void setFirst (final SExpression sexp) {
        Validate.notNull(sexp);
        this.first = sexp;
    }

    /**
     * Returns the {@code rest} element of this {@link Cell}.
     * @return The {@code rest} element of this {@code Cell}.
     */
    public SExpression getRest() {
        return this.rest;
    }

    /**
     * Sets the {@code rest} element of this {@link} cell to the {@link Atom}, Symbol or List
     * represented by the given {@link SExpression}.
     * @param sexp A non-null {@link SExpression}.
     */
    public void setRest (final SExpression sexp) {
        Validate.notNull(sexp);
        this.rest = sexp;
    }

    /**
     * Returns a {@code String} representation of this {@link Cell} as a dotted pair.
     * @return A dotted-pair representation of this cell.
     */
    @Override
    public String toString() {
        return LPAREN +
                first +
                SPACE +
                DOT +
                SPACE +
                rest +
                RPAREN;
    }
}
