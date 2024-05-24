package org.ulithi.jlisp.core;

import org.ulithi.jlisp.mem.Ref;

/**
 * An {@link SExpression} is simply a "synonym" for a {@link Ref}, the only difference being
 * that {@code SExpressions} are used on the "language" side of the interpreter and {@code Refs}
 * are used in the memory model. Syntactic sugar ...
 * <p>
 * An {@code SExpression} can be an {@code Atom} or a {@code List}.
 */
public interface SExpression extends Ref {
    /**
     * "Downcasts" a {@link Ref} to an {@link SExpression}. The subtype depends on the given
     * {@code Ref}, according to the following rules:<ul>
     *     <li>If ref is NIL, return the empty list</li>
     *     <li>If ref is an atom, return ref as an atom</li>
     *     <li>Else, return a new list with ref (as a cell) as the root</li>
     * </ul>
     *
     * @param ref A {@link Ref}, which may represent an {@link Atom} or a {@link List}.
     * @return An {@link Atom} or {@link List} as represented by the {@code Ref}.
     */
    static SExpression fromRef(final Ref ref) {
        assert ref != null: "Ref is null";
        if (ref.isNil()) { return List.create(); }
        if (ref.isAtom()) { return ref.toAtom(); }
        return List.create(ref);
    }
}
