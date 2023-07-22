package org.ulithi.jlisp.parser;

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
    protected static final String LITERAL = "[a-zA-Z0-9]+";

    /** Regular expression for a function name. */
    protected static final String FUNCTION_NAME = "[a-zA-Z][a-zA-Z0-9]*";

    /** Regular expression for a number (integer). */
    protected static final String NUMERIC_LITERAL = "[\\d\\+\\-]?[\\d]*";

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
}
