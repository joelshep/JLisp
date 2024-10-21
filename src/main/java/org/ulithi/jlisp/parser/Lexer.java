package org.ulithi.jlisp.parser;

import org.ulithi.jlisp.exception.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The {@link Lexer} forms LISP language tokens from an {@link InputStream} or a {@link String} of
 * characters representing a LISP program or expression to be evaluated.
 * <p>
 * Aside from basic tokenization, the {@code Lexer} also handles expansion of the single-quote ( ' )
 * shorthand for the QUOTE function: e.g., transforms 'A in the input to "(", "QUOTE", "A", ")" in
 * the generated list of tokens.
 * <p>
 * I originally tried to do this in the {@link Parser}, but that proved more challenging since it
 * involves some amount of look-ahead to determine what is being quoted. It's easier to do hear
 * than to deal with the complexity of the {@code PTree} being generated by the parser.
 */
public class Lexer {

    /**
     * To handle multi-line inputs, the lexer can accept a series of inputs (i.e. partially complete
     * forms), maintain scanning state across them, and indicate when a full form has been input
     * and is ready for parsing.
     */
    private static class State {
        /** The ordered list of tokens produced by lexical analysis. */
        final List<String> tokens = new ArrayList<>();

        int depth = 0;               // Parenthesis depth of all tokenized input.
        boolean inQuote = false;     // Are we processing quoted (') input?
        int quoteDepth = 0;          // Parenthesis depth of quoted (') input.
        boolean expectAtom = false;  // Whether we expect the quoted (') input to be an atom.
        boolean inComment = false;   // Are we processing commented-out input?

        private boolean isComplete() {
            return depth == 0 && !tokens.isEmpty();
        }
    }

    /** Scanning state for the form/input currently being processed. */
    private State state;

    /**
     * Constructs a new Lexer instance.
     */
    public Lexer() {
         state = new State();
    }

    /**
     * Reads the given {@link InputStream} stream as a string of characters and tokenizes it.
     * @param stream An input stream.
     */
    public void append(final InputStream stream) throws IOException {
        final byte[] bytes = new byte[1024];
        final StringBuilder text = new StringBuilder();

        while (stream.available() > 0 && stream.read(bytes, 0, 1024) > 0) {
            text.append(new String(bytes).trim().toUpperCase());
            Arrays.fill(bytes, (byte) 0);
        }

        append(text.toString());
    }

    /**
     * Reads the given string and tokenizes it.
     * @param s A string of LISP program text.
     */
    public void append(final String s) {
        try {
            tokenize(s, state);
        } catch (final ParseException e) {
            reset();
            throw e;
        }
    }

    /**
     * Resets this {@link Lexer} by clearing its accumulated list of tokens and related scanning
     * state.
     */
    public void reset() {
        state = new State();
    }

    /**
     * Indicates if the form currently being processed is a complete (but not necessarily
     * syntactically correct) form.
     */
    public boolean isComplete() {
        return state.isComplete();
    }

    /**
     * Returns the tokens formed from the stream or string by this {@link Lexer}.
     *
     * @return An ordered list of tokens.
     */
    public List<String> getTokens() {
        return Collections.unmodifiableList(state.tokens);
    }

    public boolean hasTokens() {
        return !state.tokens.isEmpty();
    }

    /**
     * Transforms a string into an ordered list of Lisp language tokens, based on the current
     * state of the lexer. The lexer state is updated by this method as the input is being
     * scanned and processed.
     *
     * @param s A string representing (presumably) a Lisp language statement or program.
     * @param state Represents the current state of the lexer, including tokens scanned for the
     *              expression currently being processed, quote state and so on.
     */
    private static void tokenize(final String s, final State state) {
        final List<String> tokens = new ArrayList<>();
        int i = 0;

        while (i < s.length()) {
            int j = i + 1;
            final String ch = s.substring(i, j);

            if (ch.equals(Grammar.EOL)) {
                state.inComment = false;
            } else if (state.inComment) {
                // Continue
            } else if (ch.equals(Grammar.SEMI)) {
                state.inComment = true;
            } else if (ch.matches(Grammar.QUOTE) && !state.inQuote) {
                tokens.add(Grammar.LPAREN);
                tokens.add("QUOTE");
                state.inQuote = true;
                state.expectAtom = true;
            } else if (ch.matches(Grammar.LETTER) || ch.matches(Grammar.NUMERIC_LITERAL_START)) {
                while (j < s.length() &&
                        (s.substring(i, j + 1).matches(Grammar.ALPHA_LITERAL) || s.substring(i, j + 1).matches(Grammar.NUMERIC_LITERAL))) {
                    j++;
                }
                tokens.add(s.substring(i, j));

                if (state.expectAtom) {
                    tokens.add(Grammar.RPAREN);
                    state.inQuote = false;
                    state.expectAtom = false;
                }
            } else if (ch.equals(Grammar.LPAREN)) {
                state.expectAtom = false;
                if (state.inQuote) { state.quoteDepth++; } else { state.depth++; }
                tokens.add(ch);
            } else if (ch.equals(Grammar.RPAREN)) {
                if (state.inQuote) {
                    state.quoteDepth--;
                    if (state.quoteDepth == 0) {
                        tokens.add(Grammar.RPAREN);
                        state.inQuote = false;
                    }
                } else {
                    state.depth--;
                }
                tokens.add(ch);
            } else if (ch.matches(Grammar.SYMBOL)) {
                tokens.add(ch);
            }
            i = j;
        }

        // Too many closing parentheses is not salvageable.
        if (state.depth < 0) {
            throw new ParseException("Mismatched parentheses");
        }

        state.tokens.addAll(tokens);
    }
}
