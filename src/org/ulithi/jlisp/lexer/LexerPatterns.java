package org.ulithi.jlisp.lexer;

/**
 * Regular expressions which represent meaningful and acceptable
 * strings in a Lisp program.
 */
class LexerPatterns {
	public static final String LETTER = "[a-zA-Z]";
	public static final String LITERAL = "[a-zA-Z0-9]+";
	public static final String WHITESPACE = "[\\s]+";
	public static final String NUMERIC_ATOM = "[\\d\\+\\-]?[\\d]*";
	public static final String SYMBOL = "[().]";
}
