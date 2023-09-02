package org.ulithi.jlisp.test.suite;

import org.ulithi.jlisp.core.SExpression;
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
     * {@code SExpression}.
     * @param expression The LISP expression to evaluate.
     * @return The result of the evaluation, as an {@link SExpression}.
     */
    public static SExpression evaluate(final String expression) {
        final PTree ptree = parse(expression);
        final Eval eval = new Eval();
        return eval.apply(ptree.root());
    }

    /**
     * Scans and parses the given LISP {@code expression} and returns resulting parse tree
     * ({@link PTree}.
     * @param expression The LISP expression to evaluate.
     * @return The result of the parsing, as a {@code PTree}.
     */
    public static PTree parse(final String expression) {
        final Lexer lexer = new Lexer(expression);
        final Parser p = new Parser();
        return p.parse(lexer.getTokens());
    }
}
