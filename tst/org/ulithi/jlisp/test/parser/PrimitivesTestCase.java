package org.ulithi.jlisp.test.parser;

import org.junit.Ignore;
import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.evaluate;

/**
 * Unit tests for the LISP primitives implemented in {@link org.ulithi.jlisp.parser.Primitives}.
 */
public class PrimitivesTestCase {
    @Test
    @Ignore
    public void evaluateCarExpression() {
        final SExpression sexp = evaluate("( CAR () )");
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
        //assertEquals("x", evaluate("( CAR () )"));
    }

    @Test
    public void evaluateCarConsExpression() {
        final String expression = "(CAR (CONS x y))";
        final String result = evaluate(expression).toAtom().toS();
        assertEquals("x", result);
    }

    @Test
    public void evaluateCdrConsExpression() {
        final String expression = "(CDR (CONS x y))";
        final String result = evaluate(expression).toList().toString();
        assertEquals("(y . NIL)", result);
    }

    @Test
    public void evaluateCdrConsListExpression() {
        final String expression = "(CDR (CONS x (PLUS 1 2)))";
        final String result = evaluate(expression).toList().toString();
        assertEquals("(3 . NIL)", result);
    }

    @Test
    public void evaluateConsExpression() {
        final String expression = "(CONS x y)";
        final String result = evaluate(expression).toList().toString();
        assertEquals("(x . (y . NIL))", result);
    }

    @Test
    public void evaluateLessExpression() {
        final String expression = "(< 2 3)";
        final boolean result = evaluate(expression).toAtom().toB();
        assertTrue(result);
    }

    @Test
    public void evaluateMathExpression() {
        final String expression = "(TIMES (PLUS 1 2) (MINUS 7 3))";
        final String result = evaluate(expression).toAtom().toS();
        assertEquals("12", result);
    }
}
