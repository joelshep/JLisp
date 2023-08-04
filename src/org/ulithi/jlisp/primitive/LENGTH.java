package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

public class LENGTH implements Function{
    @Override
    public SExpression apply(SExpression sexp) {
        if (!sexp.isList()) {
            throw new EvaluationException("Argument to LENGTH must be a list");
        }

        return ((List)sexp).length();
    }
}
