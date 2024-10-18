package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.TypeConversionException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;
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

    @Test
    public void testAppendSingleAtom() {
        final String expression = "(APPEND 'A)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isAtom());
        assertEquals("A", sexp.toString());
    }

    @Test(expected = EvaluationException.class)
    public void testAppendMultipleAtomsIsError() {
        final String expression = "(APPEND 'A 'B 'C)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
    }

    @Test
    public void testAppendSingleLists() {
        final String expression = "(APPEND '(A) '() '(B) '())";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertEquals("( A B )", sexp.toString());
    }

    @Test
    public void testAppendTwoLists() {
        final String expression = "(APPEND '(A B) '(A B))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertEquals("( A B A B )", sexp.toString());
    }

    @Test
    public void testAppendListOfLists() {
        final String expression = "(APPEND '((A) (B)) ' ((C) (D)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertEquals("( ( A ) ( B ) ( C ) ( D ) )", sexp.toString());
    }

    @Test
    public void testAssocMatchesKey() {
        final String expression = "(ASSOC 'oak '((pine cones) (oak acorns) (maple seeds)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertEquals("( oak acorns )", sexp.toString());
    }

    @Test
    public void testAssocNoMatchForKey() {
        final String expression = "(ASSOC 'birch '((pine cones) (oak acorns) (maple seeds)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals(Atom.NIL, sexp);
    }

    @Test
    public void testEmptyAssocList() {
        final SExpression sexp = UnitTestUtilities.evaluate("(ASSOC 'a '())");
        assertEquals(Atom.NIL, sexp);
    }

    @Test
    public void testAssocMatchesFirstMatch() {
        final String expression = "(ASSOC 'oak '((pine cones) (oak acorns) (oak seeds)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertEquals("( oak acorns )", sexp.toString());
    }

    @Test(expected = EvaluationException.class)
    public void testInvalidAssocList() {
        final String expression = "(ASSOC 'oak 'pine_cones)";
        UnitTestUtilities.evaluate(expression);
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testInvalidAssocListWithSingleElement() {
        final String expression = "(ASSOC 'oak)";
        UnitTestUtilities.evaluate(expression);
    }

    @Test
    public void testAssocWithNestedLists() {
        final String expression = "(ASSOC 'x '((a 1) (b 2) (x (3 4 5))))";
        SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals("( x ( 3 4 5 ) )", sexp.toString());
    }

}
