package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.eval;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Math}.
 */
public class MathTestCase {

    @Test
    public void testSingleValueIsLessThan() {
        final String expression = "(< -1)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertTrue(expression, result);
    }

    @Test
    public void testIsLessThan() {
        final String expression = "(< 5 8)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertTrue(expression, result);
    }

    @Test
    public void testNotLessThan() {
        final String expression = "(< 8 5)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertFalse(expression, result);
    }

    @Test
    public void testEqualNotLessThan() {
        final String expression = "(< 8 8)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertFalse(expression, result);
    }

    @Test
    public void testIncreasingOrderIsLessThan() {
        final String expression = "(< -4 8 23 67 98 104)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertTrue(expression, result);
    }

    @Test
    public void testNotIncreasingOrderIsNotLessThan() {
        final String expression = "(< -4 8 23 67 48 104)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertFalse(expression, result);
    }

    @Test
    public void testLastNotIncreasingOrderIsNotLessThan() {
        final String expression = "(< -4 8 23 67 98 97)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertFalse(expression, result);
    }

    @Test
    public void testSingleValueIsGreaterThan() {
        final String expression = "(> -1000000)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertTrue(expression, result);
    }

    @Test
    public void testIsGreaterThan() {
        final String expression = "(> 34 24)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertTrue(expression, result);
    }

    @Test
    public void testNotGreaterThan() {
        final String expression = "(> 63 102)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertFalse(expression, result);
    }

    @Test
    public void testEqualNotGreaterThan() {
        final String expression = "(> 63 63)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertFalse(expression, result);
    }

    @Test
    public void testDecreasingOrderIsGreaterThan() {
        final String expression = "(> 104 98 67 23 8 -4)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertTrue(expression, result);
    }

    @Test
    public void testLastNotDecreasingOrderIsNotGreaterThan() {
        final String expression = "(> 104 98 67 23 8 8)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertFalse(expression, result);
    }

    /**
     * Evaluates a simple addition expression: (+ 2 3).
     */
    @Test
    public void testAddTwoNumbers() {
        final String expression = "(+ 2 3)";
        final int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 5, result);
    }

    /**
     * Evaluates an addition expression with an "arbitrary" (not two) number of arguments.
     */
    @Test
    public void testAddFourNumbers() {
        final String expression = "(+ 2 3 4 5)";
        final int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 14, result);
    }

    /**
     * Evaluates an addition expression with a nested sub-expression: (+ 2 (* 2 3))
     */
    @Test
    public void testMultiplyThenAdd() {
        final String expression = "(+ 2 (* 2 3))";
        final int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 8, result);
    }

    @Test
    public void testAddThenMultiply() {
        final String expression = "(TIMES (PLUS 1 2) (MINUS 7 3))";
        final int result = eval(expression).toAtom().toI();
        assertEquals(expression, 12, result);
    }

    /**
     * Evaluates an addition expression with two nested sub-expressions:
     * (+ (* 4 5) (* 2 3))
     */
    @Test
    public void testMultiplyTwiceThenAdd() {
        final String expression = "(+ (* 4 5) (* 2 3))";
        final int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 26, result);
    }

    /**
     * <em>Scans</em>, parses and evaluates an expression with a sub-sub-expression:
     *   (+ (* 4 (+ 2 3)) (* 2 3))
     */
    @Test
    public void testAddMultipleThenAdd() {
        final String expression = "(+ (* 4 (+ 2 3)) (* 2 3))";
        final int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 26, result);
    }

    @Test
    public void testSubtractTwoNumbers() {
        final String expression = "(MINUS 11 5)";
        final int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 6, result);
    }

    @Test
    public void testSubtractFromZero() {
        final String expression = "(MINUS 0 12)";
        final int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, -12, result);
    }

    @Test
    public void testSubtractThreeNumbers() {
        final String expression = "(MINUS 72 12 7)";
        final int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 53, result);
    }

    @Test
    public void testSubtractSingleNumberNegates() {
        final String expression = "(MINUS 47)";
        final int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, -47, result);
    }

    @Test
    public void testDivideTwoNumbers() {
        String expression = "(QUOTIENT 72 8)";
        int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 9, result);

        expression = "(/ 72 8)";
        result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 9, result);
    }

    @Test
    public void testDivideFourNumbers() {
        String expression = "(QUOTIENT 200 4 5 5)";
        int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 2, result);

        expression = "(/ 200 4 5 5)";
        result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 2, result);
    }

    @Test
    public void testDivideByZero() {
        final String expression = "(QUOTIENT 7 0)";

        try {
            UnitTestUtilities.eval(expression);
            fail("Expected EvaluationException");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    @Test
    public void testRemainderOfTwoNumbers() {
        final String expression = "(REMAINDER 77 8)";
        final int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 5, result);
    }

    @Test
    public void testRemainderOfThreeNumbers() {
        final String expression = "(REMAINDER 77 8 3)";
        final int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 2, result);
    }

    @Test
    public void testRemainderWithLargerDivisor() {
        final String expression = "(REMAINDER 8 77)";
        final int result = UnitTestUtilities.eval(expression).toAtom().toI();
        assertEquals(expression, 8, result);
    }
}
