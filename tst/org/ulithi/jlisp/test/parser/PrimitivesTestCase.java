package org.ulithi.jlisp.test.parser;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.evaluate;

/**
 * Unit tests for the LISP primitives implemented in {@link org.ulithi.jlisp.parser.Primitives}.
 */
public class PrimitivesTestCase {
    @Test
    @Ignore
    public void evaluateCarExpression() throws Exception {
        assertEquals("x", evaluate("( CAR () )"));
    }

    @Test
    public void evaluateCarConsExpression() throws Exception {
        final String expression = "(CAR (CONS x y))";
        final String result = evaluate(expression);
        assertEquals("x", result);
    }

    @Test
    public void evaluateCdrConsExpression() throws Exception {
        final String expression = "(CDR (CONS x y))";
        final String result = evaluate(expression);
        assertEquals("y", result);
    }

    @Test
    public void evaluateCdrConsListExpression() throws Exception {
        final String expression = "(CDR (CONS x (PLUS 1 2)))";
        final String result = evaluate(expression);
        assertEquals("3", result);
    }

    @Test
    public void evaluateConsExpression() throws Exception {
        final String expression = "(CONS x y)";
        final String result = evaluate(expression);
        assertEquals("(x . y)", result);
    }

    @Test
    public void evaluateLessExpression() throws Exception {
        final String expression = "(LESS 2 3)";
        final String result = evaluate(expression);
        assertEquals("T", result);
    }

}
