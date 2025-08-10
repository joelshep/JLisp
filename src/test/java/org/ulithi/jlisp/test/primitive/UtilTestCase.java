package org.ulithi.jlisp.test.primitive;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.Session;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.eval;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.newSession;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Util}.
 */
public class UtilTestCase {
    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Before
    public void setUp() {
        systemErrRule.muteForSuccessfulTests();
        systemOutRule.mute();
    }

    @After
    public void tearDown() {
        systemErrRule.clearLog();
        systemOutRule.clearLog();
    }

    @Test
    public void testSameListIsEql() {
        Session session = newSession();
        session.eval("(SETQ MY-LIST (QUOTE (1 2 3)))");
        final SExpression sexp = session.eval("(EQL MY-LIST MY-LIST)");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testDifferentListsWithSameContentsAreNotEql() {
        final SExpression sexp = eval("(EQL (QUOTE (1 2 3)) (QUOTE (1 2 3)))");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testSameNumberIsEql() {
        final SExpression sexp = eval("(EQL 42 42)");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testDifferentNumbersAreNotEql() {
        final SExpression sexp = eval("(EQL 42 43)");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testSameSymbolIsEql() {
        final SExpression sexp = eval("(EQL (QUOTE FOO) (QUOTE FOO))");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testDifferentSymbolsAreNotEql() {
        final SExpression sexp = eval("(EQL (QUOTE FOO) (QUOTE BAR))");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testSameEmptyListIsEql() {
        List emptyList = eval("(QUOTE ())").toList();
        String expr = String.format("(EQL '%s '%s)", emptyList, emptyList);
        SExpression sexp = eval(expr);
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testDifferentEmptyListsAreEql() {
        final SExpression sexp = eval("(EQL (LIST) (LIST))");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testQuotedEmptyListsAreEql() {
        final SExpression sexp = eval("(EQL (QUOTE ()) (QUOTE ()))");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testEqualListsAreNotEql() {
        final SExpression sexp = eval("(EQL (QUOTE (1 2 3)) (QUOTE (1 2 3)))");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testConsedListsAreNotEql() {
        final SExpression sexp = eval("(EQL (CONS 1 NIL) (CONS 1 NIL))");
        assertTrue(sexp.isAtom());
        assertFalse(sexp.toAtom().toB());
    }

    @Test
    public void testNilIsEqlToItself() {
        final SExpression sexp = eval("(EQL NIL NIL)");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testTIsEqlToItself() {
        final SExpression sexp = eval("(EQL T T)");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testFIsEqlToItself() {
        final SExpression sexp = eval("(EQL F F)");
        assertTrue(sexp.isAtom());
        assertTrue(sexp.toAtom().toB());
    }

    @Test
    public void testAtomsAreEql() {
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
