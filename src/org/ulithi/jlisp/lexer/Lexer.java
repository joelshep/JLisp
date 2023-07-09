package org.ulithi.jlisp.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main class for the Lisp lexical analyzer. Its job is to break
 * apart the meaningful symbols in a Lisp program and put them into an array
 * for parsing.
 */
public class Lexer {
	private String programString = "";
	private final List<String> tokenArray;

	/**
	 * Constructor which can take a stream as input. It reads the stream and tokenizes
	 * it appropriately.
	 *
	 * @param stream An input stream
	 */
	public Lexer(final InputStream stream) throws IOException {
		final byte[] bytes = new byte[1024];
		while (stream.available() > 0) {
	        stream.read(bytes, 0, 1024);
	        programString = programString.concat(new String(bytes)).trim().toUpperCase();
        }

        tokenArray = tokenize(programString);
	}

	/**
	 * Function: Lexer
	 *
	 * @param s A string of the program to be analyzed
	 */
	public Lexer(final String s) {
		programString = s;
		tokenArray = tokenize(programString);
	}

	/**
	 * Returns the tokens gleaned from the input program.
	 *
	 * @return The list of string tokens.
	 */
	public List<String> getTokens() {
		return tokenArray;
	}

	/**
	 * Function: tokenize
	 *
	 * Splits a string up into chunks meaninful according to Lisp semantics

	 * @param s A string to be separated according to the Lisp semantics
	 */

	private static List<String> tokenize(final String s) {
		int i = 0;
		List<String> tokens = new ArrayList<>();

		if (s.length() == 1) {
			tokens.add(s);
			return tokens;
		}

		while (i < s.length()) {
			int j = i + 1;
			if (s.substring(i, j).matches(LexerPatterns.LETTER) || s.substring(i, j).matches(LexerPatterns.NUMERIC_ATOM)) {
				while (s.substring(i,j + 1).matches(LexerPatterns.LITERAL) || s.substring(i, j + 1).matches(LexerPatterns.NUMERIC_ATOM)) {
					j++;
				}
				tokens.add(s.substring(i,j));
			} else if (s.substring(i, j).matches(LexerPatterns.SYMBOL)) {
				tokens.add(s.substring(i,j));
			}
			i = j;
		}
		return tokens;
	}

	/**
	 * This function finds the index in a given string vector which denotes the closing
	 * parenthesis of a lisp expression
	 *
	 * @deprecated Not necessary as of this version. Moved to the parser to be more effective overall
	 *
	 * @param s A string vector which will be analyzed. The first character must be the left parenthesis
	 *
	 * @throws IllegalArgumentException If the passed vector is not a parenthetical statement
	 * @throws ArrayIndexOutOfBoundsException If the expression is malformed and the statement has no closing parenthesis
	 */

	private int endOfExpression(final List<String> s) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
		if (! s.get(0).matches("[(]")) {
			throw new IllegalArgumentException("ERROR.");
		}

		int openPairs = 1;
		int end = 1;
		while (openPairs > 0) {
			if (end >= s.size()) {
				throw new ArrayIndexOutOfBoundsException("Error: Unbalanced parentheses");
			}
			if (s.get(end).matches("[)]")) {
				openPairs--;
			} else if (s.get(end).matches("[(]")) {
				openPairs++;
			}

			if (openPairs == 0) {
				break;
			} else {
				end++;
			}
		}
		return end;
	}
}
