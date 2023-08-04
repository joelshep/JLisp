package org.ulithi.jlisp.main;

import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.lexer.Lexer;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.core.Environment;
import org.ulithi.jlisp.parser.Parser;
import org.ulithi.jlisp.primitive.Eval;

/**
 * The top-level JLISP interpreter. Feed it a LISP expression, and it will scan, parse and
 * evaluate it. Repeat as necessary. Runs on its own thread to stabilize integration with the
 * main client application (e.g. a console REPL-style app).
 */
public class Interpreter implements Runnable {
    /**
     * The LISP environment: Binds symbols, variables, function names, etc. to their storage or
     * definitions. At some point, the environment may be lexically scoped (allowing the same
     * name to refer to different things in different lexical scopes) but for now all environment
     * entries are global.
     */
    private final Environment environment;

    /**
     * The eval function instance used by this interpreter.
     */
    private final Eval eval = new Eval();

    /**
     * Creates a new {@link Interpreter} and its {@link Environment}.
     */
    public Interpreter () {
        this.environment = new Environment();
    }

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
            final Lexer lexer = new Lexer(expression);
            final Parser p = new Parser();
            final PTree pTree = p.parse(lexer.getTokens());
            final SExpression ret = eval.apply(pTree.root());
            System.out.println(" " + ret);
            return true;
        } catch (final Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
