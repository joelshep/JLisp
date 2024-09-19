package org.ulithi.jlisp.main;

import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.parser.Lexer;
import org.ulithi.jlisp.parser.Parser;
import org.ulithi.jlisp.primitive.Eval;

/**
 * The top-level JLISP interpreter. Feed it a LISP expression, and it will scan, parse and
 * evaluate it. Repeat as necessary. Runs on its own thread to stabilize integration with the
 * main client application (e.g. a console REPL-style app).
 */
public class Interpreter implements Runnable {

    /**
     * The eval function instance used by this interpreter.
     */
    private final Eval eval = new Eval();

    /**
     * Initializes this {@link Interpreter}.
     */
    public void initialize() { }

    /**
     * Resets this {@link Interpreter}, allowing continued operation after an expression
     * evaluation failure.
     */
    public void reset() { }

    /**
     * Invoked when the {@link Interpreter} execution is initiated on its own thread.
     * {@inheritDoc}
     */
    @Override
    public void run() { }

    /**
     * Initiates the scanning, parsing and evaluation of the given LISP expression.
     * @param expression A LISP expression to evaluate.
     * @return True if expression was evaluated successfully, false otherwise.
     */
    public boolean offer(final String expression) {
        try {
            final PTree pTree = parseExpression(expression);
            final SExpression ret = eval.apply(pTree.root());
            System.out.println(" " + ret);
            return true;
        } catch (final Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Scans and parses the given LISP expression, and writes the resulting parse tree to
     * STDOUT in dotted pair notation.
     *
     * @param expression A LISP expression to parse.
     * @return True if expression was parsed successfully, false otherwise.
     */
    public boolean parse(final String expression) {
        try {
            final PTree pTree = parseExpression(expression);
            System.out.println(pTree);
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Scans and parses the given LISP expression. Then, re-constructs the expression from the
     * parse tree and writes it to STDOUT. This is mostly useful as more readable sanity check
     * on the parser output than the parse tree itself.
     *
     * @param expression A LISP expression to parse.
     * @return True if expression was parsed successfully, false otherwise.
     */
    public boolean parseAndEcho(final String expression) {
        try {
            final PTree pTree = parseExpression(expression);
            final String reExpression = pTree.unparse();
            System.out.println(reExpression);
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Helper method that scans and parses the given expression.
     *
     * @param expression The expression to parse.
     * @return The parse tree for the expression.
     */
    private static PTree parseExpression(final String expression) {
        final Lexer lexer = new Lexer(expression);
        final Parser p = new Parser();
        return p.parse(lexer.getTokens());
    }
}
