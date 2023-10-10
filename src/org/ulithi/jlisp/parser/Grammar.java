package org.ulithi.jlisp.parser;

import java.util.regex.Pattern;

/**
 * Static methods and constants to support parsing LISP syntax.
 * <p>
 * The canonical LISP BNF grammar can be found all over the interwebs, but here it is again
 * for quicker reference.
 * <pre>
 * s_expression := atomic_symbol | "(" s_expression "." s_expression ")" | list
 * list := "(" s_expression <s_expression> ")"
 * atomic_symbol := letter atom_part
 * atom_part = empty | letter atom_part | digit atom_part
 * letter = "a" | "b" | " ..." | "z"
 * digit = "1" | "2" | " ..." | "9"
 * empty = " "
 * </pre>
 */
public class Grammar {

    /** Static methods only: do not instantiate. */
    private Grammar() { }

    /** Regular expression pattern for a letter. */
    public static final String LETTER = "[a-zA-Z]";

    /** Regular expression pattern for empty text (whitespace). */
    public static final String EMPTY = "[\\s]+";

    /** Regular expression for an alphanumeric literal. */
    public static final String ALPHA_LITERAL = "[a-zA-Z0-9]+";

    private static final Pattern ALPHA_LITERAL_PATTERN = Pattern.compile(ALPHA_LITERAL);

    /** Regular expression for a function name. */
    public static final String FUNCTION_NAME = "[a-zA-Z][a-zA-Z0-9]*";

    private static final Pattern FUNCTION_NAME_PATTERN = Pattern.compile(FUNCTION_NAME);

    /** Regular expression for the start of a number (integer). */
    public static final String NUMERIC_LITERAL_START = "[\\d\\+\\-]";

    /** Regular expression for a number (integer). */
    public static final String NUMERIC_LITERAL = "[+\\-]?\\d+";

    /** Pre-compiled Pattern for matching on the NUMERIC_LITERAL regex. */
    private static final Pattern NUMERIC_LITERAL_PATTERN = Pattern.compile(NUMERIC_LITERAL);

    /** Special syntax elements. TODO: Remove math operators. */
    public static final String SYMBOL = "[().\\+\\*\\<\\>]";

    /** Dot symbol (for S-Expressions) **/
    public static final String DOT = ".";

    /** Left parenthesis. */
    public static final String LPAREN = "(";

    /** Right parenthesis. */
    public static final String RPAREN = ")";

    /** The NIL token. */
    public static final String NIL = "NIL";

    /** A single space character. */
    public static final String SPACE = " ";

    /** The T (true) token. */
    public static final String T = "T";

    /** The F (false) token. */
    public static final String F = "F";

    /**
     * Indicates if the given token represents an alphanumeric literal.
     * @param token The token to evaluate.
     * @return True if {@code token} is alphanumeric, false otherwise.
     */
    public static boolean isAlphanumeric(final String token) {
        return ALPHA_LITERAL_PATTERN.matcher(token).matches();
    }

    /**
     * Indicates if the given token is a syntactically valid function name. Does <em>not</em>
     * indicate if a function with that name actually exists!
     * @param token The token to evaluate.
     * @return True if {@code token} is a syntactically valid function name,  false otherwise.
     */
    public static boolean isFunctionName(final String token) {
        return FUNCTION_NAME_PATTERN.matcher(token).matches();
    }

    /**
     * Indicates if the given token represents a numeric literal. Currently, numeric literals
     * can only be signed integers.
     * @param token The token to evaluate.
     * @return True if {@code token} is a numeric literal, false otherwise.
     */
    public static boolean isNumeric(final String token) {
        return NUMERIC_LITERAL_PATTERN.matcher(token).matches();
    }
}
