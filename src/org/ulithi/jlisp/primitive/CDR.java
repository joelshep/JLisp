package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.mem.Cell;

/**
 * Implements the LISP {@code CDR} function. The {@code CDR} function accepts a list and returns
 * the list except for the first item. If the list is a single element list, {@code CDR} returns
 * {@code NIL}.
 */
public class CDR implements Function {
    @Override
    public SExpression apply (final SExpression sexp) {
        if (sexp.isNil()) { return Atom.NIL; }
        if (sexp.isList()) { return ((List)sexp).toList().cdr(); }

        //return SExpression.create(cell.getRest());
        throw new EvaluationException("Argument to CDR must be a list");
    }
}
