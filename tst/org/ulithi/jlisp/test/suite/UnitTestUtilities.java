package org.ulithi.jlisp.test.suite;

import org.ulithi.jlisp.lexer.Lexer;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.parser.Parser;
import org.ulithi.jlisp.primitive.Eval;

/**
 * Static helper methods for writing unit tests.
 */
public class UnitTestUtilities {
    /**
     * Scans, parses, evaluates the given LISP {@code expression} and returns the result as a
     * {@code String}.
     * @param expression The LISP expression to evaluate.
     * @return The result of the evaluation, as a {@code String}.
     */
    public static String evaluate(final String expression) {
        final Lexer lexer = new Lexer(expression);
        final Parser p = new Parser();
        final PTree ptree = p.parse(lexer.getTokens());
        final Eval eval = new Eval();
        return String.valueOf(eval.apply(ptree.root()));
    }
}
