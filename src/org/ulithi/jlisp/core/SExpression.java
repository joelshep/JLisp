package org.ulithi.jlisp.core;

import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.Ref;

/**
 * An {@link SExpression} is the fundamental LISP language element, the basis for both
 * {@link Atom Atoms} (literals and symbols) and {@link List Lists}.
 */
public interface SExpression extends Ref, Bindable {
    /**
     * Creates and returns a new {@link SExpression}. The subtype depends on the given {@link Ref},
     * according to the following rules:<ul>
     *     <li>If ref is NIL, return the empty list</li>
     *     <li>If ref is an atom, return ref as an atom</li>
     *     <li>Else, return a new list with ref (as a cell) as the root</li>
     * </ul>
     *
     * @param ref A {@link Ref}, which may represent an {@link Atom} or a {@link List}.
     * @return An {@link Atom} or {@link List} as represented by the {@code Ref}.
     */
    static SExpression create(final Ref ref) {
        assert ref != null: "Ref is null";
        if (ref.isNil()) { return List.create(); }
        if (ref.isAtom()) { return (Atom)ref; }
        return List.create((Cell)ref);
    }
}
