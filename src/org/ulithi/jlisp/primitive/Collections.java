package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.AbstractFunction;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

import java.util.Arrays;

/**
 * Functions for working with "collections", such as lists.
 */
public class Collections implements BindingProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<Binding> getBindings() {
        return Arrays.asList(new Binding(new Collections.LENGTH()));
    }

    /**
     * Implements the LISP {@code LENGTH} function, which returns the number of top-level elements in
     * a given list. If the list is empty/NIL, returns 0. Throws if the given {@code sexpr} is not a
     * list.
     */
    public static class LENGTH extends AbstractFunction {
        public LENGTH() { super("LENGTH"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            final List args = sexp.toList();
            if (args.car().isList()) { return args.car().toList().length(); }
            throw new EvaluationException("Argument to LENGTH must be a list");
        }
    }
}
