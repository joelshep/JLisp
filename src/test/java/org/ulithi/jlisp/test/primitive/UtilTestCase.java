package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Util}.
 */
public class UtilTestCase {

    @Test
    public void testAtomsAreEq() {
        final String expression = "(EQL (QUOTE FOO) (QUOTE FOO))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testEqlIsCaseSensitive() {
        final String expression = "(EQL (QUOTE FOO) (QUOTE Foo))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testDifferentAtomsAreNotEql() {
        final String expression = "(EQL (QUOTE FOO) (QUOTE BAR))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testEqualListsAreNotEql() {
        final String expression = "(EQL (QUOTE (1 2 3)) (QUOTE (1 2 3)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testEmptyListsAreEql() {
        final String expression = "(EQL (QUOTE ()) (QUOTE ()))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testAtomsAreEqual() {
        final String expression = "(EQUAL (QUOTE FOO) (QUOTE FOO))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testEqualIsCaseSensitive() {
        final String expression = "(EQUAL (QUOTE FOO) (QUOTE Foo))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testDifferentAtomsAreNotEqual() {
        final String expression = "(EQUAL (QUOTE FOO) (QUOTE BAR))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testListsAreEqual() {
        final String expression = "(EQUAL (QUOTE (1 2 3)) (QUOTE (1 2 3)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testEmptyListsAreEqual() {
        final String expression = "(EQUAL (QUOTE ()) (QUOTE ()))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testListsOfDifferentSizeNotEqual() {
        final String expression = "(EQUAL (QUOTE (1 2 3)) (QUOTE (1 2)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testListsOfDifferentSizeNotEqual2() {
        final String expression = "(EQUAL (QUOTE (2 3)) (QUOTE (1 2 3)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testNestedListsAreEqual() {
        final String expression = "(EQUAL (QUOTE (1 2 (4 5) 3)) (QUOTE (1 2 (4 5) 3)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testMultipleListsAreEqual() {
        final String expression = "(EQUAL (QUOTE (1 2 3)) (QUOTE (1 2 3)) (QUOTE (1 2 3)) (QUOTE (1 2 3)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testMultipleUnequalLists() {
        final String expression = "(EQUAL (QUOTE (1 2 3)) (QUOTE (1 2 3)) (QUOTE (1 2)) (QUOTE (1 2 3)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testExpectIsAtom() {
        final String expression = "(EXPECT (+ 1 2) 3)";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testExpectFailure() {
        final String expression = "(EXPECT (+ 1 2) 4)";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testExpectIsList() {
        final String expression = "(EXPECT (APPEND '(A) '() '(B) '()) '(A B))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }
}
