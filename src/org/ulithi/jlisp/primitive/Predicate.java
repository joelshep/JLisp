package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Bindable;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;

import java.util.Arrays;

/**
 * A collection of predicate functions.
 */
public class Predicate implements BindingProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<Bindable> getBindings() {
        return Arrays.asList(new Predicate.ATOM(),
                             new Predicate.INTEGERP());
    }

    /**
     * Implements the LISP {@code ATOM} function. The {@code ATOM} function accepts a value and
     * returns true if the value is an atom, false otherwise.
     */
    public static class ATOM extends AbstractFunction {
        public ATOM() { super("ATOM"); }
        @Override
        public SExpression apply(final SExpression sexp) {
            final List args = sexp.toList();

            if (args.length().toI() == 1 && args.car().isAtom()) {
                return Atom.T;
            }

            return Atom.F;
        }
    }

    /**
     * Implements the LISP {@code ATOM} function. The {@code ATOM} function accepts a value and
     * returns true if the value is an atom, false otherwise.
     */
    public static class INTEGERP extends AbstractFunction {
        public INTEGERP() { super("INTEGERP"); }
        @Override
        public SExpression apply(final SExpression sexp) {
            final List args = sexp.toList();

            if (args.length().toI() == 1 &&
                args.car().isAtom() &&
                args.car().toAtom().isNumber()) {
                return Atom.T;
            }

            return Atom.F;
        }
    }
}
