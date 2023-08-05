package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

/**
 * Implements the LISP {@code LENGTH} function, which returns the number of top-level elements in
 * a given list. If the list is empty/NIL, returns 0. Throws if the given {@code sexpr} is not a
 * list.
 */
public class LENGTH implements Function{
    /**
     * {@inheritDoc}
     */
    @Override
    public SExpression apply(final SExpression sexp) {
        if (sexp.isNil()) { return Atom.create(0); }
        if (sexp.isList()) { return ((List)sexp).length(); }

        throw new EvaluationException("Argument to LENGTH must be a list");
    }
}
