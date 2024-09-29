package org.ulithi.jlisp.test.parser;

import org.junit.Ignore;
import org.junit.Test;
import org.ulithi.jlisp.parser.Lexer;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link org.ulithi.jlisp.parser.Lexer}. There are lots of tests for handling
 * single-quotes as shorthand for QUOTE: for whatever reason, I had a hard time getting that
 * right.
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

    @Test
    public void testTokenizeSingleQuoteAtom() {
        // 'A
        assertEquals(toList("(", "QUOTE", "A", ")"), tokenize("'A"));

        // 'FOO
        assertEquals(toList("(", "QUOTE", "FOO", ")"), tokenize("'FOO"));
    }

    @Test
    public void testTokenizeSingleQuoteList() {
        // '(FOO BAR)
        assertEquals(toList("(", "QUOTE", "(", "FOO", "BAR", ")", ")"),
                     tokenize("'(FOO BAR)"));

        // '(1 2 3)
        assertEquals(toList("(", "QUOTE", "(", "1", "2", "3", ")", ")"),
                     tokenize("'(1 2 3)"));
    }

    @Test
    public void testQuoteMultipleAtoms() {
        // (LIST 'A 'B 'C)
        assertEquals(toList("(", "LIST", "(", "QUOTE", "A", ")", "(", "QUOTE", "B", ")", "(", "QUOTE", "C", ")", ")"),
                     tokenize("(LIST 'A 'B 'C)"));
    }

    @Test
    public void testQuoteMixed() {
        // (CAR (CONS 'A '(B C)))
        assertEquals(toList("(", "CAR", "(", "CONS", "(", "QUOTE", "A", ")", "(", "QUOTE", "(", "B", "C", ")", ")", ")", ")"),
                     tokenize("(CAR (CONS 'A '(B C)))"));
    }

    @Test
    public void testQuoteMultipleLists() {
        // (LIST '(A B) '(C D) 'E)
        assertEquals(toList("(", "LIST", "(", "QUOTE", "(", "A", "B", ")", ")", "(", "QUOTE", "(", "C", "D", ")", ")", "(", "QUOTE", "E", ")", ")"),
                     tokenize("(LIST '(A B) '(C D) 'E)"));
    }

    @Test
    public void testNestedQuote() {
        // (LIST 'A 'B '(C 'D E))
        assertEquals(toList("(", "LIST", "(", "QUOTE", "A", ")", "(", "QUOTE", "B", ")", "(", "QUOTE", "(", "C", "'", "D", "E", ")", ")", ")"),
                     tokenize("(LIST 'A 'B '(C 'D E))"));
    }

    @Test
    public void testFullLineComment() {
        final List<String> tokens = tokenize("; (A B C)   ");
        assertTrue(tokens.isEmpty());
    }

    @Test
    public void testEndOfLineComment() {
        assertEquals(toList("(", "+", "1", "2", "3", ")"),
                     tokenize("(+ 1 2 3) ; Add some numbers"));
    }

    @Test
    public void testNewlineTerminatesComment() {
        assertEquals(toList("(", "+", "1", "2", ")", "(", "+", "3", "4", ")"),
                     tokenize("(+ 1 2) ; Multi-line input, yo\n(+ 3 4)"));
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
