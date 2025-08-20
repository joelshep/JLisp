package org.ulithi.jlisp.test.parser;

import org.junit.Test;
import org.ulithi.jlisp.exception.ParseException;
import org.ulithi.jlisp.parser.Lexer;
import org.ulithi.jlisp.parser.Token;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
                     toTokenValues("(PLUS 2 3)"));
        assertEquals(toList("(", "PLUS", "3", "4", ")"),
                     toTokenValues("( PLUS 3 4 )"));
        assertEquals(toList("(", ")"),
                     toTokenValues("()"));
        assertEquals(toList("(", ")"),
                     toTokenValues(" ( ) "));
        assertEquals(toList("HELLO"),
                     toTokenValues("HELLO"));
        assertEquals(toList("FOO"),
                     toTokenValues(" FOO "));
        assertEquals(toList("123"),
                     toTokenValues("123"));
        assertEquals(toList("456"),
                     toTokenValues("  456 "));
        assertEquals(toList("-123"),
                     toTokenValues("-123"));
        assertEquals(toList("A"),
                     toTokenValues("A"));
    }

    /**
     * Tokenizes a variety of nested expressions.
     */
    @Test
    public void testTokenizeNestedExpressions() {
        assertEquals(toList("(", "A", "(", "B", "C", ")", ")"),
                     toTokenValues("( A ( B C ) )"));
        assertEquals(toList("(", "A", "(", ")", ")"),
                     toTokenValues("(A () )"));
        assertEquals(toList("(", "(","A",  ")", ")"),
                     toTokenValues("( (A) )"));
        assertEquals(toList("(", "A", "(", "B", "C", ")", "(", "C", "D", ")", ")"),
                     toTokenValues("(A (B C) (C D ) )"));
        assertEquals(toList("(", "A", "(", "B", "C", "(", "C", "D", ")", ")"),
                     toTokenValues("(A (B C (C D ) )"));
    }

    @Test
    public void testTokenizeHyphenatedAtom() {
        assertEquals(toList("this-is-an-atom"),
                     toTokenValues("this-is-an-atom"));
    }

    @Test
    public void testTokenizeSingleQuoteAtom() {
        // 'A
        assertEquals(toList("(", "QUOTE", "A", ")"), toTokenValues("'A"));

        // 'FOO
        assertEquals(toList("(", "QUOTE", "FOO", ")"), toTokenValues("'FOO"));
    }

    @Test
    public void testTokenizeSingleQuoteList() {
        // '(FOO BAR)
        assertEquals(toList("(", "QUOTE", "(", "FOO", "BAR", ")", ")"),
                     toTokenValues("'(FOO BAR)"));

        // '(1 2 3)
        assertEquals(toList("(", "QUOTE", "(", "1", "2", "3", ")", ")"),
                     toTokenValues("'(1 2 3)"));
    }

    @Test
    public void testQuoteMultipleAtoms() {
        // (LIST 'A 'B 'C)
        assertEquals(toList("(", "LIST", "(", "QUOTE", "A", ")", "(", "QUOTE", "B", ")", "(", "QUOTE", "C", ")", ")"),
                     toTokenValues("(LIST 'A 'B 'C)"));
    }

    @Test
    public void testQuoteMixed() {
        // (CAR (CONS 'A '(B C)))
        assertEquals(toList("(", "CAR", "(", "CONS", "(", "QUOTE", "A", ")", "(", "QUOTE", "(", "B", "C", ")", ")", ")", ")"),
                     toTokenValues("(CAR (CONS 'A '(B C)))"));
    }

    @Test
    public void testQuoteMultipleLists() {
        // (LIST '(A B) '(C D) 'E)
        assertEquals(toList("(", "LIST", "(", "QUOTE", "(", "A", "B", ")", ")", "(", "QUOTE", "(", "C", "D", ")", ")", "(", "QUOTE", "E", ")", ")"),
                     toTokenValues("(LIST '(A B) '(C D) 'E)"));
    }

    @Test
    public void testNestedQuote() {
        // (LIST 'A 'B '(C 'D E))
        assertEquals(toList("(", "LIST", "(", "QUOTE", "A", ")", "(", "QUOTE", "B", ")", "(", "QUOTE", "(", "C", "'", "D", "E", ")", ")", ")"),
                     toTokenValues("(LIST 'A 'B '(C 'D E))"));
    }

    @Test
    public void testFullLineComment() {
        final List<String> tokens = toTokenValues("; (A B C)   ");
        assertTrue(tokens.isEmpty());
    }

    @Test
    public void testEndOfLineComment() {
        assertEquals(toList("(", "+", "1", "2", "3", ")"),
                     toTokenValues("(+ 1 2 3) ; Add some numbers"));
    }

    @Test
    public void testNewlineTerminatesComment() {
        assertEquals(toList("(", "+", "1", "2", ")", "(", "+", "3", "4", ")"),
                     toTokenValues("(+ 1 2) ; Multi-line input, yo\n(+ 3 4)"));
    }

    @Test
    public void testTokenizeQuotedString() {
        assertEquals(toList("(", "PRINT", "Hello World", ")"),
                     toTokenValues("(PRINT \"Hello World\")"));
    }

    @Test(expected = ParseException.class)
    public void testTooManyCloseParensThrows() {
        toTokenValues("(+ 1 2 (* 2 3)))");
    }

    @Test
    public void testMissingCloseParensIsIncompleteForm() {
        final Lexer lexer = new Lexer();
        lexer.append("(+ 1 2 (* 2 3)");
        assertFalse(lexer.isComplete());
    }

    @Test
    public void testMultiAppendExpression() {
        final Lexer lexer = new Lexer();
        lexer.append("(\n");
        assertFalse(lexer.isComplete());
        lexer.append("+ 2\n");
        assertFalse(lexer.isComplete());
        lexer.append("3\n");
        assertFalse(lexer.isComplete());
        lexer.append(")\n");
        assertTrue(lexer.isComplete());
        assertEquals(toList("(", "+", "2", "3", ")"), detokenize(lexer.getTokens()));
    }

    @Test
    public void testMultiAppendExpressionWithQuote() {
        final Lexer lexer = new Lexer();
        lexer.append("(CAR\n");
        lexer.append(" '( 1 2\n");
        lexer.append("3");
        lexer.append("))\n");
        assertTrue(lexer.isComplete());
        assertEquals(toList("(", "CAR", "(", "QUOTE", "(", "1", "2", "3", ")", ")", ")"),
                     detokenize(lexer.getTokens()));
    }

    @Test
    public void testMultiAppendExpressionWithComment() {
        final Lexer lexer = new Lexer();
        lexer.append("(+\n");
        lexer.append(" '( 1 2  ;; Oops - forgot the 3\n");
        lexer.append("3");
        lexer.append("))\n");
        assertTrue(lexer.isComplete());
        assertEquals(toList("(", "+", "(", "QUOTE", "(", "1", "2", "3", ")", ")", ")"),
                     detokenize(lexer.getTokens()));
    }

    @Test
    public void testSimpleDottedPair() {
        final List<Token> tokens = toTokens("(a . b)");
        assertEquals(5, tokens.size());
        assertEquals(Token.LPAREN, tokens.get(0));
        assertEquals("a", tokens.get(1).value());
        assertEquals(Token.DOT, tokens.get(2));
        assertEquals("b", tokens.get(3).value());
        assertEquals(Token.RPAREN, tokens.get(4));
    }

    @Test
    public void testNestedDottedPair() {
        final List<Token> tokens = toTokens("((1 . 2) . 3)");
        assertEquals(9, tokens.size());
        assertEquals(Token.LPAREN, tokens.get(0));
        assertEquals(Token.LPAREN, tokens.get(1));
        assertEquals("1", tokens.get(2).value());
        assertEquals(Token.DOT, tokens.get(3));
        assertEquals("2", tokens.get(4).value());
        assertEquals(Token.RPAREN, tokens.get(5));
        assertEquals(Token.DOT, tokens.get(6));
        assertEquals("3", tokens.get(7).value());
        assertEquals(Token.RPAREN, tokens.get(8));
    }

    @Test
    public void testDottedPairWithList() {
        final List<Token> tokens = toTokens("(a . (b c))");
        assertEquals(8, tokens.size());
        assertEquals(Token.LPAREN, tokens.get(0));
        assertEquals("a", tokens.get(1).value());
        assertEquals(Token.DOT, tokens.get(2));
        assertEquals(Token.LPAREN, tokens.get(3));
        assertEquals("b", tokens.get(4).value());
        assertEquals("c", tokens.get(5).value());
        assertEquals(Token.RPAREN, tokens.get(6));
        assertEquals(Token.RPAREN, tokens.get(7));
    }

    @Test
    public void testDotInSymbolName() {
        final List<Token> tokens = toTokens("my.symbol");
        assertEquals(1, tokens.size());
        assertTrue(tokens.get(0).isSymbol());
        assertEquals("my.symbol", tokens.get(0).value());
    }

    @Test
    public void testDotWithWhitespace() {
        final List<Token> tokens = toTokens("(a  .  b)");
        assertEquals(5, tokens.size());
        assertEquals(Token.LPAREN, tokens.get(0));
        assertEquals("a", tokens.get(1).value());
        assertEquals(Token.DOT, tokens.get(2));
        assertEquals("b", tokens.get(3).value());
        assertEquals(Token.RPAREN, tokens.get(4));
    }

    @Test
    public void testMultipleDottedPairs() {
        final List<Token> tokens = toTokens("(a . b) (c . d)");
        assertEquals(10, tokens.size());
        assertEquals(Token.LPAREN, tokens.get(0));
        assertEquals("a", tokens.get(1).value());
        assertEquals(Token.DOT, tokens.get(2));
        assertEquals("b", tokens.get(3).value());
        assertEquals(Token.RPAREN, tokens.get(4));
        assertEquals(Token.LPAREN, tokens.get(5));
        assertEquals("c", tokens.get(6).value());
        assertEquals(Token.DOT, tokens.get(7));
        assertEquals("d", tokens.get(8).value());
        assertEquals(Token.RPAREN, tokens.get(9));
    }

    @Test
    public void testNewLexer() {
        final Lexer lexer = new Lexer();
        assertFalse(lexer.isComplete());
        assertTrue(lexer.getTokens().isEmpty());
    }

    @Test
    public void testLexerReset() {
        final Lexer lexer = new Lexer();
        lexer.append("(+ 1 2");
        assertFalse(lexer.isComplete());
        assertFalse(lexer.getTokens().isEmpty());
        lexer.reset();
        assertFalse(lexer.isComplete());
        assertTrue(lexer.getTokens().isEmpty());
    }

    /**
     * Invokes the lexer on the given string and returns the resulting token list,
     * representing each token with its original value as a String.
     *
     * @param expr A string.
     * @return An ordered list of tokens extracted from the given string.
     */
    private static List<String> toTokenValues(final String expr) {
        return detokenize(toTokens(expr));
    }

    /**
     * Tokenizes the given string.
     * @param expr A string representing a LISP form or expression.
     * @return An ordered list of tokens extracted from the form.
     */
    private static List<Token> toTokens(final String expr) {
        final Lexer lexer = new Lexer();
        lexer.append(expr);
        return lexer.getTokens();
    }

    /**
     * Transforms a list of Tokens to an equivalent list of Strings representing the values of
     * the tokens.
     * @param tokens A list of Tokens.
     * @return A list of Strings representing the values of the Tokens.
     */
    private static List<String> detokenize(final List<Token> tokens) {
        return tokens.stream().map(Token::value).toList();
    }

    /**
     * @return A Java List containing one element for each provided String token.
     */
    private static List<String> toList(final String... tokens) {
        return Arrays.asList(tokens);
    }
}
