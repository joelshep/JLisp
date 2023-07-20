package org.ulithi.jlisp.test.mem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.parser.Parser2;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.ulithi.jlisp.parser.Symbols.*;

/**
 * At this point, these aren't really tests: these are just me trying to work out how
 * to build parse trees from tokens.
 */
public class PTreeBuilderTestCase {

    private Parser2 parser;

    @Before
    public void setUp() {
        this.parser = new Parser2();
    }

    @After
    public void tearDown() {
        this.parser = null;
    }

    /**
     * Makes a PTree from a simple list of tokens.
     */
    @Test
    public void buildSymbolList() {
        // ( + 1 2 4 )  =>  (+ . (1 . (2 . (4 . NIL))))
        List<String> tokens = Arrays.asList("(", "+", "1", "2", "4", ")");
        PTree ptree = parser.parse2(tokens);
        final String dpExpression = ptree.toString();
        assertEquals("(+ . (1 . (2 . (4 . NIL))))", dpExpression);
        System.out.println(ptree);
    }

    @Test
    public void buildNestedList() {
        // ( + 2 ( * 5 9 ) )  =>  ( + . (2 . (* . (5 . (9 . NIL)))))
        List<String> tokens = Arrays.asList("(", "+", "2", "(", "*", "5", "9", ")", ")");
        PTree ptree = parser.parse2(tokens);
        final String dpExpression = ptree.toString();
        assertEquals("(+ . (2 . (* . (5 . (9 . NIL)))))", dpExpression);
        System.out.println(ptree);
    }

    @Test
    public void buildListOfLists() {
        // ( ( 2 3 ) ( 4 5 ) )
        List<String> tokens = Arrays.asList("(", "(", "2", "3", ")", "(", "4", "5", ")", ")");
        PTree ptree = parser.parse2(tokens);
        final String dpExpression = ptree.toString();
        assertEquals("((2 . (3 . NIL)) . (4 . (5 . NIL)))", dpExpression);
        System.out.println(ptree);
    }

    @Test
    public void buildInitialNestedList() {
        // ( ( 2 3 ) 4 )  =>  ((2. (3 . NIL)) . (4 . NIL))
        List<String> tokens = Arrays.asList("(", "(", "2", "3", ")", "4", ")");
        PTree ptree = parser.parse2(tokens);
        final String dpExpression = ptree.toString();
        assertEquals("((2 . (3 . NIL)) . (4 . NIL))", dpExpression);
        System.out.println(ptree);
    }
}
