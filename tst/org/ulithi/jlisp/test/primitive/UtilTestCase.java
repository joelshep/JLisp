package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.primitive.Util.LENGTH;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.parse;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Util}.
 */
public class UtilTestCase {

    @Test
    public void testLengthOfEmptyList() {
        final String expression = "(LENGTH (QUOTE ()))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isAtom());
        assertEquals(0, sexp.toAtom().toI());
    }

    @Test
    public void testLengthOfSimpleList() {
        final String expression = "(LENGTH (QUOTE (1 2 3)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isAtom());
        assertEquals(3, sexp.toAtom().toI());
    }

    @Test
    public void testLengthOfNestedList() {
        final String expression = "(LENGTH (QUOTE (1 (A B C) 3 (DEF))))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isAtom());
        assertEquals(4, sexp.toAtom().toI());
    }

    @Test
    public void testLengthOfNestedList2() {
        final String expression = "(LENGTH (QUOTE ((A B C) 1 (DEF) 3)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isAtom());
        assertEquals(4, sexp.toAtom().toI());
    }

    @Test
    public void testLengthOfAtomAndEmptyList() {
        final String expression = "(LENGTH (QUOTE (Z ())))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isAtom());
        assertEquals(2, sexp.toAtom().toI());
    }
}
