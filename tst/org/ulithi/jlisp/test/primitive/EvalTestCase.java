package org.ulithi.jlisp.test.primitive;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ulithi.jlisp.lexer.Lexer;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.mem.Ref;
import org.ulithi.jlisp.parser.Parser;
import org.ulithi.jlisp.primitive.Eval;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Exercises the {@code eval} function ({@link org.ulithi.jlisp.primitive.Eval}) on some simple
 * arithmetic expressions and validates the results. Also includes some not very useful tests
 * demonstrating basic PTree navigation.
 */
public class EvalTestCase {

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

    @Test
    public void testCountSimpleList() {
        // ( 1 2 3 )
        final List<String> tokens = Arrays.asList("(", "1", "2", "3", ")");
        PTree ptree = parser.parse(tokens);
        System.out.println(ptree);
        int count = countNodes(ptree.root());
        assertEquals("Count of tree nodes", 3, count);
        System.out.println("Count=" + count);

    }

    @Test
    public void testCountTreeNodes() {
        // ( ( 2 3 ) ( 4 5 ) )
        final List<String> tokens = Arrays.asList("(", "(", "2", "3", ")", "(", "4", "5", ")", ")");
        PTree ptree = parser.parse(tokens);
        int count = countNodes(ptree.root());
        assertEquals("Count of tree nodes", 2, count);
        System.out.println("Count=" + count);
    }

    /**
     * Evaluates a simple addition expression: (+ 2 3).
     */
    @Test
    public void testAddTwoNumbers() {
        final List<String> tokens = Arrays.asList("(", "+", "2", "3", ")");
        PTree ptree = parser.parse(tokens);
        System.out.println(ptree);
        int foo = eval.apply(ptree.root());
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
        int foo = eval.apply(ptree.root());
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
        int foo = eval.apply(ptree.root());
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
        int foo = eval.apply(ptree.root());
        assertEquals("(+ (* 4 5) (* 2 3))", 26, foo);
        System.out.println("Eval returned " + foo);
    }

    /**
     * Evaluates an expression with sub-sub-expressions: (+ (* 4 (+ 2 3)) (* 2 3))
     */
    @Test
    public void testThreeLayers() {
        final List<String> tokens = Arrays.asList("(", "+", "(", "*", "4", "(", "+", "2", "3", ")", ")", "(", "*", "2", "3", ")", ")");
        PTree ptree = parser.parse(tokens);
        System.out.println(ptree);
        int foo = eval.apply(ptree.root());
        assertEquals("(+ (* 4 (+ 2 3)) (* 2 3))", 26, foo);
        System.out.println("Eval returned " + foo);
    }

    /**
     * <em>Lexs</em>, parses and evaluates an expression with a sub-sub-expression:
     *   (+ (* 4 (+ 2 3)) (* 2 3))
     */
    @Test
    public void testBigKahuna() {
        final String expr = "(+ (* 4 (+ 2 3)) (* 2 3))";
        final List<String> tokens = (new Lexer(expr)).getTokens();
        PTree ptree = parser.parse(tokens);
        System.out.println(ptree);
        int foo = eval.apply(ptree.root());
        assertEquals("(+ (* 4 (+ 2 3)) (* 2 3))", 26, foo);
        System.out.println("Eval returned " + foo);
    }

    /**
     * Iterates over the linked list of cells beginning with the given cell, and returns the
     * count of cells encountered: i.e., the count of elements directly in the list rooted by
     * the given cell.
     * @param cell The root cell of a list.
     * @return The number of elements directly in the list.
     */
    private int countNodes(final Cell cell) {
        if (cell.isNil()) { return 0; }

        Ref curr = cell;
        int count = 0;

        while (!curr.isNil()) {
            count++;
            curr = ((Cell)curr).getRest();
        }

        return count;
    }
}
