package org.ulithi.jlisp.test;

import org.ulithi.jlisp.core.BindableFunction;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.Environment;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.WrongArgumentCountException;
import org.ulithi.jlisp.primitive.Eval;

/**
 * Built-in functions to support simple unit-testing of JLisp itself.
 */
public class Expect implements BindingProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<Binding> getBindings() {
        return java.util.List.of(new Binding(new EXPECT()),
                                 new Binding(new THEN()),
                                 new Binding(new WHEN()));
    }

    /**
     * Implements the non-standard {@code EXPECT} function, which is intended to support unit
     * test functionality. EXPECT accepts a LISP expression to evaluate, and a second expression
     * representing the expected output of the first expression. If the output matches the second
     * expression, returns T. Otherwise, writes a warning to STDERR and returns F.
     */
    public static class EXPECT extends BindableFunction {
        public EXPECT() { super("EXPECT"); }

        @Override
        public boolean isSpecial() { return true; }

        @Override
        public boolean isReentrant() { return true; }

        @Override
        public SExpression apply(SExpression sexp, Environment environment, Eval eval) {
            List it = sexp.toList();

            if (it.lengthAsInt() != 2) {
                throw new WrongArgumentCountException("Expected 2 arguments: received " + it.length());
            }

            final SExpression lhs = it.nth(0);
            final SExpression rhs = it.nth(1);

            final SExpression actual = eval.eval(lhs);
            final SExpression expected = eval.eval(rhs);

            if (actual.isEqual(expected)) {
                return Atom.T;
            } else {
                System.err.println("Expected " + expected + ", got " + actual);
                return Atom.F;
            }
        }
    }

    /**
     * Implements the non-standard {@code THEN} function, which is intended to support unit
     * test functionality. THEN accepts no arguments: its only role is to erase all user-defined
     * functions and symbols from the environment, which were presumably created by a
     * preceding {@link WHEN} function.
     */
    public static class THEN extends BindableFunction {
        public THEN() { super("THEN"); }

        @Override
        public boolean isReentrant() { return true; }

        @Override
        public SExpression apply(SExpression sexp, Environment environment, Eval eval) {
            environment.reset();
            return Atom.T;
        }
    }

    /**
     * Implements the non-standard {@code WHEN} function, which is intended to support unit
     * test functionality. WHEN accepts a LISP expression to evaluate, evaluates it and returns
     * T. {@code WHEN} is primarily to set up pre-conditions -- e.g., initializing variables,
     * defining functions  -- for following {@code EXPECT} expressions.
     */
    public static class WHEN extends BindableFunction {
        public WHEN() { super("WHEN"); }

        @Override
        public boolean isSpecial() { return true; }

        @Override
        public boolean isReentrant() { return true; }

        @Override
        public SExpression apply(SExpression sexp, Environment environment, Eval eval) {
            eval.eval(sexp.toList().car());
            return Atom.T;
        }
    }
}
