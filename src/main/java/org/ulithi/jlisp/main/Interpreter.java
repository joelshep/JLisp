package org.ulithi.jlisp.main;

import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.parser.Lexer;
import org.ulithi.jlisp.parser.Parser;
import org.ulithi.jlisp.primitive.Eval;

import java.util.Optional;

/**
 * The top-level JLISP interpreter. Feed it a LISP expression, and it will scan, parse and
 * evaluate it. Repeat as necessary. Runs on its own thread to stabilize integration with the
 * main client application (e.g. a console REPL-style app).
 */
public class Interpreter implements Runnable {

    /** Interpreter name. */
    private static final String NAME = "JLisp";

    /** A version string for the core LISP implementation. */
    private static final String VERSION = "0.10";

    /** If true, enables stack trace dumps in the event of processing errors. */
    private boolean verbose = false;

    /** The eval function instance used by this interpreter. */
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
     * Enables/disables verbose error logging.
     * @param flag If true, enables stack trace dumps in the event of processing errors.
     */
    public void verbose(final boolean flag) {
        verbose = flag;
    }

    /**
     * @return The name of this interpreter.
     */
    public String getName() {
        return NAME;
    }

    /**
     * @return The version of this interpreter.
     */
    public String getVersion() {
        return VERSION;
    }

    /**
     * Initiates the scanning, parsing and evaluation of the given LISP expression.
     * @param expression A LISP expression to evaluate.
     * @return True if expression was evaluated successfully, false otherwise.
     */
    public boolean offer(final String expression) {
        try {
            final Optional<PTree> pTree = parseExpression(expression);
            pTree.map(tree -> {
                SExpression ret = eval.apply(tree.root());
                System.out.println(" " + ret);
                return true;
            });
            return true;
        } catch (final Exception e) {
            System.err.println(e.getMessage());
            if (verbose) { e.printStackTrace(System.err); }
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
            final Optional<PTree> pTree = parseExpression(expression);
            pTree.ifPresentOrElse(
                    System.out::println,
                    System.out::println
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error parsing expression: " + e.getMessage());
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
            final Optional<PTree> pTree = parseExpression(expression);
            pTree.ifPresentOrElse(
                    tree -> System.out.println(tree.unparse()),
                    System.out::println
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error parsing expression: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method that scans and parses the given expression.
     *
     * @param expression The expression to parse.
     * @return The parse tree for the expression.
     */
    private static Optional<PTree> parseExpression(final String expression) {
        final Lexer lexer = new Lexer(expression);
        final Parser p = new Parser();
        return p.parse(lexer.getTokens());
    }
}
