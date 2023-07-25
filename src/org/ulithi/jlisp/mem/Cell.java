package org.ulithi.jlisp.mem;

import org.apache.commons.lang3.Validate;

import static org.ulithi.jlisp.parser.Grammar.*;

/**
 * A {@link Cell} is a pair of {@link Ref references}: {@code first} (or {@code left}) and
 * {@code rest} (or {@code right}). Both references are <em>required</em>, however the
 * {@code rest} reference can be the special {@code NIL} value. A reference can refer to an
 * {@link Atom} or another {@code Cell}.
 * <p>
 * Cells are part of JLISP's <em>memory model</em>, not part of the language model. While it seemed
 * natural for cell fields to be treated as CAR and CDR, they aren't quite the same. E.g., the
 * CDR of (2 (1 2 3)) is (1 2 3): a list. But the in-memory representation is (2 . ((1 2 3) . NIL)):
 * the "CDR" of that is a <em>cell</em>, not a list.
 * <p>
 * So, within a cell -- a dotted-pair -- the first/left-hand field is called {@code first} and the
 * second/right-hand field is called {@code rest}. When callers fetch these fields, they receive
 * {@code Refs}, which they then need to handle appropriately, depending on whether the reference
 * is to an {@code Atom} or another {@code Cell}.
 */
public class Cell implements Ref {
    /**
     * The first/lhs field in this cell. This can be an Atom, or a reference to a Cell.
     */
    private Ref first;

    /**
     * The second/rhs field in this cell. This is typically either a reference to the next
     * Cell, or NIL.
     */
    private Ref rest;

    /**
     * Private constructor. Use one of the {@code create} methods to create a new Cell.
     * @param first The new Cell's {@code first} element.
     * @param rest The new Cell's {@code rest} element.
     */
    private Cell(final Ref first, final Ref rest) {
        Validate.notNull(first);
        Validate.notNull(rest);
        this.first = first;
        this.rest = rest;
    }

    /**
     * Constructs a new {@link Cell} with a literal {@link Atom} for the given {@code token} as
     * the {@code first} element and {@code NIL} as the {@code rest} element.
     *
     * @param token A literal used to construct the Atom for the new cell's first element.
     * @return A new {@code Cell} of the form (ATOM . NIL).
     */
    public static Cell create(final String token) {
        return create(Atom.create(token));
    }

    /**
     * Constructs a new {@link Cell} with the given {@link Atom} as the {@code first} element and
     * {@code NIL} as the {@code rest} element.
     * @param atom The {@code Atom} that will be the new cell's {@code first} element.
     * @return A new {@code Cell} of the form (ATOM . NIL).
     */
    public static Cell create(final Atom atom) {
        return new Cell(atom, Atom.NIL);
    }

    /**
     * Constructs a new {@link Cell} with the given {@code Cell} -- assumed to be the root cell
     * of a list -- as the {@code first} element and {@code NIL} as the {@code rest} element.
     * @param cell The root cell of a list.
     * @return A new {@code Cell} of the form (ROOT_CELL . NIL).
     */
    public static Cell createAsList(final Cell cell) {
        return new Cell(cell, Atom.NIL);
    }

    /**
     * Creates a new {@link Cell} with {@code NIL} as both its {@code first} and {@code rest}
     * fields. Such a cell isn't valid (I think) as a LISP construct, but as a memory construct
     * it is useful for initializing the equivalent of a null {@code Cell} reference.
     * @return A new {@code Cell} with the {@code NIL} value for both fields.
     */
    public static Cell create() {
        return new Cell(Atom.NIL, Atom.NIL);
    }

    /**
     * A {@link Cell} itself is not an {@code Atom}, although its elements may be.
     * @return False.
     */
    @Override
    public boolean isAtom() {
        return false;
    }

    /**
     * Indicates if this {@link Cell Cell's} {@code first} element is an {@link List}.
     * @return True if this {@code Cell's} {@code first} element is an {{@code List}, false
     *         otherwise.
     */
    @Override
    public boolean isList() {
        return first.isList();
    }

    /**
     * Indicates if this {@link Cell} is the special "NIL" cell.
     * @return True if this {@code Cell} is a "NIL cell", false otherwise.
     */
    @Override
    public boolean isNil() {
        return (first.equals(Atom.NIL) && rest.equals(Atom.NIL));
    }

    /**
     * Indicates if this {@link Cell} is a {@code Cell}. It is.
     * @return True.
     */
    @Override
    public boolean isCell() {
        return true;
    }

    /**
     * Returns the {@code first} field of this {@link Cell} as a {@link Ref}.
     * @return The {@code first} field of this {@code Cell}.
     */
    public Ref getFirst() {
        return this.first;
    }

    /**
     * Sets the {@code car} (first/lhs) element of this {@link} cell to the {@link Atom}, Symbol
     * or List represented by the given {@link SExpression}.
     * @param sexp A non-null {@link SExpression}.
     */
    @Deprecated
    public void setFirst(final Ref sexp) {
        Validate.notNull(sexp);
        this.first = sexp;
    }

    /**
     * Returns the {@code rest} field of this {@link Cell} as a {@link Ref}.
     * @return The {@code rest} field of this {@code Cell}.
     */
    public Ref getRest() {
        return this.rest;
    }

    /**
     * Sets the {@code rest} field of this {@link} cell to the {@link Atom}, Symbol
     * or List represented by the given {@link Ref}.
     * @param ref A non-null {@link Ref}.
     */
    public void setRest(final Ref ref) {
        Validate.notNull(ref);
        this.rest = ref;
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
