package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

public class TIMES implements Function{
    @Override
    public SExpression apply(final SExpression sexp) {
        if (!sexp.isList()) {
            throw new EvaluationException("List argument expected");
        }

        List args = sexp.toList();

        int result = 1;

        do {
            result *= ((Atom)args.car()).toI();
            if (args.endp()) break;
            args = args.cdr().toList();
        } while (true);

        return Atom.create(result);
    }
}
