package org.ulithi.jlisp.test.smoke;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.evaluate;

/**
 * Unit test that drives several simple LISP expressions through the interpreter and validates the
 * results. This test is by no means exhaustive or a reference suite: it's simply to verify that
 * the lex-parse-evaluate process works together successfully.
 */
public class SmokeTestCase {
    @Test
    public void evaluateSimpleMathExpression() throws Exception {
        final String expression = "(PLUS 3 2)";
        final String result = evaluate(expression);
        assertEquals(5, Integer.parseInt(result));
    }
}
