package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

import java.util.Arrays;

/**
 * A collection of general utility functions.
 */
public class Util implements FunctionProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<Function> getFunctions() {
        return Arrays.asList(new LENGTH());
    }

    /**
     * Implements the LISP {@code LENGTH} function, which returns the number of top-level elements in
     * a given list. If the list is empty/NIL, returns 0. Throws if the given {@code sexpr} is not a
     * list.
     */
    public static class LENGTH extends AbstractFunction {
        public LENGTH() { super("LENGTH");}
        /**
         * {@inheritDoc}
         */
        @Override
        public SExpression apply(final SExpression sexp) {
            if (sexp.isList()) { return ((List)sexp).length(); }
            throw new EvaluationException("Argument to LENGTH must be a list");
        }
    }
}
