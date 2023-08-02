package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

public class TIMES implements Function{
    @Override
    public SExpression apply(final SExpression sexp) {
        if (sexp.isNil() || !sexp.isList()) {
            throw new EvaluationException("List argument expected");
        }

        List args = sexp.toList();

        int result = ((Atom)args.car()).toI();

        while (!args.cdr().isNil()) {
            args = args.cdr().toList();
            result *= ((Atom) args.car()).toI();
        }

        return Atom.create(result);
    }
}
