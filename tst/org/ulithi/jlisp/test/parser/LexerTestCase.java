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
     * Tests the lexer on basic atoms and simple (non-nested) lists.
     */
    @Test
    public void tokenizeSimpleExpressions() {
        assertEquals(toList("(", "PLUS", "2", "3", ")"),
                     lexerize("(PLUS 2 3)"));
        assertEquals(toList("(", "PLUS", "3", "4", ")"),
                     lexerize("( PLUS 3 4 )"));
        assertEquals(toList("(", ")"),
                     lexerize("()"));
        assertEquals(toList("(", ")"),
                     lexerize(" ( ) "));
        assertEquals(toList("HELLO"),
                     lexerize("HELLO"));
        assertEquals(toList("123"),
                     lexerize("123"));
        assertEquals(toList("-123"),
                     lexerize("-123"));
        assertEquals(toList("A"),
                     lexerize("A"));
    }

    /**
     * Tests the lexer on a variety of nested expressions
     */
    @Test
    public void tokenizeNestedExpressions() {
        assertEquals(toList("(", "A", "(", "B", "C", ")", ")"),
                     lexerize("( A ( B C ) )"));
        assertEquals(toList("(", "A", "(", ")", ")"),
                     lexerize("(A () )"));
        assertEquals(toList("(", "(","A",  ")", ")"),
                     lexerize("( (A) )"));
        assertEquals(toList("(", "A", "(", "B", "C", ")", "(", "C", "D", ")", ")"),
                     lexerize("(A (B C) (C D ) )"));
        assertEquals(toList("(", "A", "(", "B", "C", "(", "C", "D", ")", ")"),
                     lexerize("(A (B C (C D ) )"));
    }

    /**
     * These test cases should pass but currently don't due to limitations in the current
     * lexer implementation.
     */
    @Test
    @Ignore
    public void brokenTestCases() {
        assertEquals(toList("this-is-an-atom"),
                     lexerize("this-is-an-atom"));
    }

    /**
     * Invokes the lexer on the given string and returns the resulting token list.
     *
     * @param expr A string.
     * @return An ordered list of tokens extracted from the given string.
     */
    private static List<String> lexerize(final String expr) {
        return (new Lexer(expr)).getTokens();
    }

    private static List<String> toList(final String... tokens) {
        return Arrays.asList(tokens);
    }
}
