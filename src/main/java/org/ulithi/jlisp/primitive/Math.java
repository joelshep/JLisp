package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.BindableFunction;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

import java.util.Arrays;

/**
 * A collection of integer math functions.
 * <p>
 * Note that the basic arithmetic operators -- PLUS, MINUS, QUOTIENT and TIMES -- are a little
 * nuanced. For PLUS and TIMES -- commutative operators -- applying them to an empty list returns
 * the respective additive or multiplicative identity value (0 or 1). This is a simplifying
 * assumption that makes it possible to treat empty and non-empty series identically. Similarly,
 * MINUS and QUOTIENT assume an implicit argument of their "identity" values (0 or 1), so that
 * when invoked with a single argument they return the argument: e.g. (QUOTIENT 4) is the same as
 * (QUOTIENT 4 1) ... i.e., 4.
 */
public class Math implements BindingProvider {

    public static final Atom ZERO = Atom.create(0);

    public static final Atom ONE = Atom.create(1);

    /** {@inheritDoc} */
    @Override
    public java.util.List<Binding> getBindings() {
        return Arrays.asList(new Binding(new GREATER()),
                             new Binding(new LESS()),
                             new Binding(new MINUS()),
                             new Binding(new PLUS()),
                             new Binding(new QUOTIENT()),
                             new Binding(new REMAINDER()),
                             new Binding(new TIMES()));
    }

    /**
     * The {@code LESS} function, a.k.a. {@code <}. The value of {@code <} is {@code T} (true) if
     * the numbers are in strictly increasing order; otherwise it is {@code F} (false).
     */
    public static final class LESS extends BindableFunction {
        public LESS() { super("<"); }

        private static final BinaryArithmeticOperator<Boolean> op =
                (lhs, rhs) -> lhs < rhs;

        @Override
        public SExpression apply(final SExpression sexp) {
            List it = sexp.toList();
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
    public static final class GREATER extends BindableFunction {
        public GREATER() { super(">"); }

        private static final BinaryArithmeticOperator<Boolean> op =
                (lhs, rhs) -> lhs > rhs;

        @Override
        public SExpression apply(final SExpression sexp) {
            List it = sexp.toList();
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
     * The {@code PLUS} function, a.k.a. {@code +}. Note that (+) => 0.
     */
    public static final class PLUS extends BindableFunction {
        public PLUS() { super("PLUS"); }

        @Override
        public String[] synonyms() { return new String[]{ "+" }; }

        private static final BinaryArithmeticOperator<Integer> op =
                (lhs, rhs) -> lhs + rhs;

        @Override
        public SExpression apply(final SExpression sexp) {
            final int result = Math.applyNumericVarArgsOperator(sexp.toList().add(ZERO), op);
            return Atom.create(result);
        }
    }

    /**
     * The {@code MINUS} function. When invoked with a single argument, MINUS returns the
     *  negation of its argument.
     */
    public static final class MINUS extends BindableFunction {
        public MINUS() { super("MINUS"); }

        @Override
        public String[] synonyms() { return new String[]{ "-" }; }

        private static final BinaryArithmeticOperator<Integer> op =
                (lhs, rhs) -> lhs - rhs;

        @Override
        public SExpression apply(final SExpression sexp) {
            final Args args = Args.create(sexp).expectMinLength(1);
            final int first = args.peekAtom().toI();

            if (args.length() == 1) {
                return Atom.create(-first);
            }

            return Atom.create(Math.applyNumericVarArgsOperator(args.toList(), op));
        }
    }

    /**
     * The {@code TIMES} function, a.k.a. {@code *}. Note that (*) => 1.
     */
    public static class TIMES extends BindableFunction {
        public TIMES() { super("TIMES"); }

        @Override
        public String[] synonyms() { return new String[]{ "*" }; }

        private static final BinaryArithmeticOperator<Integer> op =
                (lhs, rhs) -> lhs * rhs;

        @Override
        public SExpression apply(final SExpression sexp) {
            final int result = Math.applyNumericVarArgsOperator(sexp.toList().add(ONE), op);
            return Atom.create(result);
        }
    }

    /**
     * The {@code QUOTIENT} function, a.k.a. division. When invoked with a single argument,
     * QUOTIENT returns the argument (as if divided by the multiplicative identity 1 (one).
     */
    public static class QUOTIENT extends BindableFunction {
        public QUOTIENT() { super("QUOTIENT"); }

        @Override
        public String[] synonyms() { return new String[]{ "/" }; }

        private static final BinaryArithmeticOperator<Integer> op =
                (lhs, rhs) -> lhs / rhs;

        @Override
        public SExpression apply(final SExpression sexp) {
            final Args args = Args.create(sexp).expectMinLength(1);
            final int first = args.peekAtom().toI();

            if (args.length() == 1) {
                return Atom.create(first);
            }

            return Atom.create(Math.applyNumericVarArgsOperator(args.toList(), op));
        }
    }

    /**
     * The {@code REMAINDER} function, a.k.a. modulo.
     */
    public static class REMAINDER extends BindableFunction {
        public REMAINDER() { super("REMAINDER"); }

        @Override
        public String[] synonyms() { return new String[]{ "%" }; }

        private static final BinaryArithmeticOperator<Integer> op =
                (lhs, rhs) -> lhs % rhs;

        @Override
        public SExpression apply(final SExpression sexp) {
            final int result = Math.applyNumericVarArgsOperator(sexp.toList(), op);
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
                                                   final BinaryArithmeticOperator<Integer> op) {
        List it = args;

        try {
            int result = it.car().toAtom().toI();

            while (!it.endp()) {
                it = it.cdr().toList();
                result = op.eval(result, it.car().toAtom().toI());
            }

            return result;
        } catch (final ArithmeticException e) {
            throw new EvaluationException("Arithmetic exception: " + e.getMessage());
        }
    }
}
