package org.ulithi.jlisp.mem;

import org.apache.commons.lang3.Validate;

import static org.ulithi.jlisp.parser.Symbols.*;

/**
 * A {@link Cell} -- a.k.a. a {@code cons} in LISP terminology -- is a pair of {@link SExpression}
 * references: {@code car} (or {@code left}) and {@code cdr} (or {@code right}). Both references
 * are <em>required</em>, however the {@code cdr} reference can be the special {@code NIL} value.
 */
public class Cell implements SExpression {
    /**
     * The car/first/lhs SExpression in this cell.
     */
    private SExpression car;

    /**
     * The cdr/second/rhs SExpression in this cell.
     */
    private SExpression cdr;

    /**
     * Constructs a new {@link Cell} with a literal {@link Atom} for the given {@code token} as
     * the first element and NIL for the rest element.
     *
     * @param token A literal used to construct the Atom for the new cell's first element.
     * @return The new {@code Cell}.
     */
    public static Cell create(final String token) {
        return new Cell(Atom.create(token), Atom.NIL);
    }

    /**
     * Constructs a new {@linl Cell} with a {@code Cell} (e.g., representing a list) as the
     * first element and NIL for the rest element.
     *
     * @param cell A {@link Cell} representing a {@code cons}.
     * @return The new {@code Cell}.
     */
    public static Cell create(final Cell cell) {
        return new Cell(cell, Atom.NIL);
    }

    /**
     * Constructs a new {@code Cell} with the given {@code car} and {@code cdr} elements.
     * @param car The new Cell's {@code car} element.
     * @param cdr The new Cell's {@code cdr} element.
     */
    private Cell(final SExpression car, final SExpression cdr) {
        Validate.notNull(car);
        Validate.notNull(cdr);
        this.car = car;
        this.cdr = cdr;
    }

    /**
     * Returns the {@code car} (first/lhs) element of this {@link Cell}.
     * @return The {@code car} (first/lhs) element of this {@code Cell}.
     */
    public SExpression getCar() {
        return this.car;
    }

    /**
     * Sets the {@code car} (first/lhs) element of this {@link} cell to the {@link Atom}, Symbol
     * or List represented by the given {@link SExpression}.
     * @param sexp A non-null {@link SExpression}.
     */
    public void setCar(final SExpression sexp) {
        Validate.notNull(sexp);
        this.car = sexp;
    }

    /**
     * Returns the {@code cdr} (second/rhs) element of this {@link Cell}.
     * @return The {@code cdr} element of this {@code Cell}.
     */
    public SExpression getCdr() {
        return this.cdr;
    }

    /**
     * Sets the {@code cdr} (second/rhs element of this {@link} cell to the {@link Atom}, Symbol
     * or List represented by the given {@link SExpression}.
     * @param sexp A non-null {@link SExpression}.
     */
    public void setCdr(final SExpression sexp) {
        Validate.notNull(sexp);
        this.cdr = sexp;
    }

    /**
     * Returns a {@code String} representation of this {@link Cell} as a dotted pair.
     * @return A dotted-pair representation of this cell.
     */
    @Override
    public String toString() {
        return LPAREN +
                car +
                SPACE +
                DOT +
                SPACE +
                cdr +
                RPAREN;
    }
}
