package org.ulithi.jlisp.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The {@link Lexer} forms LISP language tokens from an {@link InputStream} or a {@link String} of
 * characters representing a LISP program or expression to be evaluated.
 */
public class Lexer {

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
        final StringBuilder text = new StringBuilder();

        while (stream.available() > 0 && stream.read(bytes, 0, 1024) > 0) {
            text.append(new String(bytes).trim().toUpperCase());
            Arrays.fill(bytes, (byte) 0);
        }

        tokens = tokenize(text.toString());
    }

    /**
     * Constructs a {@link Lexer} to produce tokens from a given input string.
     * @param s A string -- presumably representing a LISP program or expression -- to be tokenized.
     */
    public Lexer(final String s) {
        tokens = tokenize(s);
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
     * Transforms a string into an ordered list of Lisp language tokens.

     * @param s A string representing (presumably) a Lisp language statement or program.
     * @return An ordered list of Lisp language tokens extracted from the given string.
     */
    private static List<String> tokenize(final String s) {
        if (s.length() == 1) {
            return Collections.singletonList(s);
        }

        int i = 0;
        final List<String> tokens = new ArrayList<>();

        while (i < s.length()) {
            int j = i + 1;
            if (s.substring(i, j).matches(Grammar.LETTER) || s.substring(i, j).matches(Grammar.NUMERIC_LITERAL_START)) {
                while (j < s.length() &&
                       (s.substring(i,j + 1).matches(Grammar.ALPHA_LITERAL) || s.substring(i, j + 1).matches(Grammar.NUMERIC_LITERAL))) {
                    j++;
                }
                tokens.add(s.substring(i,j));
            } else if (s.substring(i, j).matches(Grammar.SYMBOL)) {
                tokens.add(s.substring(i,j));
            }
            i = j;
        }
        return tokens;
    }
}
