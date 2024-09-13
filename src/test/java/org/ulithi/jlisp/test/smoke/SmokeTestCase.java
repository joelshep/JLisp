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
    public void evaluateNumericLiteral() {
        final String expression = "43";
        final int result = evaluate(expression).toAtom().toI();
        assertEquals(43, result);
    }

    @Test
    public void evaluateSimpleMathExpression() {
        final String expression = "(+ 3 2)";
        final int result = evaluate(expression).toAtom().toI();
        assertEquals(5, result);
    }

    @Test
    public void evaluateCarCdrExpression() {
        final String expression = "(CDR (CAR (QUOTE ((1 2 3) (4 5 6)) ) ) )";
        final String result = evaluate(expression).toString();
        assertEquals("(2 . (3 . NIL))", result);
    }
}
