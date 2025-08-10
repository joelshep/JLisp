package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.BindableFunction;
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
        return Arrays.asList(new Binding(new EQL()),
                             new Binding(new EQUAL()));
    }

    /**
     * Implements the LISP {@code EQL} function, which is essentially the same as the Java
     * {@code equals()} operator: literal elements are equal if they are the same type of value,
     * other elements are equal if they occupy the same memory, and otherwise they are not
     * equal.
     */
    public static class EQL extends BindableFunction {
        public EQL() { super("EQL"); }

        @Override
        public String[] synonyms() { return new String[]{ "=" }; }

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
                    // The effective address for a List is the address (object id) of its root cell.
                    result = (lhs.toList().getRoot() == rhs.toList().getRoot());
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
    public static class EQUAL extends BindableFunction {
        public EQUAL() { super("EQUAL"); }

        /**
         * {@inheritDoc}
         */
        @Override
        public SExpression apply(final SExpression sexp) {
            final Args args = Args.create(sexp).expectMinLength(2);

            boolean result = false;
            final SExpression lhs = args.takeAny();

            while (args.hasNext()) {
                final SExpression rhs = args.takeAny();
                result = lhs.isEqual(rhs);
                if (!result) {
                    break;
                }
            }

            return Atom.create(result);
        }
    }
}
