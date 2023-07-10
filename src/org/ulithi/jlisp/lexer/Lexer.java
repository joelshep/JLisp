package org.ulithi.jlisp.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@link Lexer} forms LISP language tokens from an {@link InputStream} or a {@link String} of
 * characters representing a LISP program or expression to be evaluated.
 */
public class Lexer {
	/**
	 * The characters to be tokenized.
	 */
	private String text = "";

	/**
	 * The ordered list of tokens produced by lexical analysis.
	 */
	private final List<String> tokens;

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
	        text = text.concat(new String(bytes)).trim().toUpperCase();
        }

        tokens = tokenize(text);
	}

	/**
	 * Constructs a {@link Lexer} to produce tokens from a given input string.
	 * @param s A string -- presumably representing a LISP program or expresssion -- to be tokenized.
	 */
	public Lexer(final String s) {
		text = s;
		tokens = tokenize(text);
	}

	/**
	 * Returns the tokens formed from the stream or string that this {@link Lexer} parsed.
	 *
	 * @return An ordered list of tokens.
	 */
	public List<String> getTokens() {
		return Collections.unmodifiableList(tokens);
	}

	/**
	 * Splits a string up into chunks meaninful according to Lisp semantics

	 * @param s A string to be separated according to the Lisp semantics
	 */
	private static List<String> tokenize(final String s) {
		int i = 0;
		List<String> tokens = new ArrayList<>();

		if (s.length() == 1) {
			return Collections.singletonList(s);
		}

		while (i < s.length()) {
			int j = i + 1;
			if (s.substring(i, j).matches(LexerPatterns.LETTER) || s.substring(i, j).matches(LexerPatterns.NUMERIC_ATOM)) {
				while (j < s.length() && (s.substring(i,j + 1).matches(LexerPatterns.LITERAL) || s.substring(i, j + 1).matches(LexerPatterns.NUMERIC_ATOM))) {
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
}
