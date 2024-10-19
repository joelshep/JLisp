package org.ulithi.jlisp.test.primitive;

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
            UnitTestUtilities.eval(expression);
            fail("CAR should throw exception if argument is not a list");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    @Test
    public void testCAROfSingleElementList() {
        final String expression = "(CAR (QUOTE (HELLO)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals("HELLO", sexp.toAtom().toS());
    }

    @Test
    public void testCAROfSimpleList() {
        final String expression = "(CAR (QUOTE (4 5 6)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals(4, sexp.toAtom().toI());
    }

    @Test
    public void testCAROfSingleQuoteList() {
        final String expression = "(CAR '(A B C))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals("A", sexp.toAtom().toS());
    }

    @Test
    public void testCAROfListOfLists() {
        final String expression = "(CAR (QUOTE ((A B) (C D) (E F))))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals("( A B )", sexp.toList().toString()); //String.valueOf(list));
    }

    @Test
    public void testCAROfEmptyList() {
        final String expression = "(CAR (QUOTE ()))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }


    @Test
    public void testCDROfLiteral() {
        final String expression = "(CDR (QUOTE HELLO))";

        try {
            UnitTestUtilities.eval(expression);
            fail("CDR should throw exception if argument is not a list");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    @Test
    public void testCDROfSingleElementList() {
        final String expression = "(CDR (QUOTE (HELLO)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test
    public void testCDROfSimpleList() {
        final String expression = "(CDR (QUOTE (4 5 6)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals("( 5 6 )", sexp.toList().toString());
    }

    @Test
    public void testCDROfListOfLists() {
        final String expression = "(CDR (QUOTE ((A B) (C D) (E F))))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals("( ( C D ) ( E F ) )", sexp.toList().toString());
    }

    @Test
    public void testCDROfEmptyList() {
        final String expression = "(CDR (QUOTE ()))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test
    public void testConsEmptyListToEmptyList() {
        final String expression = "(CONS () ())";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test
    public void testSimpleCond() {
        String expression = "(COND ((EQL 2 2) SNOO) ((EQL 3 4) BOO) ((EQL 5 5) (+ 4 5)))";
        SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals("SNOO", sexp.toString());

        expression = "(COND ((EQL 2 3) SNOO) ((EQL 4 4) BOO) ((EQL 5 5) (+ 4 5)))";
        sexp = UnitTestUtilities.eval(expression);
        assertEquals("BOO", sexp.toString());

        expression = "(COND ((EQL 2 3) SNOO) ((EQL 3 4) BOO) ((EQL 5 5) (+ 4 5)))";
        sexp = UnitTestUtilities.eval(expression);
        assertEquals(9, sexp.toAtom().toI());
    }

    @Test
    public void testCondDefun() {
        final Eval eval = new Eval();
        final String defun = "(defun abs (x) (COND ((minusp x) (- x)) (T x)))";
        final SExpression defunResult = eval.apply(parse(defun).root());
        assertEquals("abs", defunResult.toString());

        // Now evaluate the 'abs' function that we just created.
        SExpression abs = eval.apply(parse("(abs -4)").root());
        assertEquals(4, abs.toAtom().toI());

        abs = eval.apply(parse("(abs 5)").root());
        assertEquals(5, abs.toAtom().toI());
    }

    @Test
    public void testEmptyCond() {
        final String expression = "(COND )";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isNil());
    }

    @Test
    public void testCondWithAllFalseConditions() {
        final String expression = "(COND ((EQL 1 2) 'foo) ((EQL 3 4) 'bar))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isNil());
    }

    @Test
    public void testCondShortcutsEvaluation() {
        final Eval eval = new Eval();
        eval.apply(parse("(SETQ X 0)").root());
        final SExpression sexp = eval.apply(parse("(COND ((EQ 1 1) 'foo) ((SETQ X 1) 'bar))").root());
        assertTrue(sexp.isAtom());
        assertEquals("foo", sexp.toAtom().toS());
        final SExpression x = eval.apply(parse("X").root());
        assertTrue(x.isAtom());
        assertEquals(0, x.toAtom().toI());
    }

    @Test
    public void testConsAtoms() {
        final String expression = "(CONS 1 2 3)";

        try {
            UnitTestUtilities.eval(expression);
            fail("Expected WrongArgumentCountException");
        } catch (final WrongArgumentCountException e) {
            // Expected.
        }
    }

    @Test
    public void testConsAtomAndList() {
        // (CONS 1 (QUOTE (2 3))) => (1 2 3)
        final String expression = "(CONS 1 (QUOTE (2 3)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals("( 1 2 3 )", String.valueOf(sexp));
    }

    @Test
    public void testConsLiteralToEmptyList() {
        // CONS HELLO () -> (HELLO)   (HELLO . NIL)
        final String expression = "(CONS HELLO ())";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals("( HELLO )", String.valueOf(sexp));
    }

    @Test
    public void testConsListToEmptyList() {
        // CONS (PHONE HOME) () -> ((PHONE HOME))  ((PHONE . (HOME . NIL)) . NIL)
        final String expression = "(CONS (QUOTE (PHONE HOME)) ())";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals("( ( PHONE HOME ) )", String.valueOf(sexp));
    }

    @Test
    public void testConsAtomToList() {
        // CONS BAR (BAZ) -> (BAR BAZ)  (BAR . (BAZ . ))
        final String expression = "(CONS (QUOTE BAR) (QUOTE (BAZ)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals("( BAR BAZ )", String.valueOf(sexp));
    }

    @Test
    public void testConsAtomsToList() {
        // CONS FOO (CONS BAR (BAZ)) -> (FOO BAR BAZ)  (FOO . (BAR . (BAZ . NIL)))
        final String expression = "(CONS (QUOTE FOO) (CONS (QUOTE BAR) (QUOTE (BAZ))))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals("( FOO BAR BAZ )", String.valueOf(sexp));
    }

    @Test
    public void testConsListToList() {
        // CONS (NOW IS) (THE TIME) -> ((NOW IS) THE TIME)  ((NOW . (IS . NIL)) . (THE . (TIME . NIL)))
        final String expression = "(CONS (QUOTE (NOW IS)) (QUOTE (THE TIME)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals("( ( NOW IS ) THE TIME )", String.valueOf(sexp));
        assertEquals(3, sexp.toList().length().toAtom().toI());
    }

    @Test
    public void evaluateCarExpression() {
        final SExpression sexp = UnitTestUtilities.eval("( CAR () )");
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test
    public void evaluateCarConsExpression() {
        final String expression = "(CAR (CONS x y))";
        final String result = UnitTestUtilities.eval(expression).toAtom().toS();
        assertEquals("x", result);
    }

    @Test
    public void evaluateCdrConsExpression() {
        final String expression = "(CDR (CONS x y))";
        final String result = UnitTestUtilities.eval(expression).toList().toString();
        assertEquals("( y )", result);
    }

    @Test
    public void evaluateCdrConsListExpression() {
        final String expression = "(CDR (CONS x (PLUS 1 2)))";
        final String result = UnitTestUtilities.eval(expression).toList().toString();
        assertEquals("( 3 )", result);
    }

    @Test
    public void evaluateConsExpression() {
        final String expression = "(CONS x y)";
        final String result = UnitTestUtilities.eval(expression).toList().toString();
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
        final SExpression result = UnitTestUtilities.eval(expression);
        assertEquals("FOO", result.toString());
    }

    @Test
    public void testSingleQuoteStringLiteral() {
        final String expression = "'FOO";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertEquals("FOO", sexp.toString());
    }

    @Test
    public void testQuoteStringLiteral() {
        //(QUOTE FOO) => FOO
        final String expression = "(QUOTE FOO)";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isAtom());
        assertEquals("FOO", sexp.toString());
    }

    @Test
    public void testSingleQuoteList() {
        final String expression = "'(FOO BAR)";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isList());
        assertEquals("( FOO BAR )", sexp.toString());
    }

    @Test
    public void testSingleQuoteListWithNestedQuote() {
        final String expression = "'(FOO BAR 'BAZ)";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isList());
        assertEquals("( FOO BAR ' BAZ )", sexp.toString());
    }

    @Test
    public void testQuoteList() {
        // (QUOTE (FOO BAR)) => (FOO BAR)
        final String expression = "(QUOTE (FOO BAR))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isList());
        assertEquals("( FOO BAR )", sexp.toString());
    }

    @Test
    public void testQuoteLists() {
        // (QUOTE ((FOO BAR) (BAZ QUX))) => ((FOO BAR) (BAZ QUX))
        final String expression = "(QUOTE ((FOO BAR) (BAZ QUX)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertTrue(sexp.isList());
        assertEquals("( ( FOO BAR ) ( BAZ QUX ) )", sexp.toString());
        assertEquals(2, sexp.toList().length().toI());
    }

    @Test
    public void testSetQQuoteList() {
        final Eval eval = new Eval();
        final String setq = "(SETQ X '(A B))";
        final SExpression result = eval.apply(parse(setq).root());
        assertTrue(result.isList());
        assertEquals("( A B )", result.toString());
        final SExpression X = eval.apply(parse("X").root());
        assertEquals("( A B )", X.toString());
    }

    @Test
    public void testSetQExpression() {
        final Eval eval = new Eval();
        final String setq = "(SETQ X (+ (* 2 3) (* 3 4)))";
        final SExpression result = eval.apply(parse(setq).root());
        assertTrue(result.isAtom());
        assertEquals("18", result.toString());
        final SExpression X = eval.apply(parse("X").root());
        assertEquals("18", X.toString());
    }

    @Test
    public void testSetQVariableTwice() {
        final Eval eval = new Eval();
        String setq = "(SETQ X '(A B))";
        SExpression result = eval.apply(parse(setq).root());
        assertTrue(result.isList());
        assertEquals("( A B )", result.toString());
        SExpression X = eval.apply(parse("X").root());
        assertEquals("( A B )", X.toString());

        setq = "(SETQ X '(C D))";
        result = eval.apply(parse(setq).root());
        assertTrue(result.isList());
        assertEquals("( C D )", result.toString());
        X = eval.apply(parse("X").root());
        assertEquals("( C D )", X.toString());

    }
}
