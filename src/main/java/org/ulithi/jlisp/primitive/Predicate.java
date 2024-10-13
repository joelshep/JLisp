package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.AbstractFunction;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;

import java.util.Arrays;
import java.util.Optional;

/**
 * A collection of predicate functions.
 */
public class Predicate implements BindingProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<Binding> getBindings() {
        return Arrays.asList(new Binding(new Predicate.ATOM()),
                             new Binding(new Predicate.INTEGERP()),
                             new Binding(new Predicate.MINUSP()),
                             new Binding(new Predicate.PLUSP()),
                             new Binding(new Predicate.ZEROP()));
    }

    /**
     * Implements the LISP {@code ATOM} function. The {@code ATOM} function accepts a value and
     * returns true if the value is an atom, false otherwise.
     */
    public static class ATOM extends AbstractFunction {
        public ATOM() { super("ATOM"); }
        @Override
        public SExpression apply(final SExpression sexp) {
            final List args = sexp.toList();

            if (args.lengthAsInt() == 1 && args.car().isAtom()) {
                return Atom.T;
            }

            return Atom.F;
        }
    }

    /**
     * Implements the LISP {@code INTEGERP} function. The {@code INTEGERP} function accepts a value
     * and returns true if the value is an integer, false otherwise.
     */
    public static class INTEGERP extends AbstractFunction {
        public INTEGERP() { super("INTEGERP"); }

        @Override
        public SExpression apply(final SExpression sexp) {
            final List args = checkArgs(sexp, 1);
            return isInteger(args.car()) ? Atom.T : Atom.F;
        }
    }

    /**
     * Indicates if the given SExpression represents an integer value.
     * @param sexp An SExpression.
     * @return True if sexp is a numeric atom, false otherwise.
     */
    private static boolean isInteger(final SExpression sexp) {
        return sexp.isAtom() && sexp.toAtom().isNumber();
    }

    /**
     * Implements the LISP {@code MINUSP} function. The {@code MINUSP} function accepts an
     * integer value and returns true if the value is strictly negative, false otherwise.
     */
    public static class MINUSP extends AbstractFunction {
        public MINUSP() { super("MINUSP"); }

        @Override
        public SExpression apply(final SExpression sexp) {
            return evaluateNumericPredicate(sexp, n -> n < 0);
        }
    }

    /**
     * Implements the LISP {@code PLUSP} function. The {@code PLUSP} function accepts an
     * integer value and returns true if the value is strictly positive, false otherwise.
     */
    public static class PLUSP extends AbstractFunction {
        public PLUSP() { super("PLUSP"); }

        @Override
        public SExpression apply(final SExpression sexp) {
            return evaluateNumericPredicate(sexp, n -> n > 0);
        }
    }

    /**
     * Implements the LISP {@code ZEROP} function. The {@code ZEROP} function accepts an
     * integer value and returns true if the value is zero, false otherwise.
     */
    public static class ZEROP extends AbstractFunction {
        public ZEROP() { super("ZEROP"); }

        @Override
        public SExpression apply(final SExpression sexp) {
            return evaluateNumericPredicate(sexp, n -> n == 0);
        }
    }

    /**
     * Validates the given SExpression is a List containing a single integer, and applies the
     * given predicate to the integer.
     * @param sexp An SExpression expected to be a single-element integer list.
     * @param predicate An integer predicate.
     * @return A Boolean-values Atom indicating if the integer element of sexp meets the
     *         condition specified by the predicate.
     */
    private static SExpression evaluateNumericPredicate(
            final SExpression sexp,
            final java.util.function.Predicate<Integer> predicate) {
        final List args = checkArgs(sexp, 1);
        final int value = getIntegerArgument(args);
        return Atom.create(predicate.test(value));
    }

    /**
     * Converts and returns the given SExpression to a list of the specified length, or throws
     * a WrongArgumentCountException if the resulting List is not of the specified length.
     *
     * @param sexp An SExpression representing a list.
     * @param length The expected length of the list.
     * @return A List representation of the give SExpression.
     */
    private static List checkArgs(final SExpression sexp, final int length) {
        final List args = sexp.toList();

        if (args.lengthAsInt() != length) {
            throw new WrongArgumentCountException(
                    "Expected " + length + "arguments, received " + args.lengthAsInt());
        }

        return args;
    }

    /**
     * Attempts to convert the first element of the given list to an integer.
     * @param args A List, assumed to be single element.
     * @return An integer, converted from the single element of the list.
     */
    private static Integer getIntegerArgument(final List args) {
        return Optional.of(args.car())
                .filter(SExpression::isAtom)
                .map(SExpression::toAtom)
                .filter(Atom::isNumber)
                .map(Atom::toI)
                .orElseThrow(() -> new EvaluationException("Expected a numeric argument"));
    }
}
