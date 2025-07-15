package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.eval;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.newSession;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Logic}.
 */
public class LogicTestCase {

    @Test
    public void testSimpleAnd() {
        assertTrue(eval("(AND (EQL 1 1) (EQL 2 2))").toAtom().toB());
    }

    @Test
    public void testSimpleAndWithBooleanSymbols() {
        assertTrue(eval("(AND T T T)").toAtom().toB());
        assertFalse(eval("(AND T F T)").toAtom().toB());
        assertFalse(eval("(AND F F F)").toAtom().toB());
    }

    @Test
    public void testAndWithFalseFirstArgument() {
        assertFalse(eval("(AND (NOT T) (NOT NIL))").toAtom().toB());
    }

    @Test
    public void testAndWithFalseSecondArgument() {
        assertFalse(eval("(AND (NOT F) (NOT T))").toAtom().toB());
    }

    @Test
    public void testAndWithEmptyArgumentList() {
        assertTrue(eval("(AND)").toAtom().toB());
    }

    @Test
    public void testAndWithSingleArgument() {
        assertTrue(eval("(AND (EQL 1 1))").toAtom().toB());
        assertFalse(eval("(AND (EQL 1 2))").toAtom().toB());
    }

    @Test
    public void testAndDoesShortcutEvaluation() {
        final UnitTestUtilities.Session session = newSession();
        session.eval("(SETQ A 5)");
        final SExpression result = session.eval("(AND T F (SETQ A 6))");
        assertFalse(result.toAtom().toB());
        assertEquals(5, session.eval("A").toAtom().toI());
    }

    @Test
    public void testNotOfTrue() {
        assertEquals(Atom.F, eval("(NOT T)"));
    }

    @Test
    public void testNotOfFalse() {
        assertEquals(Atom.T, eval("(NOT F)"));
    }

    @Test
    public void testNotOfNil() {
        assertEquals(Atom.T, eval("(NOT NIL)"));
        assertEquals(Atom.T, eval("(NOT ())"));
        assertEquals(Atom.T, eval("(NOT '())"));
    }

    @Test
    public void testNotOfTruthy() {
        assertEquals(Atom.F, eval("(NOT (EQL 4 4))"));
        assertEquals(Atom.T, eval("(NOT (EQL 4 3))"));
        assertEquals(Atom.F, eval("(NOT '(A B C))"));
    }


    @Test
    public void testSimpleOr() {
        assertTrue(eval("(OR (EQL 1 0) (EQL 1 1))").toAtom().toB());
    }

    @Test
    public void testSimpleORWithBooleanSymbols() {
        assertTrue(eval("(OR T T T)").toAtom().toB());
        assertTrue(eval("(OR F F T)").toAtom().toB());
        assertFalse(eval("(OR F F F)").toAtom().toB());
    }

    @Test
    public void testORWithFalseFirstArgument() {
        assertTrue(eval("(OR (NOT T) (NOT NIL))").toAtom().toB());
    }

    @Test
    public void testORWithFalseSecondArgument() {
        assertTrue(eval("(OR (NOT F) (NOT T))").toAtom().toB());
    }

    @Test
    public void testOrWithEmptyArgumentList() {
        assertFalse(eval("(OR)").toAtom().toB());
    }

    @Test
    public void testORWithSingleArgument() {
        assertTrue(eval("(OR (EQL 1 1))").toAtom().toB());
        assertFalse(eval("(OR (EQL 1 2))").toAtom().toB());
    }

    @Test
    public void testOrDoesShortcutEvaluation() {
        final UnitTestUtilities.Session session = newSession();
        session.eval("(SETQ A 5)");
        final SExpression result = session.eval("(OR F T (SETQ A 6))");
        assertTrue(result.toAtom().toB());
        assertEquals(5, session.eval("A").toAtom().toI());
    }

    @Test
    public void testT() {
        final boolean result = eval("T").toAtom().toB();
        assertTrue(result);
    }

    @Test
    public void testTNotFunction() {
        Exception ex = null;
        try {
            eval("(T)").toAtom().toB();
        } catch (final EvaluationException e) {
            ex = e;
        }

        assertNotNull(ex);
        assertEquals("Expected function!", ex.getMessage());
    }

    @Test
    public void testF() {
        final boolean result = eval("F").toAtom().toB();
        assertFalse(result);
    }

    @Test
    public void testFNotFunction() {
        Exception ex = null;
        try {
            eval("(F)").toAtom().toB();
        } catch (final EvaluationException e) {
            ex = e;
        }

        assertNotNull(ex);
        assertEquals("Expected function!", ex.getMessage());
    }
}
