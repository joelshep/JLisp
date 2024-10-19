package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.eval;

public class CollectionsTestCase {

    @Test
    public void testLengthOfEmptyList() {
        final SExpression sexp = eval("(LENGTH (QUOTE ()))");
        assertTrue(sexp.isAtom());
        assertEquals(0, sexp.toAtom().toI());
    }

    @Test
    public void testLengthOfSimpleList() {
        final SExpression sexp = eval("(LENGTH (QUOTE (1 2 3)))");
        assertTrue(sexp.isAtom());
        assertEquals(3, sexp.toAtom().toI());
    }

    @Test
    public void testLengthOfNestedList() {
        final SExpression sexp = eval("(LENGTH (QUOTE (1 (A B C) 3 (DEF))))");
        assertTrue(sexp.isAtom());
        assertEquals(4, sexp.toAtom().toI());
    }

    @Test
    public void testLengthOfNestedList2() {
        final SExpression sexp = eval("(LENGTH (QUOTE ((A B C) 1 (DEF) 3)))");
        assertTrue(sexp.isAtom());
        assertEquals(4, sexp.toAtom().toI());
    }

    @Test
    public void testLengthOfAtomAndEmptyList() {
        final SExpression sexp = eval("(LENGTH (QUOTE (Z ())))");
        assertTrue(sexp.isAtom());
        assertEquals(2, sexp.toAtom().toI());
    }

    @Test
    public void testListWithSingleAtom() {
        final SExpression sexp = eval("(LIST 'A)");
        assertTrue(sexp.isList());
        assertEquals("( A )", sexp.toString());
    }

    @Test
    public void testListWithMultipleAtoms() {
        final SExpression sexp = eval("(LIST 'A 'B 'C)");
        assertTrue(sexp.isList());
        assertEquals("( A B C )", sexp.toString());
    }

    @Test
    public void testListWithSingleList() {
        final SExpression sexp = eval("(LIST '(1 2 3))");
        assertTrue(sexp.isList());
        assertEquals("( ( 1 2 3 ) )", sexp.toString());
    }

    @Test
    public void testListWithSublists() {
        final SExpression sexp = eval("(LIST (LIST 'A 'B) (LIST 'C 'D))");
        assertTrue(sexp.isList());
        assertEquals("( ( A B ) ( C D ) )", sexp.toString());
    }

    @Test
    public void testAppendSingleAtom() {
        final SExpression sexp = eval("(APPEND 'A)");
        assertTrue(sexp.isAtom());
        assertEquals("A", sexp.toString());
    }

    @Test(expected = EvaluationException.class)
    public void testAppendMultipleAtomsIsError() {
        eval("(APPEND 'A 'B 'C)");
    }

    @Test
    public void testAppendSingleLists() {
        final SExpression sexp = eval("(APPEND '(A) '() '(B) '())");
        assertTrue(sexp.isList());
        assertEquals("( A B )", sexp.toString());
    }

    @Test
    public void testAppendTwoLists() {
        final SExpression sexp = eval("(APPEND '(A B) '(A B))");
        assertTrue(sexp.isList());
        assertEquals("( A B A B )", sexp.toString());
    }

    @Test
    public void testAppendListOfLists() {
        final SExpression sexp = eval("(APPEND '((A) (B)) ' ((C) (D)))");
        assertTrue(sexp.isList());
        assertEquals("( ( A ) ( B ) ( C ) ( D ) )", sexp.toString());
    }

    @Test
    public void testAssocMatchesKey() {
        final SExpression sexp = eval("(ASSOC 'oak '((pine cones) (oak acorns) (maple seeds)))");
        assertTrue(sexp.isList());
        assertEquals("( oak acorns )", sexp.toString());
    }

    @Test
    public void testAssocNoMatchForKey() {
        final SExpression sexp = eval("(ASSOC 'birch '((pine cones) (oak acorns) (maple seeds)))");
        assertEquals(Atom.NIL, sexp);
    }

    @Test
    public void testEmptyAssocList() {
        final SExpression sexp = eval("(ASSOC 'a '())");
        assertEquals(Atom.NIL, sexp);
    }

    @Test
    public void testAssocMatchesFirstMatch() {
        final SExpression sexp = eval("(ASSOC 'oak '((pine cones) (oak acorns) (oak seeds)))");
        assertTrue(sexp.isList());
        assertEquals("( oak acorns )", sexp.toString());
    }

    @Test(expected = EvaluationException.class)
    public void testInvalidAssocList() {
        eval("(ASSOC 'oak 'pine_cones)");
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testInvalidAssocListWithSingleElement() {
        eval("(ASSOC 'oak)");
    }

    @Test
    public void testAssocWithNestedLists() {
        SExpression sexp = eval("(ASSOC 'x '((a 1) (b 2) (x (3 4 5))))");
        assertEquals("( x ( 3 4 5 ) )", sexp.toString());
    }
}
