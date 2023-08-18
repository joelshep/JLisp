package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

/**
 * A collection of integer math functions.
 */
public class Math {

    /**
     * The {@code PLUS} function, a.k.a. {@code +}.
     */
    public static final class PLUS implements Function {
        @Override
        public SExpression apply(final SExpression sexp) {
            if (!sexp.isList()) {
                throw new EvaluationException("List argument expected");
            }

            List args = sexp.toList();
            int result = 0;

            do {
                result += ((Atom)args.car()).toI();
                if (args.endp()) break;
                args = args.cdr().toList();
            } while (true);

            return Atom.create(result);
        }
    }

    /**
     * The {@code MINUS} function.
     */
    public static final class MINUS implements Function {
        @Override
        public SExpression apply(final SExpression sexp) {
            if (!sexp.isList()) {
                throw new EvaluationException("List argument expected");
            }

            List args = sexp.toList();
            int result = ((Atom)args.car()).toI();

            while (!args.endp()) {
                args = args.cdr().toList();
                result -= ((Atom)args.car()).toI();
            }

            return Atom.create(result);
        }
    }

    /**
     * The {@code TIMES} function, a.k.a. {@code *}.
     */
    public static class TIMES implements Function {
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
}
