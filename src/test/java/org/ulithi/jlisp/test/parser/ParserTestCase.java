package org.ulithi.jlisp.test.parser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ulithi.jlisp.exception.ParseException;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.parser.Parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Basic unit tests for {@link org.ulithi.jlisp.parser.Parser} and related classes. At the
 * moment, these tests simply parse a selection of valid expressions and verify the resulting
 * dotted-pair expression matches what is expected.
 */
public class ParserTestCase {

    /** Parser reference, re-initialized with each test. */
    private Parser parser;

    /** Creates a new {@link Parser} instance before each test. */
    @Before
    public void setUp() {
        this.parser = new Parser();
    }

    /** De-allocates the {@link Parser} instance after each test. */
    @After
    public void tearDown() {
        this.parser = null;
    }

    @Test
    public void testUnbalancedExtraParenthesis() {
        final List<String> tokens = Arrays.asList("(", "(", "CAR", "(", "QUOTE", "FOO", ")", ")");

        try {
            parser.parse(tokens);
            fail("Parser should throw exception if unbalanced parentheses");
        } catch (final ParseException e) {
            // Expected.
            assertEquals("Mismatched parentheses", e.getMessage());
        }
    }

    @Test
    public void testUnbalancedMissingParenthesis() {
        final List<String> tokens = Arrays.asList("(", "CAR", "(", "QUOTE", "FOO", ")");

        try {
            parser.parse(tokens);
            fail("Parser should throw exception if unbalanced parentheses");
        } catch (final ParseException e) {
            // Expected.
            assertEquals("Mismatched parentheses", e.getMessage());
        }
    }

    /**
     * Parses a numeric literal.
     */
    @Test
    public void parseNumericLiteral() {
        // 43 => (43 . null)
        final List<String> tokens = Collections.singletonList("43");
        final String expected = "(43 . null)";
        parseAndValidate(parser, tokens, expected);
    }

    /**
     * Parses an empty list.
     */
    @Test
    public void parseEmptyList() {
        // ( ) => (NIL . null)
        final List<String> tokens = Arrays.asList("(", ")");
        final String expected = "(NIL . NIL)";
        parseAndValidate(parser, tokens, expected);
    }

    /**
     * Parses a single element list.
     */
    @Test
    public void parseSingleElementList() {
        // ( FOO ) => (FOO . NIL)
        final List<String> tokens = Arrays.asList("(", "FOO", ")");
        final String expected = "(FOO . NIL)";
        parseAndValidate(parser, tokens, expected);
    }

    /**
     * Parses a simple list of tokens.
     */
    @Test
    public void parseSimpleList() {
        // ( + 1 2 4 )  =>  (+ . (1 . (2 . (4 . NIL))))
        final List<String> tokens = Arrays.asList("(", "+", "1", "2", "4", ")");
        final String expected = "(+ . (1 . (2 . (4 . NIL))))";
        parseAndValidate(parser, tokens, expected);
    }

    /**
     * Parses a list with a nested list.
     */
    @Test
    public void parseNestedList() {
        // ( + 2 ( * 5 9 ) )  =>  (+ . (2 . ((* . (5 . (9 . NIL))) . NIL)))
        final List<String> tokens = Arrays.asList("(", "+", "2", "(", "*", "5", "9", ")", ")");
        final String expected = "(+ . (2 . ((* . (5 . (9 . NIL))) . NIL)))";
        parseAndValidate(parser, tokens, expected);
    }

    /**
     * Parses a list containing two nested lists.
     */
    @Test
    public void parseListOfLists() {
        // ( ( 2 3 ) ( 4 5 ) )
        final List<String> tokens = Arrays.asList("(", "(", "2", "3", ")", "(", "4", "5", ")", ")");
        final String expected = "((2 . (3 . NIL)) . ((4 . (5 . NIL)) . NIL))";
        parseAndValidate(parser, tokens, expected);
    }

    @Test
    public void testOperatorAndListOperands() {
        // (+ (* 2 3) (* 4 5))
        final List<String> tokens = Arrays.asList("(", "+", "(", "*", "4", "5", ")", "(", "*", "2", "3", ")", ")");
        final String expected = "(+ . ((* . (4 . (5 . NIL))) . ((* . (2 . (3 . NIL))) . NIL)))";
        parseAndValidate(parser, tokens, expected);
    }

    /**
     * Parses a list that has a nested list as its first element.
     */
    @Test
    public void parseInitialNestedList() {
        // ( ( 2 3 ) 4 )  =>  ((2. (3 . NIL)) . (4 . NIL))
        final List<String> tokens = Arrays.asList("(", "(", "2", "3", ")", "4", ")");
        final String expected = "((2 . (3 . NIL)) . (4 . NIL))";
        parseAndValidate(parser, tokens, expected);
    }

    /**
     * Parses the given list of tokens and compares the resulting dotted-pair representation
     * to the expected result.
     *
     * @param parser The parser.
     * @param tokens The list of tokens to parse.
     * @param expected The dotted-pair expression the parsed tokens are expected to produce.
     */
    private static void parseAndValidate(final Parser parser,
                                         final List<String> tokens,
                                         final String expected) {
        PTree ptree = parser.parse(tokens);
        final String dpExpression = ptree.toString();
        assertEquals(expected, dpExpression);
        System.out.println(ptree);
    }
}
