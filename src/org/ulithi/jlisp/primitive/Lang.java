package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;

import java.util.Arrays;

/**
 * Contains core LISP language functions.
 */
public class Lang implements FunctionProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<Function> getFunctions() {
        return Arrays.asList(new Lang.CAR(),
                             new Lang.CDR(),
                             new Lang.CONS(),
                             new Lang.QUOTE());
    }

    /**
     * Implements the LISP {@code CAR} function. The {@code CAR} function accepts a list and returns
     * the first element in the list. If the list is a single element list, {@code CDR} returns
     * {@code NIL}.
     */
    public static class CAR extends AbstractFunction {
        public CAR() { super("CAR"); }
        @Override
        public SExpression apply(final SExpression sexp) {
            if (sexp.isList()) {
                return sexp.toList().car();
            }
            throw new EvaluationException("Argument to CAR must be a list");
        }
    }

    /**
     * Implements the LISP {@code CDR} function. The {@code CDR} function accepts a list and returns
     * the list except for the first item. If the list is a single element list, {@code CDR} returns
     * {@code NIL}.
     */
    public static class CDR extends AbstractFunction {
        public CDR() { super("CDR"); }
        @Override
        public SExpression apply(final SExpression sexp) {
            if (sexp.isList()) {
                return sexp.toList().cdr();
            }
            throw new EvaluationException("Argument to CDR must be a list");
        }
    }

    /**
     * Implements the LISP {@code CONS} function. The {@code CONS} function accepts two arguments
     * and returns a {@link List} such that the {@code CAR} of the list is the first argument and the
     * {@code CDR} of the list is the second element.
     */
    public static class CONS extends AbstractFunction {
        public CONS() { super("CONS"); }

        @Override
        public SExpression apply(final SExpression sexp) {
            if (!sexp.isList()) { throw new EvaluationException("List argument expected"); }

            List args = sexp.toList();

            if (args.length().toAtom().toI() != 2) {
                throw new WrongArgumentCountException("Expected 2 arguments: received " + args.length());
            }

            final SExpression first = args.car();
            final SExpression rest = args.cdr();

            final List cons = List.create();

            do {
                final SExpression arg = args.car();
                if (arg.isAtom()) {
                    cons.add(arg.toAtom());
                } else if (arg.isList()) {
                    final List list = arg.toList();
                    if (!list.isEmpty()) {
                        cons.append(list);
                    }
                }

                if (args.endp()) break;
                args = args.cdr().toList();
            } while (true);

            return cons;
        }
    }

    /**
     * Implements the LISP {@code QUOTE} function. The {@code QUOTE} returns its arguments as-is, and is
     * therefore a "special" function. In modern LISP, the {@code '} token is shorthand for
     * {@code QUOTE}, but implementing it will require changes to the lexer, which I haven't done yet.
     */
    public static class QUOTE extends AbstractFunction {
        public QUOTE() { super("QUOTE");}
        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            if (sexp.isAtom()) { return sexp.toAtom(); }
            return sexp.toList().car();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSpecial() { return true; }
    }
}
