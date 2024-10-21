package org.ulithi.jlisp.main;

import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.parser.Lexer;
import org.ulithi.jlisp.parser.Parser;
import org.ulithi.jlisp.primitive.Eval;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

    /** The lexer used by this interpreter. **/
    private final Lexer lexer = new Lexer();

    /** The eval function instance used by this interpreter. */
    private final Eval eval = new Eval();

    /** Initializes this {@link Interpreter}. */
    public void initialize() { }

    /**
     * Resets this {@link Interpreter}, allowing continued operation after an expression
     * evaluation failure.
     */
    public void reset() { lexer.reset(); }

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
     * @return An {@code Optional<Boolean>} True if expression was evaluated successfully, false
     *         if an error occurred while parsing or evaluating the expression, or empty if the
     *         expression wasn't a complete form, or didn't complete a form created by previous
     *         calls to the interpreter.
     */
    public Optional<Boolean> offer(final String expression) {
        return processExpression(expression, this::offerImpl, this::onOfferError);
    }

    /**
     * Scans and parses the given LISP expression. Then, re-constructs the expression from the
     * parse tree and writes it to STDOUT. This is mostly useful as more readable sanity check
     * on the parser output than the parse tree itself.
     *
     * @param expression A LISP expression to parse.
     * @return See {@link #offer(String)}.
     */
    public Optional<Boolean> parseAndEcho(final String expression) {
        return processExpression(expression, this::echoImpl, this::onParseError);
    }

    /**
     * Scans and parses the given LISP expression, and writes the resulting parse tree to
     * STDOUT in dotted pair notation.
     *
     * @param expression A LISP expression to parse.
     * @return See {@link #offer(String)}.
     */
    public Optional<Boolean> parseOnly(final String expression) {
        return processExpression(expression, this::parseImpl, this::onParseError);
    }

    /**
     * Scans and parses the given expression. If the expression is a complete form, or produces a
     * completes the form created by previous calls to the interpreter, invokes the given
     * implementing function {@code impl}. If an error occurs during parsing or evaluation, invokes
     * the given error handler {@code onError}.
     *
     * @param expression A LISP expression to evaluate.
     * @param impl A function implementing some particular interpreter functionality.
     * @param onError The function to invoke if an error occurs during parsing or processing.
     * @return An (optional) boolean true if the expression was parsed and evaluated without
     *         error, false if an error occurred, and null if the expression wasn't a complete
     *         form, or didn't complete a form created by previous calls to the interpreter.
     */
    private Optional<Boolean> processExpression(final String expression,
                                                final Function<PTree, Optional<Boolean>> impl,
                                                final Function<Exception, Optional<Boolean>> onError)
    {
        try {
            final Optional<PTree> pTree = parseExpression(expression);

            if (pTree.isPresent()) {
                return impl.apply(pTree.get());
            } else if (lexer.hasTokens()) {
                return Optional.empty();
            } else {
                return Optional.of(Boolean.TRUE);
            }
        } catch (final Exception e) {
            return onError.apply(e);
        }
    }

    /**
     * Implements the 'offer' method by evaluating the given PTree, if provided.
     * @param pTree A parsed LISP expression.
     * @return An Optional True if the expression was successfully evaluated, empty if the
     *         expression wasn't evaluated. Evaluation errors are handled by the caller.
     */
    private Optional<Boolean> offerImpl(final PTree pTree) {
            SExpression ret = eval.apply(pTree.root());
            System.out.println(" " + ret);
            return Optional.of(Boolean.TRUE);
    }

    /**
     * Implements the 'parse' method by outputting a string representation of the given PTree in
     * dotted pair notation, if provided.
     *
     * @param pTree A parsed LISP expression.
     * @return An Optional True if the PTree was present and output, empty if no PTree was
     *         provided. Errors are handled by the caller.
     */
    private Optional<Boolean> parseImpl(final PTree pTree) {
        System.out.println(pTree);
        return Optional.of(Boolean.TRUE);
    }

    /**
     * Implements the 'echo' method by outputting the "unparsed" string representation of the given
     * PTree: i.e., takes the parse tree, converts it back to an equivalent LISP expression and
     * outputs that.
     *
     * @param pTree A parsed LISP expression.
     * @return An Optional True if the PTree was present and output, empty if no PTree was
     *         provided. Errors are handled by the caller.
     */
    private Optional<Boolean> echoImpl(final PTree pTree) {
        System.out.println(pTree.unparse());
        return Optional.of(Boolean.TRUE);
    }

    /**
     * Handles exceptions thrown during the parsing and evaluation of LISP expressions.
     */
    private Optional<Boolean> onOfferError(final Exception e) {
        System.err.println(e.getMessage());
        if (verbose) { e.printStackTrace(System.err); }
        return Optional.of(Boolean.FALSE);
    }

    /**
     * Handles exceptions thrown during parsing of LISP expressions.
     */
    private Optional<Boolean> onParseError(final Exception e) {
        System.err.println("Error parsing expression: " + e.getMessage());
        return Optional.of(Boolean.FALSE);
    }

    /**
     * Helper method that scans and parses the given expression.
     *
     * @param expression The expression to parse.
     * @return The parse tree for the expression.
     */
    private Optional<PTree> parseExpression(final String expression) {
        lexer.append(expression);
        final Parser p = new Parser();

        if (lexer.isComplete()) {
            List<String> tokens = lexer.getTokens();
            lexer.reset();
            return p.parse(tokens);
        }

        return Optional.empty();
    }
}
