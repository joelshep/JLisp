package org.ulithi.jlisp.parser;

/**
 * Represents a token (a single unit of LISP grammar) produced by the Lexer.
 * TODO: Capture source position data like line and character position.
 */
public class Token {

    /** The token value as a String. */
    private final String value;

    /**
     * Creates a new {@link Token} to represent the given {@code String} value.
     * @param value A source code token, as a {@code String}.
     * @return A {@code Token} representing the given source code token.
     */
    public static Token create(final String value) {
        return new Token(value);
    }

    /**
     * Creates a new {@link Token} to represent the given {@code String} value.
     * @param value A source code token, as a {@code String}.
     */
    private Token(final String value) {
        this.value = value;
    }

    /**
     * @return The original source code representation of this token.
     */
    public String value() {
        return this.value;
    }
}
