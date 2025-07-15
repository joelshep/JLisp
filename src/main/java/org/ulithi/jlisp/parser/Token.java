package org.ulithi.jlisp.parser;

/**
 * Represents a token (a single unit of LISP grammar) produced by the Lexer.
 * TODO: Capture source position data like line and character position.
 */
public class Token {

    /**
     * Enumerates the different types of tokens relevant for parsing and evaluation.
     */
    public enum Type {
        ATOM,
        LIST_START,
        LIST_END,
        DOT
    }

    /** Singleton instances of the token for a left-parenthesis (list start). */
    public static final Token LPAREN = new Token(Grammar.LPAREN, Type.LIST_START);

    /** Singleton instances of the token for a right-parenthesis (list end). */
    public static final Token RPAREN = new Token(Grammar.RPAREN, Type.LIST_END);

    /** The token value as a String. */
    private final String value;

    /** The type of language element represented by the token. */
    private final Type type;

    /**
     * Creates and returns an Atom-type {@link Token} wrapping the given value.
     * @param value A parsed atom-type value.
     * @return A {@code Token} representing the atom-type value.
     */
    public static Token fromAtom(final String value) {
        return new Token(value, Type.ATOM);
    }

    /**
     * Indicates if this token represents the start of a list.
     * @return True if this token marks the start of a list, false otherwise.
     */
    public boolean isListStart() {
        return this.type == Type.LIST_START;
    }

    /**
     * Indicates if this token represents the emd of a list.
     * @return True if this token marks the end of a list, false otherwise.
     */
    public boolean isListEnd() {
        return this.type == Type.LIST_END;
    }

    /**
     * Indicates if this token represents an atomic value.
     * @return True if this token represents an atomic value, false otherwise.
     */
    public boolean isAtom() {
        return this.type == Type.ATOM;
    }

    /**
     * Indicates if this token represents a dotted pair separator (".").
     * @return True if this token represents a dotted pair separator, false otherwise.
     */
    public boolean isDot() {
        return this.type == Type.DOT;
    }

    /**
     * Creates a new {@link Token} to represent the given {@code String} value and {@code Type}
     * @param value A source code token, as a {@code String}.
     * @param type The semantic type of the token.
     */
    private Token(final String value, final Type type) {
        this.value = value;
        this.type = type;
    }

    /**
     * @return The original source code representation of this token.
     */
    public String value() {
        return this.value;
    }
}
