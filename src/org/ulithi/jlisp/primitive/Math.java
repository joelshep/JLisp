package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Bindable;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

import java.util.Arrays;

/**
 * A collection of integer math functions.
 */
public class Math implements BindingProvider {

    /** {@inheritDoc} */
    @Override
    public java.util.List<Bindable> getBindings() {
        return Arrays.asList(new GREATER(),
                             new LESS(),
                             new MINUS(),
                             new PLUS(),
                             new QUOTIENT(),
                             new TIMES());
    }

    /**
     * The {@code LESS} function, a.k.a. {@code <}. The value of {@code <} is {@code T} (true) if
     * the numbers are in strictly increasing order; otherwise it is {@code F} (false).
     */
    public static final class LESS extends AbstractFunction {
        public LESS() { super("<"); }

        private static final BinaryArithmeticOperator<Boolean> op =
                (lhs, rhs) -> lhs < rhs;

        @Override
        public SExpression apply(final SExpression sexp) {
            List it = toList(sexp);
            int last = Integer.MIN_VALUE;
            boolean result = true;

            while (it.length().toI() > 0) {
                final int next = it.car().toAtom().toI();
                result = op.eval(last, next);
                if (!result) { break; }
                last = next;
                it = it.cdr().toList();
            }

            return Atom.create(result);
        }
    }

    /**
     * The {@code GREATER} function, a.k.a. {@code >}. The value of {@code >} is {@code T} (true)
     * if the numbers are in strictly decreasing order; otherwise it is {@code F} (false).
     */
    public static final class GREATER extends AbstractFunction {
        public GREATER() { super(">"); }

        private static final BinaryArithmeticOperator<Boolean> op =
                (lhs, rhs) -> lhs > rhs;

        @Override
        public SExpression apply(final SExpression sexp) {
            List it = toList(sexp);
            int last = Integer.MAX_VALUE;
            boolean result = true;

            while (it.length().toI() > 0) {
                final int next = it.car().toAtom().toI();
                result = op.eval(last, next);
                if (!result) { break; }
                last = next;
                it = it.cdr().toList();
            }

            return Atom.create(result);
        }
    }

    /**
     * The {@code PLUS} function, a.k.a. {@code +}.
     */
    public static final class PLUS extends AbstractFunction {
        public PLUS() { super("PLUS"); }

        @Override
        public String[] synonyms() { return new String[]{ "+" }; }

        private static final BinaryArithmeticOperator<Integer> op =
                (lhs, rhs) -> lhs + rhs;

        @Override
        public SExpression apply(final SExpression sexp) {
            final int result = Math.applyNumericVarArgsOperator(toList(sexp), op);
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

        private static final BinaryArithmeticOperator<Integer> op =
                (lhs, rhs) -> lhs - rhs;

        @Override
        public SExpression apply(final SExpression sexp) {

            // When invoked with a single argument, MINUS returns the
            // negation of its argument.
            List args = toList(sexp);
            final int first = args.car().toAtom().toI();

            if (args.endp()) { return Atom.create(-first); }

            final int result = Math.applyNumericVarArgsOperator(sexp.toList(), op);
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

        private static final BinaryArithmeticOperator<Integer> op =
                (lhs, rhs) -> lhs * rhs;

        @Override
        public SExpression apply(final SExpression sexp) {
            final int result = Math.applyNumericVarArgsOperator(toList(sexp), op);
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

        private static final BinaryArithmeticOperator<Integer> op =
                (lhs, rhs) -> lhs / rhs;

        @Override
        public SExpression apply(final SExpression sexp) {
            final int result = Math.applyNumericVarArgsOperator(toList(sexp), op);
            return Atom.create(result);
        }
    }

    /**
     * Applies the given binary operator to the given arguments and returns the result. The
     * operator is applied to arguments from the beginning of the list to the end. The first
     * evaluation is simply the value of the first element in the list. Successive evaluations
     * are on the previous result and the next element in the list.
     *
     * @param args A list of Atoms: the arguments to the operator.
     * @param op A binary operator to apply to the arguments.
     * @return The result of applying the operator to the arguments.
     */
    private static int applyNumericVarArgsOperator(final List args,
                                                   final BinaryArithmeticOperator<Integer>  op) {
        List it = args;
        int result = it.car().toAtom().toI();

        while (!it.endp()) {
            it = it.cdr().toList();
            result = op.eval(result, it.car().toAtom().toI());
        }

        return result;
    }

    /**
     * Converts the given SExpression to a List.
     *
     * @param sexp An SExpression.
     * @return The given SExpression as a List.
     */
    private static List toList(final SExpression sexp) {
        if (!sexp.isList()) { throw new EvaluationException("List argument expected"); }
        return sexp.toList();
    }
}
