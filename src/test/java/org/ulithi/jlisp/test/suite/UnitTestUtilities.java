package org.ulithi.jlisp.test.suite;

import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.parser.Lexer;
import org.ulithi.jlisp.parser.Parser;
import org.ulithi.jlisp.primitive.Eval;

/**
 * Static helper methods for writing unit tests.
 */
public class UnitTestUtilities {

    /**
     * Thin wrapper around {@link Eval} that maintains state between evaluations (environment
     * especially) so tests can do things like creating a user-defined function or variable in
     * one step and then reference it (e.g. invoke the function, read the variable) later on.
     */
    public static class Session {
        private final Eval eval = new Eval();

        // No makee: use the newSession() factory method instead.
        private Session() { }

        /**
         * Evaluates the given expression and returns the result, retaining any state created
         * by the expression or previous expressions evaluated by the same session.
         *
         * @param expression The LISP expression to evaluate.
         * @return The result of the evaluation, as an {@link SExpression}.
         */
        public SExpression eval(final String expression) {
            final PTree pTree = parse(expression);
            return eval.apply(pTree.root());
        }
    }

    /**
     * Creates and returns a new {@link Session} instance, to support tests that need to maintain
     * state across multiple evals.
     *
     * @return A new {@code Session} instance with a newly initialized runtime environment.
     */
    public static Session newSession() {
        return new Session();
    }

    /**
     * Scans, parses, evaluates the given LISP {@code expression} and returns the result as a
     * {@code SExpression}.
     * @param expression The LISP expression to evaluate.
     * @return The result of the evaluation, as an {@link SExpression}.
     */
    public static SExpression eval(final String expression) {
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
        return p.parse(lexer.getTokens()).orElseThrow();
    }
}
