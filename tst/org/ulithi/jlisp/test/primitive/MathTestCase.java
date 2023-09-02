package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Math}.
 */
public class MathTestCase {

    @Test
    public void testIsLessThan() {
        final String expression = "(< 5 8)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        boolean foo = sexp.toAtom().toB();
        assertTrue(expression, foo);
    }

    @Test
    public void testNotLessThan() {
        final String expression = "(< 8 5)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        boolean foo = sexp.toAtom().toB();
        assertFalse(expression, foo);
    }

    @Test
    public void testEqualNotLessThan() {
        final String expression = "(< 8 8)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        boolean foo = sexp.toAtom().toB();
        assertFalse(expression, foo);
    }


    @Test
    public void testIsGreaterThan() {
        final String expression = "(> 34 24)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        boolean foo = sexp.toAtom().toB();
        assertTrue(expression, foo);
    }

    @Test
    public void testNotGreaterThan() {
        final String expression = "(> 63 102)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        boolean foo = sexp.toAtom().toB();
        assertFalse(expression, foo);
    }

    @Test
    public void testEqualNotGreaterThan() {
        final String expression = "(> 63 63)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        boolean foo = sexp.toAtom().toB();
        assertFalse(expression, foo);
    }

    /**
     * Evaluates a simple addition expression: (+ 2 3).
     */
    @Test
    public void testAddTwoNumbers() {
        final String expression = "(+ 2 3)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        int foo = sexp.toAtom().toI();
        assertEquals(expression, 5, foo);
    }

    /**
     * Evaluates an addition expression with an "arbitrary" (not two) number of arguments.
     */
    @Test
    public void testAddFourNumbers() {
        final String expression = "(+ 2 3 4 5)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        int foo = sexp.toAtom().toI();
        assertEquals(expression, 14, foo);
    }

    /**
     * Evaluates an addition expression with a nested sub-expression: (+ 2 (* 2 3))
     */
    @Test
    public void testMultiplyThenAdd() {
        final String expression = "(+ 2 (* 2 3))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        int foo = sexp.toAtom().toI();
        assertEquals(expression, 8, foo);
    }

    /**
     * Evaluates an addition expression with two nested sub-expressions:
     * (+ (* 4 5) (* 2 3))
     */
    @Test
    public void testMultiplyTwiceThenAdd() {
        final String expression = "(+ (* 4 5) (* 2 3))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        int foo = sexp.toAtom().toI();
        assertEquals(expression, 26, foo);
    }

    /**
     * <em>Scans</em>, parses and evaluates an expression with a sub-sub-expression:
     *   (+ (* 4 (+ 2 3)) (* 2 3))
     */
    @Test
    public void testAddMultipleThenAdd() {
        final String expression = "(+ (* 4 (+ 2 3)) (* 2 3))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        int foo = sexp.toAtom().toI();
        assertEquals(expression, 26, foo);
    }

    @Test
    public void testSubtractTwoNumbers() {
        final String expression = "(MINUS 11 5)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        int foo = sexp.toAtom().toI();
        assertEquals(expression, 6, foo);
    }

    @Test
    public void testSubtractFromZero() {
        final String expression = "(MINUS 0 12)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        int foo = sexp.toAtom().toI();
        assertEquals(expression, -12, foo);
    }

    @Test
    public void testSubtractThreeNumbers() {
        final String expression = "(MINUS 72 12 7)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        int foo = sexp.toAtom().toI();
        assertEquals(expression, 53, foo);
    }

    @Test
    public void testSubtractSingleNumberNegates() {
        final String expression = "(MINUS 47)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        int foo = sexp.toAtom().toI();
        assertEquals(expression, -47, foo);
    }

    @Test
    public void testDivideTwoNumbers() {
        final String expression = "(QUOTIENT 72 8)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        int foo = sexp.toAtom().toI();
        assertEquals(expression, 9, foo);
    }

    @Test
    public void testDivideFourNumbers() {
        final String expression = "(QUOTIENT 200 4 5 5)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        int foo = sexp.toAtom().toI();
        assertEquals(expression, 2, foo);
    }
}
