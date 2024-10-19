package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.exception.EvaluationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.eval;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Math}.
 */
public class MathTestCase {

    @Test
    public void testSingleValueIsLessThan() {
        final boolean result = eval("(< -1)").toAtom().toB();
        assertTrue(result);
    }

    @Test
    public void testIsLessThan() {
        final boolean result = eval("(< 5 8)").toAtom().toB();
        assertTrue(result);
    }

    @Test
    public void testNotLessThan() {
        final boolean result = eval("(< 8 5)").toAtom().toB();
        assertFalse(result);
    }

    @Test
    public void testEqualNotLessThan() {
        final boolean result = eval("(< 8 8)").toAtom().toB();
        assertFalse(result);
    }

    @Test
    public void testIncreasingOrderIsLessThan() {
        final boolean result = eval("(< -4 8 23 67 98 104)").toAtom().toB();
        assertTrue(result);
    }

    @Test
    public void testNotIncreasingOrderIsNotLessThan() {
        final boolean result = eval("(< -4 8 23 67 48 104)").toAtom().toB();
        assertFalse(result);
    }

    @Test
    public void testLastNotIncreasingOrderIsNotLessThan() {
        final boolean result = eval("(< -4 8 23 67 98 97)").toAtom().toB();
        assertFalse(result);
    }

    @Test
    public void testSingleValueIsGreaterThan() {
        final boolean result = eval("(> -1000000)").toAtom().toB();
        assertTrue(result);
    }

    @Test
    public void testIsGreaterThan() {
        final boolean result = eval("(> 34 24)").toAtom().toB();
        assertTrue(result);
    }

    @Test
    public void testNotGreaterThan() {
        final boolean result = eval("(> 63 102)").toAtom().toB();
        assertFalse(result);
    }

    @Test
    public void testEqualNotGreaterThan() {
        final boolean result = eval("(> 63 63)").toAtom().toB();
        assertFalse(result);
    }

    @Test
    public void testDecreasingOrderIsGreaterThan() {
        final boolean result = eval("(> 104 98 67 23 8 -4)").toAtom().toB();
        assertTrue(result);
    }

    @Test
    public void testLastNotDecreasingOrderIsNotGreaterThan() {
        final boolean result = eval("(> 104 98 67 23 8 8)").toAtom().toB();
        assertFalse(result);
    }

    /**
     * Evaluates a simple addition expression: (+ 2 3).
     */
    @Test
    public void testAddTwoNumbers() {
        final int result = eval("(+ 2 3)").toAtom().toI();
        assertEquals(5, result);
    }

    /**
     * Evaluates an addition expression with an "arbitrary" (not two) number of arguments.
     */
    @Test
    public void testAddFourNumbers() {
        final int result = eval("(+ 2 3 4 5)").toAtom().toI();
        assertEquals(14, result);
    }

    /**
     * Evaluates an addition expression with a nested sub-expression: (+ 2 (* 2 3))
     */
    @Test
    public void testMultiplyThenAdd() {
        final int result = eval("(+ 2 (* 2 3))").toAtom().toI();
        assertEquals(8, result);
    }

    @Test
    public void testAddThenMultiply() {
        final int result = eval("(TIMES (PLUS 1 2) (MINUS 7 3))").toAtom().toI();
        assertEquals(12, result);
    }

    /**
     * Evaluates an addition expression with two nested sub-expressions:
     * (+ (* 4 5) (* 2 3))
     */
    @Test
    public void testMultiplyTwiceThenAdd() {
        final int result = eval("(+ (* 4 5) (* 2 3))").toAtom().toI();
        assertEquals(26, result);
    }

    /**
     * <em>Scans</em>, parses and evaluates an expression with a sub-sub-expression:
     *   (+ (* 4 (+ 2 3)) (* 2 3))
     */
    @Test
    public void testAddMultipleThenAdd() {
        final int result = eval("(+ (* 4 (+ 2 3)) (* 2 3))").toAtom().toI();
        assertEquals(26, result);
    }

    @Test
    public void testSubtractTwoNumbers() {
        final int result = eval("(MINUS 11 5)").toAtom().toI();
        assertEquals(6, result);
    }

    @Test
    public void testSubtractFromZero() {
        final int result = eval("(MINUS 0 12)").toAtom().toI();
        assertEquals(-12, result);
    }

    @Test
    public void testSubtractThreeNumbers() {
        final int result = eval("(MINUS 72 12 7)").toAtom().toI();
        assertEquals(53, result);
    }

    @Test
    public void testSubtractSingleNumberNegates() {
        final int result = eval("(MINUS 47)").toAtom().toI();
        assertEquals(-47, result);
    }

    @Test
    public void testDivideTwoNumbers() {
        int result = eval("(QUOTIENT 72 8)").toAtom().toI();
        assertEquals(9, result);

        result = eval("(/ 72 8)").toAtom().toI();
        assertEquals(9, result);
    }

    @Test
    public void testDivideFourNumbers() {
        int result = eval("(QUOTIENT 200 4 5 5)").toAtom().toI();
        assertEquals(2, result);

        result = eval("(/ 200 4 5 5)").toAtom().toI();
        assertEquals(2, result);
    }

    @Test(expected = EvaluationException.class)
    public void testDivideByZero() {
        eval("(QUOTIENT 7 0)");
    }

    @Test
    public void testRemainderOfTwoNumbers() {
        final int result = eval("(REMAINDER 77 8)").toAtom().toI();
        assertEquals(5, result);
    }

    @Test
    public void testRemainderOfThreeNumbers() {
        final int result = eval("(REMAINDER 77 8 3)").toAtom().toI();
        assertEquals(2, result);
    }

    @Test
    public void testRemainderWithLargerDivisor() {
        final int result = eval("(REMAINDER 8 77)").toAtom().toI();
        assertEquals(8, result);
    }
}
