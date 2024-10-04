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
        if (ref.isNil()) { return Atom.NIL; }
        if (ref.isAtom()) { return ref.toAtom(); }
        return List.create(ref);
    }

    /**
     * Recursively determines equality (as defined for the {@code EQUAL} function) of this
     * {@link SExpression SExpression} and the given {@code SExpression}. Two {@code SExpressions}
     * are considered isomorphic if they are identical {@code Atoms} or isomorphic {@code Lists}.
     *
     * @param rhs The {@code SExpression} to compare.
     * @return True if this and the given {@code SExpression} are isomorphic, false otherwise.
     */
    default boolean isEqual(final SExpression rhs) {
        if (this.isAtom() && rhs.isAtom()) {
            return this.toAtom().eql(rhs.toAtom());
        }

        if (!(this.isList() && rhs.isList())) {
            return false;
        }

        return listEqual(this.toList(), rhs.toList());
    }

    /**
     * Recursively determines equality (as defined for the {@code EQUAL} function) of two
     * {@link List Lists}. Two {@code Lists} are considered isomorphic if they are the same
     * length and contain the same elements in the same order.
     *
     * @param lhs The {@code List} to compare to.
     * @param rhs The {@code List} to compare.
     * @return True if the two {@code Lists} are isomorphic, false otherwise.
     */
    private static boolean listEqual(final List lhs, final List rhs) {
        if (lhs.isEmpty() && rhs.isEmpty()) {
            return true;
        }

        if (lhs.lengthAsInt() != rhs.lengthAsInt()) {
            return false;
        }

        return lhs.car().isEqual(rhs.car()) && lhs.cdr().isEqual(rhs.cdr());
    }
}
