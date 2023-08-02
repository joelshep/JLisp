package org.ulithi.jlisp.mem;

import org.apache.commons.lang3.Validate;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;

import static org.ulithi.jlisp.parser.Grammar.*;

/**
 * {@link Cell Cells} are the basic unit of storage in JLISP, acting either as storage for a single
 * literal, or as a node in a list. All {@code cells} are pairs of {@link Ref references}:
 * {@code first} (or {@code left}) and {@code rest} (or {@code right}). Both references ar
 * e <em>required</em>. The {@code first} reference can be a reference to an {@link Atom} or to
 * the root {@code cell} of a sub-list. The {@code rest} reference can be a reference to a
 * {@code cell} representing the next element in the list, or the {@code NIL} atom representing
 * the end of the list, or the special {@code NAL} (Not A List) terminator if the {@code cell} is
 * pure storage (not a list node). A reference can refer to an {@link Atom} or another {@code Cell}.
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
     * The (very) special NAL reference, which is used only as the rest reference in a cell that
     * represents pure storage of a literal value. TODO - Note: I'm not sure if this is the right
     * way to handle such values, but the issue is that I "need" to use cells to represent both
     * standalone atomic values, as well as lists.
     */
    private static final class NALReference implements Ref {
        /**
         * Constructs a new NAL reference.
         */
        private NALReference() { }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() { return "NAL"; }
    }

    /** Singleton instance of the special NAL reference. */
    private static final Ref NAL = new NALReference();

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
     * Constructs a new list {@link Cell} with a literal {@link Atom} for the given {@code token}
     * as the {@code first} element and {@code NIL} as the {@code rest} element.
     *
     * @param token A literal used to construct the Atom for the new cell's first element.
     * @return A new list {@code Cell} of the form {@code (ATOM . NIL)}.
     */
    public static Cell create(final String token) {
        return create(Atom.create(token));
    }

    /**
     * Creates a new storage-only {@link Cell} with a literal {@link Atom} for the given
     * {@code token} as the {@code first} element and {@code NAL} as the {@code rest}
     * element.
     *
     * @param token A literal used to construct the Atom for the new cell's first element.
     * @return A new storage-only {@code Cell} of the form {@code (ATOM . NAL)}.
     */
    public static Cell createStorage(final String token) {
        return createStorage(Atom.create(token));
    }

    /**
     * Constructs a new list {@link Cell} with a literal {@link Atom} for the given integer value as
     * the {@code first} element and {@code NIL} as the {@code rest} element.
     *
     * @param value An integer used to construct the Atom for the new cell's first element.
     * @return A new list {@code Cell} of the form {@code (ATOM . NIL)}.
     */
    public static Cell create(final int value) {
        return create(Atom.create(value));
    }

    /**
     * Creates a new storage-only {@link Cell} with a literal {@link Atom} for the given integer
     * value as the {@code first} element and {@code NAL} as the {@code rest} element.
     *
     * @param value An integer used to construct the Atom for the new cell's first element.
     * @return A new storage-only {@code Cell} of the form {@code (ATOM . NAL)}.
     */
    public static Cell createStorage(final int value) {
        return createStorage(Atom.create(value));
    }

    /**
     * Constructs a new list{@link Cell} with a literal {@link Atom} for the given Boolean value as
     * the {@code first} element and {@code NIL} as the {@code rest} element.
     *
     * @param bool A Boolean used to construct the Atom for the new cell's first element.
     * @return A new list {@code Cell} of the form {@code (ATOM . NIL)}.
     */
    public static Cell create(final boolean bool) {
        return create(Atom.create(bool));
    }

    /**
     * Creates a new storage-only {@link Cell} with a literal {@link Atom} for the given Boolean
     * value as the {@code first} element and {@code NAL} as the {@code rest} element.
     *
     * @param bool A Boolean used to construct the Atom for the new cell's first element.
     * @return A new storage-only {@code Cell} of the form {@code (ATOM . NAL)}.
     */
    public static Cell createStorage(final boolean bool) {
        return createStorage(Atom.create(bool));
    }

    /**
     * Constructs a new list {@link Cell} with the given {@link Atom} as the {@code first} element
     * and {@code NIL} as the {@code rest} element.
     * @param atom The {@code Atom} that will be the new cell's {@code first} element.
     * @return A new list {@code Cell} of the form {@code (ATOM . NIL)}.
     */
    public static Cell create(final Atom atom) {
        return new Cell(atom, Atom.NIL);
    }

    /**
     * Constructs a new storage-only {@link Cell} with the given {@link Atom} -- assumed to be a
     * pure atom, not a node of a list -- as the {@code first} element and {@code NAL} as the
     * {@code rest} element. A {@link Cell} created by this method cannot be part of a {@code List}.
     * @param atom The {@link Atom} that this cell provides memory storage for.
     * @return A new storage-only {@code Cell} of the form {@code (atom . NAL)}.
     */
    public static Cell createStorage(final Atom atom) {
        return new Cell(atom, NAL);
    }

    /**
     * Constructs a new {@link Cell} with the given {@code Cell} -- assumed to be the root cell
     * of a list -- as the {@code first} element and {@code NIL} as the {@code rest} element.
     * @param cell The root cell of a list.
     * @return A new {@code Cell} of the form {@code (ROOT_CELL . NIL)}.
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
     * Indicates if this {@link Cell} is storage-only: i.e. a "pure atom".
     * @return True if this is a storage-only cell with a single {@link Atom} reference,
     *         false otherwise.
     */
    @Override
    public boolean isAtom() {
        return rest.equals(NAL);
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
     * Indicates if this {@link Ref} is a {@code Cell}. It is.
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
