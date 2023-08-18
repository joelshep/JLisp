package org.ulithi.jlisp.test.primitive;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ulithi.jlisp.lexer.Lexer;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.parser.Parser;
import org.ulithi.jlisp.primitive.Eval;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Math}.
 */
public class MathTestCase {
    /** A parser, re-initialized with each test. */
    private Parser parser;

    /** An eval function, initialized once (shouldn't carry state over between invocations). */
    private final Eval eval = new Eval();

    /**
     * Creates a new {@link Parser} instance before each test.
     */
    @Before
    public void setUp() {
        this.parser = new Parser();
    }

    /**
     * De-allocates the {@link Parser} instance after each test.
     */
    @After
    public void tearDown() {
        this.parser = null;
    }

    /**
     * Evaluates a simple addition expression: (+ 2 3).
     */
    @Test
    public void testAddTwoNumbers() {
        final List<String> tokens = Arrays.asList("(", "+", "2", "3", ")");
        PTree ptree = parser.parse(tokens);
        System.out.println(ptree);
        int foo = eval.apply(ptree.root()).toAtom().toI();
        System.out.println("Eval returned " + foo);
        assertEquals("(+ 2 3)", 5, foo);
    }

    /**
     * Evaluates an addition expression with an "arbitrary" (not two) number of arguments.
     */
    @Test
    public void testAddFourNumbers() {
        final List<String> tokens = Arrays.asList("(", "+", "2", "3", "4", "5", ")");
        PTree ptree = parser.parse(tokens);
        System.out.println(ptree);
        int foo = eval.apply(ptree.root()).toAtom().toI();
        System.out.println("Eval returned " + foo);
        assertEquals("(+ 2 3 4 5)", 14, foo);
    }

    /**
     * Evaluates an addition expression with a nested sub-expression: (+ 2 (* 2 3))
     */
    @Test
    public void testMultiplyAndAdd() {
        final List<String> tokens = Arrays.asList("(", "+", "2", "(", "*", "2", "3", ")", ")");
        PTree ptree = parser.parse(tokens);
        System.out.println(ptree);
        int foo = eval.apply(ptree.root()).toAtom().toI();
        assertEquals("(+ 2 (* 2 3))", 8, foo);
        System.out.println("Eval returned " + foo);
    }

    /**
     * Evaluates an addition expression with two nested sub-expressions:
     * (+ (* 4 5) (* 2 3))
     */
    @Test
    public void testMultiplyTwiceAndAdd() {
        final List<String> tokens = Arrays.asList("(", "+", "(", "*", "4", "5", ")", "(", "*", "2", "3", ")", ")");
        PTree ptree = parser.parse(tokens);
        System.out.println(ptree);
        int foo = eval.apply(ptree.root()).toAtom().toI();
        assertEquals("(+ (* 4 5) (* 2 3))", 26, foo);
        System.out.println("Eval returned " + foo);
    }

    /**
     * <em>Scans</em>, parses and evaluates an expression with a sub-sub-expression:
     *   (+ (* 4 (+ 2 3)) (* 2 3))
     */
    @Test
    public void testThreeLayers() {
        final String expr = "(+ (* 4 (+ 2 3)) (* 2 3))";
        final List<String> tokens = (new Lexer(expr)).getTokens();
        PTree ptree = parser.parse(tokens);
        System.out.println(ptree);
        int foo = eval.apply(ptree.root()).toAtom().toI();
        assertEquals(expr, 26, foo);
        System.out.println("Eval returned " + foo);
    }

    @Test
    public void testSimpleSubtract() {
        final String expr = "(MINUS 11 5)";
        final List<String> tokens = (new Lexer(expr)).getTokens();
        PTree ptree = parser.parse(tokens);
        System.out.println(ptree);
        int foo = eval.apply(ptree.root()).toAtom().toI();
        assertEquals(expr, 6, foo);
        System.out.println("Eval returned " + foo);
    }

    @Test
    public void testSubtractFromZero() {
        final String expr = "(MINUS 0 12)";
        final List<String> tokens = (new Lexer(expr)).getTokens();
        PTree ptree = parser.parse(tokens);
        System.out.println(ptree);
        int foo = eval.apply(ptree.root()).toAtom().toI();
        assertEquals(expr, -12, foo);
        System.out.println("Eval returned " + foo);
    }

    @Test
    public void testSubtractThreeNumbers() {
        final String expr = "(MINUS 72 12 7)";
        final List<String> tokens = (new Lexer(expr)).getTokens();
        PTree ptree = parser.parse(tokens);
        System.out.println(ptree);
        int foo = eval.apply(ptree.root()).toAtom().toI();
        assertEquals(expr, 53, foo);
        System.out.println("Eval returned " + foo);
    }
}
