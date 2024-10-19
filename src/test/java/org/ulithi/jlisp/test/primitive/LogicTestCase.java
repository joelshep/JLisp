package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Logic}.
 */
public class LogicTestCase {

    @Test
    public void testT() {
        final String expression = "(T)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertTrue(expression, result);
    }

    @Test
    public void testF() {
        final String expression = "(F)";
        final boolean result = UnitTestUtilities.eval(expression).toAtom().toB();
        assertFalse(expression, result);
    }
}
