package org.ulithi.jlisp.test.mem;

import org.junit.Test;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link org.ulithi.jlisp.mem.PTree}.
 * <p>
 * Note that most {@code PTree} functionality is exercised through unit tests for the parser
 * and related functionality. These tests focus on other functionality used only by the
 * REPL but not in the course of normal expression evaluation.
 */
public class PTreeTestCase {

    @Test
    public void testEmptyPTree() {
        final PTree pTree = new PTree();
        assertTrue(pTree.isEmpty());
        assertEquals("NIL", pTree.unparse());
        assertEquals("(NIL . NIL)", pTree.toString());
    }

    @Test
    public void testNestedUnparse() {
        final String expression = "(+ (* 2 4) (* 3 6))";
        final PTree pTree = UnitTestUtilities.parse(expression);
        assertFalse(pTree.isEmpty());
        final String unparsed = pTree.unparse();
        assertEquals("( + ( * 2 4 ) ( * 3 6 ) )", unparsed);
    }

    @Test
    public void testQuoteUnparse() {
        final String expression = "(QUOTE (A B C))";
        final PTree pTree = UnitTestUtilities.parse(expression);
        assertFalse(pTree.isEmpty());
        final String unparsed = pTree.unparse();
        assertEquals("( QUOTE ( A B C ) )", unparsed);
    }

    @Test
    public void testSingleQuoteUnparse() {
        final String expression = "('(A B C))";
        final PTree pTree = UnitTestUtilities.parse(expression);
        assertFalse(pTree.isEmpty());
        final String unparsed = pTree.unparse();
        assertEquals("( QUOTE ( A B C ) )", unparsed);
    }

    @Test
    public void testNestedNestedUnparse() {
        final String expression = "(CAR (CDR '(A B C)))";
        final PTree pTree = UnitTestUtilities.parse(expression);
        assertFalse(pTree.isEmpty());
        final String unparsed = pTree.unparse();
        assertEquals("( CAR ( CDR ( QUOTE ( A B C ) ) ) )", unparsed);
    }
}
