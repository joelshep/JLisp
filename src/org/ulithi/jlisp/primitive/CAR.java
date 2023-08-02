package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

/**
 * Implements the LISP {@code CAR} function. The {@code CAR} function accepts a list and returns
 * the first element in the list. If the list is a single element list, {@code CDR} returns
 * {@code NIL}.
 */
public class CAR implements Function {
    @Override
    public SExpression apply (final SExpression sexp) {
        if (sexp.isNil()) { return Atom.NIL; }
        if (sexp.isList()) { return ((List)sexp).toList().car(); }
        throw new EvaluationException("Argument to CAR must be a list");
    }
}
