package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Logic}.
 */
public class LogicTestCase {

    @Test
    public void testT() {
        final String expression = "(T)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        boolean foo = sexp.toAtom().toB();
        assertTrue(expression, foo);
    }

    @Test
    public void testInvalidT() {
        final String expression = "(T 1 2 3)";

        try {
            UnitTestUtilities.evaluate(expression);
            fail("Expected exception not thrown");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    @Test
    public void testF() {
        final String expression = "(F)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        boolean foo = sexp.toAtom().toB();
        assertFalse(expression, foo);
    }

    @Test
    public void testInvalidF() {
        final String expression = "(F 1 2 3)";

        try {
            UnitTestUtilities.evaluate(expression);
            fail("Expected exception not thrown");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }


}
