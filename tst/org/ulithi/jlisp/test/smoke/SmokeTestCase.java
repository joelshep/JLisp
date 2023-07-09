package org.ulithi.jlisp.test.smoke;

import org.junit.Test;
import org.ulithi.jlisp.lexer.Lexer;
import org.ulithi.jlisp.parser.Parser;

import static org.junit.Assert.assertEquals;

/**
 * Unit test that drives several simple LISP expressions through the interpreter and validates the
 * results. This test is by no means exhaustive or a reference suite: it's simply to verify that
 * the lex-parse-evaluate process works together successfully.
 */
public class SmokeTestCase {
    @Test
    public void evaluateSimpleMathExpression() throws Exception {
        final String expression = "(PLUS 3 2)";
        final Lexer lexer = new Lexer(expression);
        final Parser p = new Parser(lexer.getTokens());
        final String result = p.evaluate();
        assertEquals(5, Integer.parseInt(result));
    }
}
