package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;
import org.ulithi.jlisp.test.suite.UnitTestUtilities.Session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.eval;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.newSession;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Lang}.
 */
public class LangTestCase {

    @Test(expected = EvaluationException.class)
    public void testCAROfLiteral() {
        // CAR should throw exception if argument is not a list
        eval("(CAR (QUOTE HELLO))");
    }

    @Test
    public void testCAROfSingleElementList() {
        final SExpression sexp = eval("(CAR (QUOTE (HELLO)))");
        assertEquals("HELLO", sexp.toAtom().toS());
    }

    @Test
    public void testCAROfSimpleList() {
        final SExpression sexp = eval("(CAR (QUOTE (4 5 6)))");
        assertEquals(4, sexp.toAtom().toI());
    }

    @Test
    public void testCAROfSingleQuoteList() {
        final SExpression sexp = eval("(CAR '(A B C))");
        assertEquals("A", sexp.toAtom().toS());
    }

    @Test
    public void testCAROfListOfLists() {
        final SExpression sexp = eval("(CAR (QUOTE ((A B) (C D) (E F))))");
        assertEquals("( A B )", sexp.toList().toString());
    }

    @Test
    public void testCAROfEmptyList() {
        final SExpression sexp = eval("(CAR (QUOTE ()))");
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test(expected = EvaluationException.class)
    public void testCDROfLiteral() {
        // CDR should throw exception if argument is not a list
        eval("(CDR (QUOTE HELLO))");
    }

    @Test
    public void testCDROfSingleElementList() {
        final SExpression sexp = eval("(CDR (QUOTE (HELLO)))");
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test
    public void testCDROfSimpleList() {
        final SExpression sexp = eval("(CDR (QUOTE (4 5 6)))");
        assertEquals("( 5 6 )", sexp.toList().toString());
    }

    @Test
    public void testCDROfListOfLists() {
        final SExpression sexp = eval("(CDR (QUOTE ((A B) (C D) (E F))))");
        assertEquals("( ( C D ) ( E F ) )", sexp.toList().toString());
    }

    @Test
    public void testCDROfEmptyList() {
        final SExpression sexp = eval("(CDR (QUOTE ()))");
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test
    public void testConsEmptyListToEmptyList() {
        final SExpression sexp = eval("(CONS () ())");
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test
    public void testSimpleCond() {
        SExpression sexp = eval("(COND ((EQL 2 2) SNOO) ((EQL 3 4) BOO) ((EQL 5 5) (+ 4 5)))");
        assertEquals("SNOO", sexp.toString());

        sexp = UnitTestUtilities.eval("(COND ((EQL 2 3) SNOO) ((EQL 4 4) BOO) ((EQL 5 5) (+ 4 5)))");
        assertEquals("BOO", sexp.toString());

        sexp = UnitTestUtilities.eval("(COND ((EQL 2 3) SNOO) ((EQL 3 4) BOO) ((EQL 5 5) (+ 4 5)))");
        assertEquals(9, sexp.toAtom().toI());
    }

    @Test
    public void testCondDefun() {
        final Session session = newSession();
        final SExpression defunResult = session.eval("(defun abs (x) (COND ((minusp x) (- x)) (T x)))");
        assertEquals("abs", defunResult.toString());

        // Now evaluate the 'abs' function that we just created.
        SExpression abs = session.eval("(abs -4)");
        assertEquals(4, abs.toAtom().toI());

        abs = session.eval("(abs 5)");
        assertEquals(5, abs.toAtom().toI());
    }

    @Test
    public void testEmptyCond() {
        final SExpression sexp = eval("(COND )");
        assertTrue(sexp.isNil());
    }

    @Test
    public void testCondWithAllFalseConditions() {
        final SExpression sexp = eval("(COND ((EQL 1 2) 'foo) ((EQL 3 4) 'bar))");
        assertTrue(sexp.isNil());
    }

    @Test
    public void testCondShortcutsEvaluation() {
        final Session session = newSession();
        session.eval("(SETQ X 0)");
        final SExpression sexp = session.eval("(COND ((EQ 1 1) 'foo) ((SETQ X 1) 'bar))");
        assertTrue(sexp.isAtom());
        assertEquals("foo", sexp.toAtom().toS());
        final SExpression x = session.eval("X");
        assertTrue(x.isAtom());
        assertEquals(0, x.toAtom().toI());
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testConsAtoms() {
        eval("(CONS 1 2 3)");
    }

    @Test
    public void testConsAtomAndList() {
        final SExpression sexp = eval("(CONS 1 (QUOTE (2 3)))");
        assertEquals("( 1 2 3 )", String.valueOf(sexp));
    }

    @Test
    public void testConsLiteralToEmptyList() {
        final SExpression sexp = eval("(CONS HELLO ())");
        assertEquals("( HELLO )", String.valueOf(sexp));
    }

    @Test
    public void testConsListToEmptyList() {
        final SExpression sexp = eval("(CONS (QUOTE (PHONE HOME)) ())");
        assertEquals("( ( PHONE HOME ) )", String.valueOf(sexp));
    }

    @Test
    public void testConsAtomToList() {
        final SExpression sexp = eval("(CONS (QUOTE BAR) (QUOTE (BAZ)))");
        assertEquals("( BAR BAZ )", String.valueOf(sexp));
    }

    @Test
    public void testConsAtomsToList() {
        final SExpression sexp = eval("(CONS (QUOTE FOO) (CONS (QUOTE BAR) (QUOTE (BAZ))))");
        assertEquals("( FOO BAR BAZ )", String.valueOf(sexp));
    }

    @Test
    public void testConsListToList() {
        final SExpression sexp = eval("(CONS (QUOTE (NOW IS)) (QUOTE (THE TIME)))");
        assertEquals("( ( NOW IS ) THE TIME )", String.valueOf(sexp));
        assertEquals(3, sexp.toList().length().toAtom().toI());
    }

    @Test
    public void evaluateCarExpression() {
        final SExpression sexp = eval("( CAR () )");
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test
    public void evaluateCarConsExpression() {
        final String result = eval("(CAR (CONS x y))").toAtom().toS();
        assertEquals("x", result);
    }

    @Test
    public void evaluateCdrConsExpression() {
        final String result = eval("(CDR (CONS x y))").toList().toString();
        assertEquals("( y )", result);
    }

    @Test
    public void evaluateCdrConsListExpression() {
        final String result = eval("(CDR (CONS x (PLUS 1 2)))").toList().toString();
        assertEquals("( 3 )", result);
    }

    @Test
    public void evaluateConsExpression() {
        final String result = eval("(CONS x y)").toList().toString();
        assertEquals("( x y )", result);
    }

    /**
     * Simple test of the DEFUN function. First defines an "average" function, then invokes it.
     * This is a bit hacked up because it needs to retain the same eval instance, so it references
     * the same environment when defining the function and when applying it.
     */
    @Test
    public void testSimpleDefun() {
        // Evaluate defun, which should create the 'average' function and return its name.
        final Session session = newSession();
        final String defun = "(defun average (x y) (QUOTIENT (PLUS x y) 2))";
        final SExpression defunResult = session.eval(defun);
        assertEquals("average", defunResult.toString());

        // Now evaluate the 'average' function that we just created.
        final SExpression avg = session.eval("(average 7 5)");
        assertEquals(6, avg.toAtom().toI());
    }

    /**
     * Creates a user function that takes zero arguments, invokes it and validates the result.
     */
    @Test
    public void testZeroArgDefun() {
        final Session session = newSession();
        final SExpression defunResult = session.eval("(defun eleven () (QUOTE 11))");
        assertEquals("eleven", defunResult.toString());

        // Now evaluate the 'average' function that we just created.
        final SExpression avg = session.eval("(eleven)");
        assertEquals(11, avg.toAtom().toI());
    }

    /**
     * Creates a user function that takes two arguments, invokes it with one and three arguments,
     * and verifies that both cause an EvaluationException to be thrown.
     */
    @Test
    public void testDefunParameterCountMismatch() {
        // Evaluate defun, which should create the 'average' function and return its name.
        final Session session = newSession();
        final String defun = "(defun average (x y) (QUOTIENT (PLUS x y) 2))";
        final SExpression result = session.eval(defun);
        assertEquals("average", result.toString());

        try {
            session.eval("(average 7)");
            fail("Expected EvaluationException");
        } catch (final EvaluationException e) {
            assertTrue(e.getMessage().startsWith("Expected 2"));
        }

        try {
            session.eval("(average 7 5 3)");
            fail("Expected EvaluationException");
        } catch (final EvaluationException e) {
            assertTrue(e.getMessage().startsWith("Expected 2"));
        }
    }

    @Test
    public void testSimpleConditional() {
        final SExpression result = eval("(IF (> 1 2) (QUOTE BAZ) (QUOTE FOO) )");
        assertEquals("FOO", result.toString());
    }

    @Test
    public void testSingleQuoteStringLiteral() {
        final SExpression sexp = eval("'FOO");
        assertTrue(sexp.isAtom());
        assertEquals("FOO", sexp.toString());
    }

    @Test
    public void testQuoteStringLiteral() {
        //(QUOTE FOO) => FOO
        final SExpression sexp = eval("(QUOTE FOO)");
        assertTrue(sexp.isAtom());
        assertEquals("FOO", sexp.toString());
    }

    @Test
    public void testSingleQuoteList() {
        final SExpression sexp = eval("'(FOO BAR)");
        assertTrue(sexp.isList());
        assertEquals("( FOO BAR )", sexp.toString());
    }

    @Test
    public void testSingleQuoteListWithNestedQuote() {
        final SExpression sexp = eval("'(FOO BAR 'BAZ)");
        assertTrue(sexp.isList());
        assertEquals("( FOO BAR ' BAZ )", sexp.toString());
    }

    @Test
    public void testQuoteList() {
        final SExpression sexp = eval("(QUOTE (FOO BAR))");
        assertTrue(sexp.isList());
        assertEquals("( FOO BAR )", sexp.toString());
    }

    @Test
    public void testQuoteLists() {
        final SExpression sexp = eval("(QUOTE ((FOO BAR) (BAZ QUX)))");
        assertTrue(sexp.isList());
        assertEquals("( ( FOO BAR ) ( BAZ QUX ) )", sexp.toString());
        assertEquals(2, sexp.toList().length().toI());
    }

    @Test
    public void testSetQQuoteList() {
        final Session session = newSession();
        final SExpression result = session.eval("(SETQ X '(A B))");
        assertTrue(result.isList());
        assertEquals("( A B )", result.toString());
        final SExpression X = session.eval("X");
        assertEquals("( A B )", X.toString());
    }

    @Test
    public void testSetQExpression() {
        final Session session = newSession();
        final SExpression result = session.eval("(SETQ X (+ (* 2 3) (* 3 4)))");
        assertTrue(result.isAtom());
        assertEquals("18", result.toString());
        final SExpression X = session.eval("X");
        assertEquals("18", X.toString());
    }

    @Test
    public void testSetQVariableTwice() {
        final Session session = newSession();
        SExpression result = session.eval("(SETQ X '(A B))");
        assertTrue(result.isList());
        assertEquals("( A B )", result.toString());
        SExpression X = session.eval("X");
        assertEquals("( A B )", X.toString());

        result = session.eval("(SETQ X '(C D))");
        assertTrue(result.isList());
        assertEquals("( C D )", result.toString());
        X = session.eval("X");
        assertEquals("( C D )", X.toString());
    }
}
