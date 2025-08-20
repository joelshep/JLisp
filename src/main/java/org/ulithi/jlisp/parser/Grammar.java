package org.ulithi.jlisp.parser;

import java.util.HashSet;
import java.util.Set;
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
 * Some character classes are defined as {@code Sets} of characters instead of or in addition to
 * regular expressions, to optimize lexing performance.
 */
public class Grammar {

    /** Static methods only: do not instantiate. */
    private Grammar() { }

    /** Regular expression for the first character of an identifier (variable, symbol, function). */
    public static final String IDENTIFIER_START = "[a-zA-Z!$%&+*/:<=>?~_^]";

    public static final Pattern IDENTIFIER_START_PATTERN = Pattern.compile(IDENTIFIER_START);

    /** Valid initial characters for identifiers (in addition to alphabetic characters). */
    public static final Set<Character> IDENTIFIER_START_CHARS = new HashSet<>();

    static {
        IDENTIFIER_START_CHARS.add('!');
        IDENTIFIER_START_CHARS.add('$');
        IDENTIFIER_START_CHARS.add('%');
        IDENTIFIER_START_CHARS.add('&');
        IDENTIFIER_START_CHARS.add('+');
        IDENTIFIER_START_CHARS.add('*');
        IDENTIFIER_START_CHARS.add('/');
        IDENTIFIER_START_CHARS.add(':');
        IDENTIFIER_START_CHARS.add('<');
        IDENTIFIER_START_CHARS.add('=');
        IDENTIFIER_START_CHARS.add('>');
        IDENTIFIER_START_CHARS.add('?');
        IDENTIFIER_START_CHARS.add('~');
        IDENTIFIER_START_CHARS.add('_');
        IDENTIFIER_START_CHARS.add('^');
    }

    /** Regular expression for subsequent characters of an identifier */
    public static final String IDENTIFIER_REST = "[a-zA-Z!$%&*/:<=>?~_^0-9.+-]+";

    public static final Pattern IDENTIFIER_REST_PATTERN = Pattern.compile(IDENTIFIER_REST);

    /** Special syntax elements. */
    public static final Set<Character> SYMBOLS = new HashSet<>();

    static {
        SYMBOLS.add('(');
        SYMBOLS.add(')');
        SYMBOLS.add('\'');
    }

    /** Regular expression for a number (integer). */
    public static final String NUMERIC_LITERAL = "[+\\-]?\\d+";

    /** Pre-compiled Pattern for matching on the NUMERIC_LITERAL regex. */
    private static final Pattern NUMERIC_LITERAL_PATTERN = Pattern.compile(NUMERIC_LITERAL);

    /** Regular expression for line/expression that is whitespace and a comment. */
    public static final String FULL_LINE_COMMENT = "^\\s*;+.*$";

    /** Single quote (shorthand for QUOTE) */
    public static final char QUOTE = '\'';

    /** Double quote. */
    public static final char DOUBLE_QUOTE = '"';

    /** Left parenthesis. */
    public static final String LPAREN = "(";
    public static final char LPAREN_CHAR = '(';

    /** Right parenthesis. */
    public static final String RPAREN = ")";
    public static final char RPAREN_CHAR = ')';

    /** Dot symbol (for S-Expressions) */
    public static final String DOT = ".";
    public static final char DOT_CHAR = '.';

    /** Semicolon (starts a rest-of-line comment). */
    public static final char SEMI = ';';

    /** End-of-line character. */
    public static final char EOL = '\n';

    /** A single space character. */
    public static final String SPACE = " ";

    /** The NIL token. */
    public static final String NIL = "NIL";

    /** The T (true) token. */
    public static final String T = "T";

    /** The F (false) token. */
    public static final String F = "F";

    /**
     * Indicates if the given token is a syntactically valid function name. Does <em>not</em>
     * indicate if a function with that name actually exists!
     * @param token The token to evaluate.
     * @return True if {@code token} is a syntactically valid function name,  false otherwise.
     */
    public static boolean isFunctionName(final String token) {
        if (token == null || token.isBlank()) { return false; }

        return (IDENTIFIER_START_PATTERN.matcher(token.substring(0, 1)).matches())
               &&
               (token.length() == 1 || IDENTIFIER_REST_PATTERN.matcher(token.substring(1)).matches());
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
