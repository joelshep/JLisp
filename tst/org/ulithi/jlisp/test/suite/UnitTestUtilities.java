package org.ulithi.jlisp.test.suite;

import org.ulithi.jlisp.lexer.Lexer;
import org.ulithi.jlisp.parser.Parser;

/**
 * Static helper methods for writing unit tests.
 */
public class UnitTestUtilities {
    public static String evaluate(final String expression) throws Exception {
        final Lexer lexer = new Lexer(expression);
        final Parser p = new Parser(lexer.getTokens());
        return p.evaluate();
    }
}
