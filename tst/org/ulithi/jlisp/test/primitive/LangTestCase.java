package org.ulithi.jlisp.test.primitive;

import org.junit.Ignore;
import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;
import org.ulithi.jlisp.primitive.Eval;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.parse;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Lang}.
 */
public class LangTestCase {

    @Test
    public void testCAROfLiteral() {
        final String expression = "(CAR (QUOTE HELLO))";

        try {
            UnitTestUtilities.evaluate(expression);
            fail("CAR should throw exception if argument is not a list");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    @Test
    public void testCAROfSingleElementList() {
        final String expression = "(CAR (QUOTE (HELLO)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals("HELLO", sexp.toAtom().toS());
    }

    @Test
    public void testCAROfSimpleList() {
        final String expression = "(CAR (QUOTE (4 5 6)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals(4, sexp.toAtom().toI());
    }

    @Test
    public void testCAROfListOfLists() {
        final String expression = "(CAR (QUOTE ((A B) (C D) (E F))))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals("(A . (B . NIL))", sexp.toList().toString()); //String.valueOf(list));
    }

    @Test
    public void testCAROfEmptyList() {
        final String expression = "(CAR (QUOTE ()))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }


    @Test
    public void testCDROfLiteral() {
        final String expression = "(CDR (QUOTE HELLO))";

        try {
            UnitTestUtilities.evaluate(expression);
            fail("CDR should throw exception if argument is not a list");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    @Test
    public void testCDROfSingleElementList() {
        final String expression = "(CDR (QUOTE (HELLO)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test
    public void testCDROfSimpleList() {
        final String expression = "(CDR (QUOTE (4 5 6)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals("(5 . (6 . NIL))", sexp.toList().toString());
    }

    @Test
    public void testCDROfListOfLists() {
        final String expression = "(CDR (QUOTE ((A B) (C D) (E F))))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals("((C . (D . NIL)) . ((E . (F . NIL)) . NIL))", sexp.toList().toString());
    }

    @Test
    public void testCDROfEmptyList() {
        final String expression = "(CDR (QUOTE ()))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test
    public void testConsEmptyListToEmptyList() {
        final String expression = "(CONS () ()))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test
    public void testConsAtoms() {
        final String expression = "(CONS 1 2 3)";

        try {
            UnitTestUtilities.evaluate(expression);
            fail("Expected WrongArgumentCountException");
        } catch (final WrongArgumentCountException e) {
            // Expected.
        }
    }

    @Test
    public void testConsAtomAndList() {
        // (CONS 1 (QUOTE (2 3))) => (1 2 3)
        final String expression = "(CONS 1 (QUOTE (2 3)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals("(1 . (2 . (3 . NIL)))", String.valueOf(sexp));
    }

    @Test
    public void testConsLiteralToEmptyList() {
        // CONS HELLO () -> (HELLO)   (HELLO . NIL)
        final String expression = "(CONS HELLO ())";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals("(HELLO . NIL)", String.valueOf(sexp));
    }

    @Test
    public void testConsListToEmptyList() {
        // CONS (PHONE HOME) () -> ((PHONE HOME))  ((PHONE . (HOME . NIL)) . NIL)
        final String expression = "(CONS (QUOTE (PHONE HOME)) ())";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals("((PHONE . (HOME . NIL)) . NIL)", String.valueOf(sexp));
    }

    @Test
    public void testConsAtomToList() {
        // CONS BAR (BAZ) -> (BAR BAZ)  (BAR . (BAZ . ))
        final String expression = "(CONS (QUOTE BAR) (QUOTE (BAZ)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals("(BAR . (BAZ . NIL))", String.valueOf(sexp));
    }

    @Test
    public void testConsAtomsToList() {
        // CONS FOO (CONS BAR (BAZ)) -> (FOO BAR BAZ)  (FOO . (BAR . (BAZ . NIL)))
        final String expression = "(CONS (QUOTE FOO) (CONS (QUOTE BAR) (QUOTE (BAZ))))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals("(FOO . (BAR . (BAZ . NIL)))", String.valueOf(sexp));
    }

    @Test
    public void testConsListToList() {
        // CONS (NOW IS) (THE TIME) -> ((NOW IS) THE TIME)  ((NOW . (IS . NIL)) . (THE . (TIME . NIL)))
        final String expression = "(CONS (QUOTE (NOW IS)) (QUOTE (THE TIME)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals("((NOW . (IS . NIL)) . (THE . (TIME . NIL)))", String.valueOf(sexp));
        assertEquals(3, sexp.toList().length().toAtom().toI());
    }

    @Test
    @Ignore
    public void evaluateCarExpression() {
        final SExpression sexp = UnitTestUtilities.evaluate("( CAR () )");
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test
    public void evaluateCarConsExpression() {
        final String expression = "(CAR (CONS x y))";
        final String result = UnitTestUtilities.evaluate(expression).toAtom().toS();
        assertEquals("x", result);
    }

    @Test
    public void evaluateCdrConsExpression() {
        final String expression = "(CDR (CONS x y))";
        final String result = UnitTestUtilities.evaluate(expression).toList().toString();
        assertEquals("(y . NIL)", result);
    }

    @Test
    public void evaluateCdrConsListExpression() {
        final String expression = "(CDR (CONS x (PLUS 1 2)))";
        final String result = UnitTestUtilities.evaluate(expression).toList().toString();
        assertEquals("(3 . NIL)", result);
    }

    @Test
    public void evaluateConsExpression() {
        final String expression = "(CONS x y)";
        final String result = UnitTestUtilities.evaluate(expression).toList().toString();
        assertEquals("(x . (y . NIL))", result);
    }

    /**
     * Simple test of the DEFUN function. First defines an "average" function, then invokes it.
     * This is a bit hacked up because it needs to retain the same eval instance, so it references
     * the same environment when defining the function and when applying it.
     */
    @Test
    public void testSimpleDefun() {
        // Evaluate defun, which should create the 'average' function and return its name.
        final Eval eval = new Eval();
        final String defun = "(defun average (x y) (QUOTIENT (PLUS x y) 2))";
        final SExpression defunResult = eval.apply(parse(defun).root());
        assertEquals("average", defunResult.toString());

        // Now evaluate the 'average' function that we just created.
        final SExpression avg = eval.apply(parse("(average 7 5)").root());
        assertEquals(6, avg.toAtom().toI());
    }

    /**
     * Creates a user function that takes zero arguments, invokes it and validates the result.
     */
    @Test
    public void testZeroArgDefun() {
        final Eval eval = new Eval();
        final String defun = "(defun eleven () (QUOTE 11))";
        final SExpression defunResult = eval.apply(parse(defun).root());
        assertEquals("eleven", defunResult.toString());

        // Now evaluate the 'average' function that we just created.
        final SExpression avg = eval.apply(parse("(eleven)").root());
        assertEquals(11, avg.toAtom().toI());
    }

    /**
     * Creates a user function that takes two arguments, invokes it with one and three arguments,
     * and verifies that both cause an EvaluationException to be thrown.
     */
    @Test
    public void testDefunParameterCountMismatch() {
        // Evaluate defun, which should create the 'average' function and return its name.
        final Eval eval = new Eval();
        final String defun = "(defun average (x y) (QUOTIENT (PLUS x y) 2))";
        final SExpression result = eval.apply(parse(defun).root());
        assertEquals("average", result.toString());

        try {
            eval.apply(parse("(average 7)").root());
            fail("Expected EvaluationException");
        } catch (final EvaluationException e) {
            assertTrue(e.getMessage().startsWith("Expected 2"));
        }

        try {
            eval.apply(parse("(average 7 5 3)").root());
            fail("Expected EvaluationException");
        } catch (final EvaluationException e) {
            assertTrue(e.getMessage().startsWith("Expected 2"));
        }
    }

    @Test
    public void testSimpleConditional() {
        final String expression = "(IF (> 1 2) (QUOTE BAZ) (QUOTE FOO) )";
        final SExpression result = UnitTestUtilities.evaluate(expression);
        assertEquals("FOO", result.toString());
    }

    @Test
    public void testQuoteStringLiteral() {
        //(QUOTE FOO) => FOO
        final String expression = "(QUOTE FOO)";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isAtom());
        assertEquals("FOO", sexp.toString());
    }

    @Test
    public void testQuoteList() {
        // (QUOTE (FOO BAR)) => (FOO BAR)
        final String expression = "(QUOTE (FOO BAR))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertEquals("(FOO . (BAR . NIL))", sexp.toString());
    }

    @Test
    public void testQuoteLists() {
        // (QUOTE ((FOO BAR) (BAZ QUX))) => ((FOO BAR) (BAZ QUX))
        final String expression = "(QUOTE ((FOO BAR) (BAZ QUX)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertTrue(sexp.isList());
        assertEquals("((FOO . (BAR . NIL)) . ((BAZ . (QUX . NIL)) . NIL))", sexp.toString());
        assertEquals(2, sexp.toList().length().toI());
    }
}
