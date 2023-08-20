package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

import java.util.Arrays;

/**
 * A collection of integer math functions.
 */
public class Math implements FunctionProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<Function> getFunctions() {
        return Arrays.asList(new MINUS(),
                             new PLUS(),
                             new QUOTIENT(),
                             new TIMES());
    }

    /**
     * The {@code PLUS} function, a.k.a. {@code +}.
     */
    public static final class PLUS extends AbstractFunction {
        public PLUS() { super("PLUS"); }

        @Override
        public String[] synonyms() { return new String[]{ "+" }; }

        @Override
        public SExpression apply(final SExpression sexp) {
            if (!sexp.isList()) {
                throw new EvaluationException("List argument expected");
            }

            List args = sexp.toList();
            int result = ((Atom)args.car()).toI();

            while (!args.endp()) {
                args = args.cdr().toList();
                result += ((Atom)args.car()).toI();
            }

            return Atom.create(result);
        }
    }

    /**
     * The {@code MINUS} function.
     */
    public static final class MINUS extends AbstractFunction {
        public MINUS() { super("MINUS"); }

        @Override
        public String[] synonyms() { return new String[]{ "-" }; }

        @Override
        public SExpression apply(final SExpression sexp) {
            if (!sexp.isList()) {
                throw new EvaluationException("List argument expected");
            }

            List args = sexp.toList();
            int result = ((Atom)args.car()).toI();

            // When invoked with a single argument, MINUS returns the
            // negation of its argument.
            if (args.endp()) {
                return Atom.create(-result);
            }

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
    public static class TIMES extends AbstractFunction {
        public TIMES() { super("TIMES"); }

        @Override
        public String[] synonyms() { return new String[]{ "*" }; }

        @Override
        public SExpression apply(final SExpression sexp) {
            if (!sexp.isList()) {
                throw new EvaluationException("List argument expected");
            }

            List args = sexp.toList();
            int result = ((Atom)args.car()).toI();

            while (!args.endp()) {
                args = args.cdr().toList();
                result *= ((Atom)args.car()).toI();
            }

            return Atom.create(result);
        }
    }

    /**
     * The {@code QUOTIENT} function, a.k.a. division.
     */
    public static class QUOTIENT extends AbstractFunction {
        public QUOTIENT() { super("QUOTIENT"); }

        @Override
        public String[] synonyms() { return new String[]{ "/" }; }

        @Override
        public SExpression apply(final SExpression sexp) {
            if (!sexp.isList()) {
                throw new EvaluationException("List argument expected");
            }

            List args = sexp.toList();
            int result = ((Atom)args.car()).toI();

            while (!args.endp()) {
                args = args.cdr().toList();
                result /= ((Atom)args.car()).toI();
            }

            return Atom.create(result);
        }
    }
}
