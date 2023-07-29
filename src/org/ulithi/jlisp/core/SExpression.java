package org.ulithi.jlisp.core;

import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.Ref;

/**
 * An {@link SExpression} is the fundamental LISP language element, the basis for both
 * {@link Atom Atoms} (literals and symbols) and {@link List Lists}.
 */
public abstract class SExpression {

    /**
     * Creates and returns a new {@link SExpression}. The subtype depends on the given
     * {@link Cell}, according to the following rules:<ul>
     *     <li>If cell.first() is NIL, return NIL atom</li>
     *     <li>If cell.first() is an atom and cell.rest() is NIL, return the atom</li>
     *     <li>Else, return a list with the given cell as the root</li>
     * </ul>
     *
     * @param cell A {@link Cell}, which may represent an {@link Atom} or a {@link List}.
     * @return An {@link Atom} or {@link List} as represented by the cell.
     */
    public static SExpression create(final Cell cell) {
        assert cell != null: "Cell is null";
        if (cell.isNil()) { return Atom.NIL; }
        if (cell.getFirst().isAtom() && cell.getRest().isNil()) { return (Atom)cell.getFirst(); }
        return List.create(cell);
    }

    /**
     * Creates and returns a new {@link SExpression}. The subtype depends on the given
     * {@link Ref}, according to the following rules:<ul>
     *     <li>If cell.first() is NIL, return NIL atom</li>
     *     <li>If cell.first() is an atom and cell.rest() is NIL, return the atom</li>
     *     <li>Else, return a list with the given cell as the root</li>
     * </ul>
     *
     * @param ref A {@link Ref}, which may represent an {@link Atom} or a {@link List}.
     * @return An {@link Atom} or {@link List} as represented by the {@code Ref}.
     */
    public static SExpression create(final Ref ref) {
        assert ref != null: "Ref is null";
        if (ref.isNil()) { return Atom.NIL; }
        if (ref.isAtom()) { return (Atom)ref; }
        if (ref.isList()) { return List.create((Cell)ref); }
        return List.create((Cell)ref);
    }

    /**
     * Indicates if this {@link SExpression} is an {@link Atom}.
     * @return True if this s-expression is an {@code Atom}, false otherwise.
     */
    public abstract boolean isAtom();

    /**
     * Indicates if this {@link SExpression} is a {@link List}.
     * @return True if this is a reference to a {@code List}, false otherwise.
     */
    public abstract boolean isList();
}
