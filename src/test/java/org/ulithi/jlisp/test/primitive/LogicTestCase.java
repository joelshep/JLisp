package org.ulithi.jlisp.test.primitive;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.eval;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Logic}.
 */
public class LogicTestCase {

    @Test
    public void testT() {
        final boolean result = eval("(T)").toAtom().toB();
        assertTrue(result);
    }

    @Test
    public void testF() {
        final boolean result = eval("(F)").toAtom().toB();
        assertFalse(result);
    }
}
