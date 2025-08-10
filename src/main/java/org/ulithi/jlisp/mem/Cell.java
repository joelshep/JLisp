package org.ulithi.jlisp.mem;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Function;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.TypeConversionException;
import org.ulithi.jlisp.parser.Grammar;

import java.util.Objects;

import static org.ulithi.jlisp.mem.NilReference.NIL;

/**
 * {@link Cell Cells} are the basic unit of storage in JLISP. {@code Cells} are pairs of
 * {@link Ref references}: {@code first} (or {@code left}) and {@code rest} (or {@code right}). Both
 * references are <em>required</em>. Cells are similar to linked list nodes, but have one key
 * difference. Like a linked list node, the {@code first} reference in a cell can be a reference to
 * an {@link Atom}, {@code Symbol} or {@link Function}, or to the root {@code cell} of a sub-list:
 * i.e., it is the <em>value</em> held by the cell. The {@code rest} reference can be a reference to
 * another {@code cell} that is the next node in the list, or the {@code NIL} atom which marks the
 * end of the list, <em>or</em> to a non-nil {@code Atom}, {@code Symbol} or {@code Function}. A
 * cons cell is always the end of a list, even if it is the only cell in a list. Any cell that
 * ends with a reference to a non-cell marks the end of a list.
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
 * {@code Refs}, which they then need to handle appropriately, depending what type of reference it
 * is.
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
 *     <li>{@code (NIL . ATOM}} - A cons cell with NIL and an atom</li>.
 *     <li>{@code (ATOM . ATOM}) - A cons cell with two atoms.</li>
 *     <li>{@code (LIST . ATOM}) - A cons cell with a list and an atom.</li>
 * </ul>
 */
public class Cell implements Ref {

    /**
     * Special singleton {@link Cell} representing the NIL value. This is in part to help
     * ensure that any two empty lists are considered {@code EQL}.
     */
    public static final Cell NIL_CELL = new Cell(NIL, NIL);

    /**
     * The first/lhs field in this cell. This can be an Atom, Symbol, Function or a reference to a
     * Cell, which is interpreted as the head cell of a sub-list.
     */
    private Ref first;

    /**
     * The second/rhs field in this cell. This is typically either a reference to the next
     * Cell, or NIL. In a CONS cell, it may also be an Atom, Symbol or Function.
     */
    private Ref rest;

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
        this.first = first;
        this.rest = rest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cell toCell() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Function toFunction() {
        return null;  // TODO - This is suss.
    }

    /**
     * Indicates if this is the last {@link Cell} in a linked list of cells, as indicated by
     * its {@code rest} element being NIL or an SExpression (not a reference to the next
     * {@code Cell}).
     * @return True if this {@code Cell} terminates a linked list, false otherwise.
     */
    public boolean isTerminal() {
        return (rest.isNil() || !rest.isCell());
    }

    /**
     * Indicates if this {@link Cell Cell's} value is an {@link Atom}.
     * @return True if this {@code Cell's} value is an {@code Atom}, false otherwise.
     */
    public boolean hasAtom() {
        return isNil() || (first instanceof Atom);
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
    public boolean hasList() {
        // If the first reference is nil, it's an empty list.
        return isNil() || (first instanceof Cell);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List toList() {
        return List.create(this);
    }

    /**
     * Indicates if this {@link Cell} stores the special {@code NIL} value.
     * @return True if this {@code Cell} is a "NIL cell", false otherwise.
     */
    @Override
    public boolean isNil() {
        return first == NIL && rest == NIL;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: There is no deterministic conversion of a single Cell to an SExpression.
     * @return
     */
    @Override
    public SExpression toSExpression() {
        throw new TypeConversionException("Cannot convert Cell to SExpression");
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
