package org.ulithi.jlisp.core;

import org.ulithi.jlisp.lexer.Lexer;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.parser.Environment;
import org.ulithi.jlisp.parser.Parser;
import org.ulithi.jlisp.primitive.Eval;

import java.io.InputStream;

public class Interpreter implements Runnable {

    private final Environment environment;

    private final Eval eval = new Eval();

    public Interpreter () {
        this.environment = new Environment();
    }

    public void initialize() {

    }

    public void reset() {

    }

    public boolean offer(final String expression) {
        try {
            final Lexer lexer = new Lexer(expression);
            final Parser p = new Parser();
            final PTree pTree = p.parse(lexer.getTokens());
            final int ret = eval.apply(pTree.root());
            System.out.println(" " + ret);
            return true;
        } catch (final Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void run() {

    }
}
