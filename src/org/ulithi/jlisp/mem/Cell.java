package org.ulithi.jlisp.mem;

import org.apache.commons.lang3.Validate;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.parser.Grammar;

import static org.ulithi.jlisp.mem.NilReference.NIL;

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
 * <p><strong>NIL vs NULL</strong></p>
 * A key concept in understanding how cells, atoms and lists work is to understand the difference
 * between {@code NIL} and {@code NULL}. {@code NIL} is a LISP language concept. It is a special
 * value that is both a list and an atom. But the key point is that it is a <em>value</em> defined
 * by the language.
 * <p>
 * {@code NULL}, on the other hand, is a terminal reference: it indicates that the cell field
 * literally refers to nothing: no cell, no value (including {@code NIL}), no list. It is not
 * a language-defined value: it is an internal implementation concept.
 * <p>
 * In general, when {@code NIL} appears as the first element in a cell, it is interpreted as an
 * atom. When it appears as the second element in a cell, it is interpreted as the end of a list.
 * {@code NULL} can only appear as the second element in the cell, and its meaning is that the
 * cell is pure storage for whatever the first value is. It is <em>not</em> a list element.
 * <p>
 * So, valid (and invalid) cells include:<ul>
 *     <li>{@code (ATOM . NIL)} - A terminal list node whose value is an atom.</li>
 *     <li>{@code (NIL . NIL)} - A terminal list node whose value is {@code NIL}.</li>
 *     <li>{@code (LIST . NIL} - A terminal list node whose value is a list.</li>
 *     <li>{@code (ATOM . <Cell>)} - A non-terminal list node whose value is an atom.</li>
 *     <li>{@code (NIL . <Cell>>)} - A non-terminal list node whose value is {@code NIL}.</li>
 *     <li>{@code (LIST . <Cell>)} - A non-terminal list node whose value is a list.</li>
 *     <li>{@code (ATOM . <null>)} - Pure storage for an atom-value.</li>
 *     <li>{@code (NIL . <null>)} - The empty list!</li>
 *     <li>{@code (LIST . <null>)} - Invalid.</li>
 *     <li>{@code (<null> . <anything>)} - Invalid.</li>
 * </ul>
 */
public class Cell implements Ref {

    /** The {@code NULL} reference, signifying a non-list (a.k.a. storage) node. */
    public static final Ref NULL_REF = null;

    /**
     * The first/lhs field in this cell. This can be an Atom, or a reference to a Cell, which
     * is interpreted as the head cell of a sub-list.
     */
    private Ref first;

    /**
     * The second/rhs field in this cell. This is typically either a reference to the next
     * Cell, or NIL. If NULL, it signifies the cell is storage-only: not a list node.
     */
    private Ref rest;

    /**
     * Private constructor. Use one of the {@code create} methods to create a new Cell.
     *
     * @param first The new Cell's {@code first} element.
     * @param rest The new Cell's {@code rest} element.
     */
    private Cell(final Ref first, final Ref rest) {
        Validate.notNull(first);
        this.first = first;
        this.rest = rest;
    }

    /**
     * Creates a {@link Cell} representing a terminal list node with the given {@link Ref}
     * as its value.
     *
     * @param ref A {@code Ref} that will be the new {@code Cell's} first element.
     * @return A new {@link Cell} of the form {@code (<ref> . NIL)}.
     */
    public static Cell create(final Ref ref) {
        return new Cell(ref, NIL);
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
     * {@code token} as the {@code first} element and {@code NULL_REF} as the {@code rest}
     * element.
     *
     * @param token A literal used to construct the Atom for the new cell's first element.
     * @return A new storage-only {@code Cell} of the form {@code (ATOM . NULL_REF)}.
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
     * value as the {@code first} element and {@code NULL_REF} as the {@code rest} element.
     *
     * @param value An integer used to construct the Atom for the new cell's first element.
     * @return A new storage-only {@code Cell} of the form {@code (ATOM . NULL_REF)}.
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
     * value as the {@code first} element and {@code NULL_REF} as the {@code rest} element.
     *
     * @param bool A Boolean used to construct the Atom for the new cell's first element.
     * @return A new storage-only {@code Cell} of the form {@code (ATOM . NULL_REF)}.
     */
    public static Cell createStorage(final boolean bool) {
        return createStorage(Atom.create(bool));
    }

    /**
     * Constructs a new storage-only {@link Cell} with the given {@link Ref} -- assumed to be a
     * pure atom or NIL, not a node of a list -- as the {@code first} element and {@code NIL} as the
     * {@code rest} element. A {@link Cell} created by this method cannot be part of a {@code List}.
     * @param ref The {@link Ref} that this cell provides memory storage for.
     * @return A new storage-only {@code Cell} of the form {@code (ref . NULL_REF)}.
     */
    public static Cell createStorage(final Ref ref) {
        return new Cell(ref, NULL_REF);
    }

    /**
     * Constructs a new {@link Cell} with the given {@code Cell} -- assumed to be the root cell
     * of a list -- as the {@code first} element and {@code NIL} as the {@code rest} element.
     * @param cell The root cell of a list.
     * @return A new {@code Cell} of the form {@code (ROOT_CELL . NIL)}.
     */
    public static Cell createAsList(final Cell cell) {
        return new Cell(cell, NIL);
    }

    /**
     * Creates a new {@link Cell} with {@code NIL} as its {@code first} element and {@code NULL_REF}
     * as its {@code rest} element, signifying te empty list.
     * @return A new {@code Cell} representing an empty list, of the form {@code (NIL . NULL_REF)}.
     */
    public static Cell create() {
        return new Cell(NIL, NULL_REF);
    }

    /**
     * Indicates if this {@link Cell} represents a pure (storage-only) {@link Atom}.
     * @return True if this cell is a storage-only {@code Atom}, false otherwise.
     */
    @Override
    public boolean isAtom() {
        return first.isAtom() && rest == NULL_REF;
    }

    /**
     * Returns this cell's {@code first} value as an {@link Atom}.
     * @return This cell's {@code first} value as an {@link Atom}.
     */
    public Atom toAtom() {
        return (Atom)first;
    }

    /**
     * Indicates if this {@link Cell Cell's} {@code first} element is a {@link List}.
     * @return True if this {@code Cell's} {@code first} element is a {@code List}, false
     *         otherwise.
     */
    @Override
    public boolean isList() {
        return first.isList();
    }

    /**
     * Indicates if this {@link Cell} stores the special {@code NIL} value.
     * @return True if this {@code Cell} is a "NIL cell", false otherwise.
     */
    @Override
    public boolean isNil() {
        return first.equals(NIL);
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
        return Grammar.LPAREN +
               first +
               Grammar.SPACE +
               Grammar.DOT +
               Grammar.SPACE +
               rest +
               Grammar.RPAREN;
    }
}
