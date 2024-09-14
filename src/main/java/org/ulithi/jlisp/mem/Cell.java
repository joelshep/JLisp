package org.ulithi.jlisp.mem;

import org.apache.commons.lang3.Validate;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.parser.Grammar;

import java.util.Objects;

import static org.ulithi.jlisp.mem.NilReference.NIL;

/**
 * {@link Cell Cells} are the basic unit of storage in JLISP, acting either as storage for a single
 * literal, or as a node in a list. All {@code cells} are pairs of {@link Ref references}:
 * {@code first} (or {@code left}) and {@code rest} (or {@code right}). Both references are
 * <em>required</em>. The {@code first} reference can be a reference to an {@link Atom} or to the
 * root {@code cell} of a sub-list. The {@code rest} reference can be a reference to a {@code cell}
 * representing the next element in the list, or the {@code NIL} atom representing the end of the
 * list. A reference can refer to an {@link Atom} or another {@code Cell}.
 * <p>
 * Cells can also be used as "pure storage": as just a holder for a value. In a storage-only cell,
 * the {@code first} reference is the value; the {@code rest} reference is undefined (but typically
 * {@code null}).
 * <p>
 * The type of value held in a cell's {@code first} element can be determined by the {@code isAtom()},
 * {@code isList()} and {@code isNil()} methods.
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
 * <p><strong>NIL</strong></p>
 * {@code NIL} is a LISP language concept, and play the interesting double role of being both an
 * atom and an <em>empty</em> list. From the Stack Overflow article linked in the README:
 * "In Lisp languages that aren't spectacular cluster-fumbles of this sort, empty lists are atoms,
 * and non-empty lists are binary cells with a field which holds the first item, and another field
 * that holds the rest of the list."
 * <p>
 * In general, when {@code NIL} appears as the first element in a cell, it is interpreted as the
 * {@code NIL} atom and as the empty list. When it appears as the second element in a cell, it is
 * interpreted as the end of a list.
 * <p>
 * So, valid (and invalid) cells include:<ul>
 *     <li>{@code (ATOM . NIL)} - A terminal list node whose value is an atom.</li>
 *     <li>{@code (LIST . NIL} - A terminal list node whose value is a list.</li>
 *     <li>{@code (NIL . NIL)} - An empty list.</li>
 *     <li>{@code (ATOM . <Cell>)} - A non-terminal list node whose value is an atom.</li>
 *     <li>{@code (LIST . <Cell>)} - A non-terminal list node whose value is a list.</li>
 *     <li>{@code (NIL . <Cell>>)} - Invalid.</li>
 * </ul>
 */
public class Cell implements Ref {
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
     * Indicates if this cell is being used purely for storage. If so, only the "first" reference
     * is valid; the "rest" reference is undefined.
     */
    private final boolean isStorage;

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
        Validate.isInstanceOf(Atom.class, ref);
        return new Cell(ref);
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
     * Creates a new {@link Cell} with {@code NIL} references, signifying an empty list.
     * @return A new {@code Cell} representing an empty list, of the form {@code (NIL . NIL)}.
     */
    public static Cell create() {
        return new Cell(NIL, NIL);
    }

    /**
     * Private constructor. Use one of the {@code create} methods to create a new Cell.
     *
     * @param first The new Cell's {@code first} element.
     * @param rest The new Cell's {@code rest} element.
     */
    private Cell(final Ref first, final Ref rest) {
        Objects.requireNonNull(first);
        this.first = first;
        this.rest = rest;
        this.isStorage = false;
    }

    /**
     * Private constructor for making a storage-only cell. Use one of the {@code create} methods to
     * create a new Cell.
     *
     * @param first The new Cell's {@code first} element.
     */
    private Cell(final Ref first) {
        Objects.requireNonNull(first);
        this.first = first;
        this.rest = null;
        this.isStorage = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cell toCell() {
        return this;
    }

    /**
     * Indicates if this {@link Cell} is a storage-only {@code Cell}.
     * @return True if this {@code Cell} is storage-only, false otherwise.
     */
    public boolean isStorage() {
        return this.isStorage;
    }

    /**
     * Indicates if this {@link Cell} is pure storage for an {@link Atom}.
     * @return True if this {@code Cell} is pure storage for an {@code Atom}, false otherwise.
     */
    public boolean isAtom() {
        return isNil() || (first instanceof Atom && rest == null);
    }

    /**
     * Returns this cell's {@code first} value as an {@link Atom}.
     * @return This cell's {@code first} value as an {@link Atom}.
     */
    public Atom toAtom() {
        return Atom.create(first);
    }

    /**
     * Indicates if this {@link Cell Cell's} {@code first} element is a {@link List}.
     * @return True if this {@code Cell's} {@code first} element is a {@code List}, false
     *         otherwise.
     */
    @Override
    public boolean isList() {
        // If the first reference is nil, it's an empty list.
        return isNil() || (first instanceof Cell);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List toList() {
        return List.create(first);
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
     * Returns the {@code first} field of this {@link Cell} as a {@link Ref}.
     * @return The {@code first} field of this {@code Cell}.
     */
    public Ref getFirst() {
        return this.first;
    }

    /**
     * Sets the {@code car} (first/lhs) element of this {@link} cell to the {@link Atom}, Symbol
     * or List represented by the given {@link Ref}.
     * @param ref A non-null {@link Ref}.
     */
    public void setFirst(final Ref ref) {
        Objects.requireNonNull(ref);
        this.first = ref;
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
        Objects.requireNonNull(ref);
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
