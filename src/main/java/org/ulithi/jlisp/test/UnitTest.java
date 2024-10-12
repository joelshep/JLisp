package org.ulithi.jlisp.test;

import org.ulithi.jlisp.core.AbstractFunction;
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
public class UnitTest implements BindingProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<Binding> getBindings() {
        return java.util.List.of(new Binding(new EXPECT()));
    }

    /**
     * Implements the non-standard {@code EXPECT} function, which is intended to support unit
     * test functionality. EXPECT accepts a LISP expression to evaluate, and a second expression
     * representing the expected output of the first expression. If the output matches the second
     * expression, returns T. Otherwise, writes a warning to STDERR and returns F.
     */
    public static class EXPECT extends AbstractFunction {
        public EXPECT() {
            super("EXPECT");
        }

        @Override
        public boolean isSpecial() {
            return true;
        }

        @Override
        public boolean isReentrant() {
            return true;
        }

        @Override
        public SExpression apply(SExpression sexp, Environment environment, Eval eval) {
            List it = sexp.toList();

            if (it.lengthAsInt() != 2) {
                throw new WrongArgumentCountException("Expected 2 arguments: received " + it.length());
            }

            final SExpression lhs = it.car();
            final SExpression rhs = it.cdr();

            final SExpression actual = eval.apply(lhs);
            final SExpression expected = eval.apply(rhs);

            if (actual.isEqual(expected)) {
                return Atom.T;
            } else {
                System.err.println("Expected " + expected + ", got " + actual);
                return Atom.F;
            }
        }
    }
}
