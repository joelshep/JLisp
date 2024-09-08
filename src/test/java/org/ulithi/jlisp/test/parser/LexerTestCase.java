package org.ulithi.jlisp.test.parser;

import org.junit.Ignore;
import org.junit.Test;
import org.ulithi.jlisp.parser.Lexer;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link org.ulithi.jlisp.parser.Lexer}.
 */
public class LexerTestCase {

    /**
     * Tokenizes simple atoms and un-nested lists, including cases with extra whitespace.
     */
    @Test
    public void testTokenizeSimpleExpressions() {
        assertEquals(toList("(", "PLUS", "2", "3", ")"),
                     tokenize("(PLUS 2 3)"));
        assertEquals(toList("(", "PLUS", "3", "4", ")"),
                     tokenize("( PLUS 3 4 )"));
        assertEquals(toList("(", ")"),
                     tokenize("()"));
        assertEquals(toList("(", ")"),
                     tokenize(" ( ) "));
        assertEquals(toList("HELLO"),
                     tokenize("HELLO"));
        assertEquals(toList("FOO"),
                     tokenize(" FOO "));
        assertEquals(toList("123"),
                     tokenize("123"));
        assertEquals(toList("456"),
                     tokenize("  456 "));
        assertEquals(toList("-123"),
                     tokenize("-123"));
        assertEquals(toList("A"),
                     tokenize("A"));
    }

    /**
     * Tokenizes an atom prefixed by a single quote (parser will transform this to a QUOTE
     * function invocation).
     */
    @Test
    public void testTokenizeSingleQuoteAtom() {
        assertEquals(toList("'", "FOO"), tokenize("'FOO"));
    }

    /**
     * Tokenizes a simple list prefixed by a single quote (parser will transform this to a QUOTE
     * function invocation).
     */
    @Test
    public void testTokenizeSingleQuoteList() {
        assertEquals(toList("'", "(", "FOO", "BAR", ")"),
                     tokenize("'(FOO BAR)"));
    }

    /**
     * Tokenizes a variety of nested expressions.
     */
    @Test
    public void testTokenizeNestedExpressions() {
        assertEquals(toList("(", "A", "(", "B", "C", ")", ")"),
                     tokenize("( A ( B C ) )"));
        assertEquals(toList("(", "A", "(", ")", ")"),
                     tokenize("(A () )"));
        assertEquals(toList("(", "(","A",  ")", ")"),
                     tokenize("( (A) )"));
        assertEquals(toList("(", "A", "(", "B", "C", ")", "(", "C", "D", ")", ")"),
                     tokenize("(A (B C) (C D ) )"));
        assertEquals(toList("(", "A", "(", "B", "C", "(", "C", "D", ")", ")"),
                     tokenize("(A (B C (C D ) )"));
    }

    /**
     * These test cases should pass but currently don't due to limitations in the current
     * lexer implementation.
     */
    @Test
    @Ignore
    public void brokenTestCases() {
        assertEquals(toList("this-is-an-atom"),
                     tokenize("this-is-an-atom"));
    }

    /**
     * Invokes the lexer on the given string and returns the resulting token list.
     *
     * @param expr A string.
     * @return An ordered list of tokens extracted from the given string.
     */
    private static List<String> tokenize(final String expr) {
        return (new Lexer(expr)).getTokens();
    }

    private static List<String> toList(final String... tokens) {
        return Arrays.asList(tokens);
    }
}
