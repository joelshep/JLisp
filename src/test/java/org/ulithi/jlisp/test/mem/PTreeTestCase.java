package org.ulithi.jlisp.test.mem;

import org.junit.Test;
import org.ulithi.jlisp.mem.PTree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.parse;

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
        final PTree pTree = parse("(+ (* 2 4) (* 3 6))");
        assertFalse(pTree.isEmpty());
        final String unparsed = pTree.unparse();
        assertEquals("( + ( * 2 4 ) ( * 3 6 ) )", unparsed);
    }

    @Test
    public void testQuoteUnparse() {
        final PTree pTree = parse("(QUOTE (A B C))");
        assertFalse(pTree.isEmpty());
        final String unparsed = pTree.unparse();
        assertEquals("( QUOTE ( A B C ) )", unparsed);
    }

    @Test
    public void testSingleQuoteUnparse() {
        final PTree pTree = parse("'A");
        assertFalse(pTree.isEmpty());
        final String unparsed = pTree.unparse();
        assertEquals("( QUOTE A )", unparsed);
    }

    @Test
    public void testSingleQuoteListUnparse() {
        final PTree pTree = parse("'(A B C)");
        assertFalse(pTree.isEmpty());
        final String unparsed = pTree.unparse();
        assertEquals("( QUOTE ( A B C ) )", unparsed);
    }

    @Test
    public void testNestedNestedUnparse() {
        final PTree pTree = parse("(CAR (CDR '(A B C)))");
        assertFalse(pTree.isEmpty());
        final String unparsed = pTree.unparse();
        assertEquals("( CAR ( CDR ( QUOTE ( A B C ) ) ) )", unparsed);
    }
}
