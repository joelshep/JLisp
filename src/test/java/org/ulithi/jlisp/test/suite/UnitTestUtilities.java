package org.ulithi.jlisp.test.suite;

import org.ulithi.jlisp.core.Environment;
import org.ulithi.jlisp.core.SExpression;
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
        private final Environment env = new Environment();

        private final Eval eval = new Eval(env);

        // No makee: use the newSession() factory method instead.
        private Session() { }

        /**
         * Evaluates the given form and returns the result, retaining any state created by the
         * form or previous forms evaluated by the same session.
         *
         * @param form The LISP form to evaluate.
         * @return The result of the evaluation, as an {@link SExpression}.
         */
        public SExpression eval(final String form) {
            final SExpression sexp = parse(form);
            return eval.eval(sexp);
        }

        public boolean isDefined(final String name) {
            return env.isDefined(name);
        }

        public boolean isMacro(final String name) {
            return env.isMacro(name);
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
     * Scans, parses, evaluates the given LISP {@code form} and returns the result as an
     * {@code SExpression}.
     * @param form The LISP form to evaluate.
     * @return The result of the evaluation, as an {@link SExpression}.
     */
    public static SExpression eval(final String form) {
        final SExpression sexp = parse(form);
        final Eval eval = new Eval();
        return eval.eval(sexp);
    }

    /**
     * Scans and parses the given LISP form and returns the equivalent ({@link SExpression}.
     * @param form The LISP form to parse.
     * @return The result of the parsing, as an {@link SExpression}.
     */
    public static SExpression parse(final String form) {
        final Lexer lexer = new Lexer();
        lexer.append(form);
        final Parser p = new Parser();
        return p.parse(lexer.getTokens()).orElseThrow();
    }
}
