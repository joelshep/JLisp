package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.AbstractFunction;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.WrongArgumentCountException;

import java.util.Arrays;

/**
 * A collection of general utility functions.
 */
public class Util implements BindingProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<Binding> getBindings() {
        return Arrays.asList(new Binding(new Util.EQL()),
                             new Binding(new Util.EQUAL()));
    }

    /**
     * Implements the LISP {@code EQL} function, which is essentially the same as the Java
     * {@code equals()} operator: literal elements are equal if they are the same type of value,
     * other elements are equal if they occupy the same memory, and otherwise they are not
     * equal.
     */
    public static class EQL extends AbstractFunction {
        public EQL() { super("EQL"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            List it = sexp.toList();

            if (it.lengthAsInt() < 2) {
                throw new WrongArgumentCountException("Expected 2 or more arguments: received " + it.length());
            }

            boolean result = false;
            final SExpression lhs = it.car();

            while (!it.endp()) {
                it = it.cdr().toList();
                final SExpression rhs = it.car();
                if (lhs.isAtom() && rhs.isAtom()) {
                    result = lhs.toAtom().eql(rhs.toAtom());
                } else if (lhs.isList() && rhs.isList()) {
                    result = lhs.toList().isEmpty() && rhs.toList().isEmpty();
                } else {
                    result = (lhs == it.car());
                }

                if (!result) { break; }
            }

            return Atom.create(result);
        }
    }

    /**
     * Implements the LISP {@code EQUAL} function, which returns true if its arguments are
     * structurally similar (isomorphic). A rough rule of thumb is that two objects are equal
     * if and only if their printed representations are the same.
     */
    public static class EQUAL extends AbstractFunction {
        public EQUAL() {
            super("EQUAL");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SExpression apply(final SExpression sexp) {
            List it = sexp.toList();

            if (it.lengthAsInt() < 2) {
                throw new WrongArgumentCountException("Expected 2 or more arguments: received " + it.length());
            }

            boolean result = false;
            final SExpression lhs = it.car();

            while (!it.endp()) {
                it = it.cdr().toList();
                result = isEqual(lhs, it.car());
                if (!result) {
                    break;
                }
            }

            return Atom.create(result);
        }

        /**
         * Recursively determines equality (as defined for the {@code EQUAL} function) of two
         * {@link SExpression SExpressions}. Two {@code SExpressions} are considered isomorphic
         * if they are identical {@code Atoms} or isomorphic {@code Lists}.
         *
         * @param lhs The {@code SExpression} to compare to.
         * @param rhs The {@code SExpression} to compare.
         * @return True if the two {@code SExpressions} are isomorphic, false otherwise.
         */
        private static boolean isEqual(final SExpression lhs, final SExpression rhs) {
            if (lhs.isAtom() && rhs.isAtom()) {
                return lhs.toAtom().eql(rhs.toAtom());
            }

            if (!(lhs.isList() && rhs.isList())) {
                return false;
            }

            return listEqual(lhs.toList(), rhs.toList());
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

            return isEqual(lhs.car(), rhs.car()) && isEqual(lhs.cdr(), rhs.cdr());
        }
    }
}
