package org.ulithi.jlisp.test.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link org.ulithi.jlisp.test.Expect} class.
 */
public class ExpectTestCase {

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().muteForSuccessfulTests();

    @Test
    public void testExpectWithExpectedEqualActual() {
        final SExpression result = UnitTestUtilities.eval("(EXPECT (+ 2 3) '5)");
        assertTrue(result.toAtom().toB());
    }

    @Test
    public void testExpectWithExpectedNotEqualActual() {
        final SExpression result = UnitTestUtilities.eval("(EXPECT (+ 2 6) '5)");
        assertFalse(result.toAtom().toB());
    }

    @Test
    public void testExpectWithNILExpectedAndActual() {
        final SExpression result = UnitTestUtilities.eval("(EXPECT (CAR NIL) NIL)");
        assertTrue(result.toAtom().toB());
    }

    @Test
    public void testExpectWithNonNILActualAndNILExpected() {
        final SExpression result = UnitTestUtilities.eval("(EXPECT (CAR '(A B C)) NIL)");
        assertFalse(result.toAtom().toB());
    }

    @Test
    public void testBareThen() {
        final SExpression result = UnitTestUtilities.eval("(THEN)");
        assertTrue(result.toAtom().toB());
    }
}
