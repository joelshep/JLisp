package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CollectionsTestCase {

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

    @Test
    public void testListWithSingleAtom() {
        final String expression = "(LIST 'A)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertEquals("( A )", sexp.toString());
    }

    @Test
    public void testListWithMultipleAtoms() {
        final String expression = "(LIST 'A 'B 'C)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertEquals("( A B C )", sexp.toString());
    }

    @Test
    public void testListWithSingleList() {
        final String expression = "(LIST '(1 2 3))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertEquals("( ( 1 2 3 ) )", sexp.toString());
    }

    @Test
    public void testListWithSublists() {
        final String expression = "(LIST (LIST 'A 'B) (LIST 'C 'D))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertEquals("( ( A B ) ( C D ) )", sexp.toString());
    }
}
