package org.ulithi.jlisp.test.primitive;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.mem.Ref;
import org.ulithi.jlisp.parser.Parser;

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
