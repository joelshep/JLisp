package org.ulithi.jlisp.mem;

import org.ulithi.jlisp.parser.Grammar;

/**
 * An {@link Atom} is an indivisible unit of literal data (e.g., number, boolean or character
 * sequence), or a symbol.
 */
public class Atom implements SExpression {

    public static final Atom NIL = new Atom(Grammar.NIL);

    public static final Atom T = new Atom(Grammar.T);

    private final String literal;

    public static Atom create(final String literal) {
        return new Atom(literal);
    }

    // TODO - I don't like everything being a string.
    private Atom(final String literal) {
        this.literal = literal;
    }

    public String toString() {
        return literal;
    }
}
