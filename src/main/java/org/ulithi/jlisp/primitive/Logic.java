package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.AbstractSymbol;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.BindableFunction;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.Environment;
import org.ulithi.jlisp.core.SExpression;

import java.util.Arrays;
import java.util.List;

/**
 * A collection of Boolean and logical functions.
 */
public class Logic implements BindingProvider {

    /** {@inheritDoc} */
    @Override
    public List<Binding> getBindings() {
        return Arrays.asList(new Binding(new Logic.AND()),
                             new Binding(new Logic.NOT()),
                             new Binding(new Logic.OR()),
                             new Binding(new Logic.T()),
                             new Binding(new Logic.F()));
    }

    /**
     * Implements the LISP {@code AND} special function. Returns {@code NIL} if any argument
     * evaluates to {@code NIL} or {@code F}. If every argument evaluates to a non-{@code NIL}
     * value, returns the value of the last argument, or {@code T} if invoked without arguments.
     * {@code AND} evaluates its arguments from left to right and short-circuits evaluation: once an
     * argument which evaluates to {@code NIL} or {@code F} is encountered, no further arguments
     * are evaluated.
     */
    public static class AND extends BindableFunction {
        public AND() { super("AND"); }

        /** {@inheritDoc} */
        @Override
        public boolean isSpecial() { return true; }

        /** {@inheritDoc} */
        @Override
        public boolean isReentrant() { return true; }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp, final Environment env, final Eval eval) {
            final Args args = Args.create(sexp);
            SExpression value = Atom.T;

            while (args.hasNext()) {
                final SExpression arg = args.wantAny();
                value = eval.eval(arg);
                if (!t(value)) { return Atom.NIL; }
            }

            return value;
        }
    }

    /**
     * Implements the LISP {@code NOT} function, which returns {@code T} if its argument is NIL or
     * an {@code Atom} that is equivalent to {@link F}, or {@code F} otherwise.
     */
    public static class NOT extends BindableFunction {
        public NOT() { super("NOT"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            final Args args = Args.create(sexp).expect(1);
            return Atom.create(!t(args.wantAny()));
        }
    }

    /**
     * Implements the LISP {@code OR} special function. Returns {@code T} if any argument evaluates
     * to {@code T}. If every argument evaluates to a{@code NIL} or {@code F} value, returns the
     * value of the last argument, or {@code F} if invoked without arguments. {@code OR} evaluates
     * its arguments from left to right and short-circuits evaluation: once an argument which
     * evaluates to {@code T} is encountered, no further arguments are evaluated.
     */
    public static class OR extends BindableFunction {
        public OR() { super("OR"); }

        /** {@inheritDoc} */
        @Override
        public boolean isSpecial() { return true; }

        /** {@inheritDoc} */
        @Override
        public boolean isReentrant() { return true; }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp, final Environment env, final Eval eval) {
            final Args args = Args.create(sexp);
            SExpression value = Atom.NIL;

            while (args.hasNext()) {
                final SExpression arg = args.wantAny();
                value = eval.eval(arg);
                if (t(value)) { return Atom.T; }
            }

            return value;
        }
    }

    /**
     * A symbol representing a Boolean "true" value.
     */
    public static class T extends AbstractSymbol {
        public T() { super("T"); }

        /** {@inheritDoc} */
        @Override
        public SExpression eval() { return Atom.T; }
    }

    /**
     * A symbol representing a Boolean "false" value.
     */
    public static class F extends AbstractSymbol {
        public F() { super("F"); }

        /** {@inheritDoc} */
        @Override
        public SExpression eval() { return Atom.F; }
    }

    /**
     * Determines if the given {@link SExpression} represents a truth value: is either an
     * {@link Atom} that is equivalent to a Boolean True (see {@link Atom#toB()} or a non-NIL
     * {@link List}.
     * @param sexp An {@link SExpression}.
     * @return True if the given {@code SExpression} represents a truth value: false otherwise.
     */
    private static boolean t(final SExpression sexp) {
        return sexp.isAtom() ? sexp.toAtom().toB() : !sexp.isNil();
    }
}
