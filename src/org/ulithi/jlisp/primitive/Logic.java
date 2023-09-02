package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

import java.util.Arrays;

/**
 * A collection of Boolean and logical functions.
 */
public class Logic implements FunctionProvider {

    /** {@inheritDoc} */
    @Override
    public java.util.List<Function> getFunctions() {
        return Arrays.asList(new Logic.T(),
                             new Logic.F());
    }

    public static class T extends AbstractFunction {
        public T() { super("T"); }

        /** {@inheritDoc} */
        public SExpression apply(final SExpression sexp) {
            if (sexp.isList() && sexp.toList().isEmpty()) {
                return Atom.T;
            }
            throw new EvaluationException("No arguments expected");
        }
    }

    public static class F extends AbstractFunction {
        public F() { super("F"); }

        /** {@inheritDoc} */
        public SExpression apply(final SExpression sexp) {
            if (sexp.isList() && sexp.toList().isEmpty()) {
                return Atom.F;
            }
            throw new EvaluationException("No arguments expected");
        }
    }
}
