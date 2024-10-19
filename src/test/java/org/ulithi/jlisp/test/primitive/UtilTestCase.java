package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.eval;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Util}.
 */
public class UtilTestCase {

    @Test
    public void testAtomsAreEq() {
        final SExpression sexp = eval("(EQL (QUOTE FOO) (QUOTE FOO))");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testEqlIsCaseSensitive() {
        final SExpression sexp = eval("(EQL (QUOTE FOO) (QUOTE Foo))");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testDifferentAtomsAreNotEql() {
        final SExpression sexp = eval("(EQL (QUOTE FOO) (QUOTE BAR))");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testEqualListsAreNotEql() {
        final SExpression sexp = eval("(EQL (QUOTE (1 2 3)) (QUOTE (1 2 3)))");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testEmptyListsAreEql() {
        final SExpression sexp = eval("(EQL (QUOTE ()) (QUOTE ()))");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testAtomsAreEqual() {
        final SExpression sexp = eval("(EQUAL (QUOTE FOO) (QUOTE FOO))");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testEqualIsCaseSensitive() {
        final SExpression sexp = eval("(EQUAL (QUOTE FOO) (QUOTE Foo))");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testDifferentAtomsAreNotEqual() {
        final SExpression sexp = eval("(EQUAL (QUOTE FOO) (QUOTE BAR))");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testListsAreEqual() {
        final SExpression sexp = eval("(EQUAL (QUOTE (1 2 3)) (QUOTE (1 2 3)))");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testEmptyListsAreEqual() {
        final SExpression sexp = eval("(EQUAL (QUOTE ()) (QUOTE ()))");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testListsOfDifferentSizeNotEqual() {
        final SExpression sexp = eval("(EQUAL (QUOTE (1 2 3)) (QUOTE (1 2)))");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testListsOfDifferentSizeNotEqual2() {
        final SExpression sexp = eval("(EQUAL (QUOTE (2 3)) (QUOTE (1 2 3)))");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testNestedListsAreEqual() {
        final SExpression sexp = eval("(EQUAL (QUOTE (1 2 (4 5) 3)) (QUOTE (1 2 (4 5) 3)))");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testMultipleListsAreEqual() {
        final SExpression sexp = eval("(EQUAL (QUOTE (1 2 3)) (QUOTE (1 2 3)) (QUOTE (1 2 3)) (QUOTE (1 2 3)))");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testMultipleUnequalLists() {
        final SExpression sexp = eval("(EQUAL (QUOTE (1 2 3)) (QUOTE (1 2 3)) (QUOTE (1 2)) (QUOTE (1 2 3)))");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testExpectIsAtom() {
        final SExpression sexp = eval("(EXPECT (+ 1 2) 3)");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testExpectFailure() {
        final SExpression sexp = eval("(EXPECT (+ 1 2) 4)");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testExpectIsList() {
        final SExpression sexp = eval("(EXPECT (APPEND '(A) '() '(B) '()) '(A B))");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }
}
