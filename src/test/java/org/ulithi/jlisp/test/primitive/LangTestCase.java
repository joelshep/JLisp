package org.ulithi.jlisp.test.primitive;

import org.junit.Ignore;
import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.InvalidArgumentException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;
import org.ulithi.jlisp.test.suite.UnitTestUtilities.Session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.eval;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.newSession;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Lang}.
 */
public class LangTestCase {

    @Test
    public void testSimpleApply() {
        assertEquals(9, eval("(APPLY 'PLUS '(2 3 4))").toAtom().toI());
    }

    @Test
    public void testSimpleApplyWithLambda() {
        assertEquals(8, eval("(APPLY (LAMBDA (X) (+ 5 X)) '(3))").toAtom().toI());
    }

    @Test
    public void testApplyToListArgument() {
        assertEquals("( B C )", eval("(APPLY 'CDR '((A B C)))").toList().toString());
    }

    @Test(expected = InvalidArgumentException.class)
    public void testCAROfLiteral() {
        // CAR should throw exception if argument is not a list
        eval("(CAR (QUOTE HELLO))");
    }

    @Test
    public void testCAROfNIL() {
        final SExpression sexp = eval("(CAR NIL)");
        assertTrue(sexp.isList());
        assertTrue(sexp.isNil());
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
    public void testCDROfNIL() {
        final SExpression sexp = eval("(CDR NIL)");
        assertTrue(sexp.isList());
        assertTrue(sexp.isNil());
    }

    @Test
    public void testCAROfEmptyList() {
        final SExpression sexp = eval("(CAR (QUOTE ()))");
        assertTrue(sexp.isList());
        assertTrue(sexp.toList().isEmpty());
    }

    @Test(expected = InvalidArgumentException.class)
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
        final SExpression sexp = session.eval("(COND ((EQL 1 1) 'foo) ((SETQ X 1) 'bar))");
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
    public void testConsAtomToAtom() {
        assertEquals("( A . B )", eval("(CONS 'A 'B)").toString());
    }

    @Test
    public void testConsNilToAtom() {
        assertEquals("( NIL . A )", eval("(CONS NIL 'A)").toString());
    }

    @Test
    public void testConsAtomToNil() {
        assertEquals("( A )", eval("(CONS 'A NIL)").toString());
    }

    @Test
    public void testConsListToAtom() {
        assertEquals("( ( A B ) . C )", eval("(CONS '(A B) 'C)").toString());
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
    public void testConsListToNil() {
        assertEquals("( ( A B ) )", eval("(CONS '(A B) NIL)").toString());
    }

    @Test
    public void testConsNilToList() {
        assertEquals("( NIL A B )", eval("(CONS NIL '(A B))").toString());
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
        final String result = eval("(CAR (CONS 'x 'y))").toAtom().toS();
        assertEquals("x", result);
    }

    @Test
    public void evaluateCdrConsExpression() {
        final String result = eval("(CDR (CONS 'x 'y))").toAtom().toS();
        assertEquals("y", result);
    }

    @Test
    public void evaluateCdrConsListExpression() {
        final int result = eval("(CDR (CONS 'x (PLUS 1 2)))").toAtom().toI();
        assertEquals(3, result);
    }

    @Test
    public void evaluateConsExpression() {
        final String result = eval("(CONS 'x 'y)").toList().toString();
        assertEquals("( x . y )", result);
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

    @Test
    public void testRecursiveDefun() {
        final Session session = newSession();
        assertEquals("factorial",
                     session.eval(" (defun factorial (x) (if (eql x 0) 1 (* x (factorial (- x 1)))))").toAtom().toS());
        assertEquals(120, session.eval("(factorial 5)").toAtom().toI());
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
            assertTrue(e.getMessage().startsWith("Too few arguments"));
            assertTrue(e.getMessage().endsWith("expected 2"));
        }

        try {
            session.eval("(average 7 5 3)");
            fail("Expected EvaluationException");
        } catch (final EvaluationException e) {
            assertTrue(e.getMessage().startsWith("Too many arguments"));
            assertTrue(e.getMessage().endsWith("expected 2"));
        }
    }

    @Test
    public void testDefunWithRestParameter() {
        // Define a function that cons together the first argument and all rest args into a list
        // (assuming there is at least on rest arg).
        Session session = newSession();
        session.eval("(defun con-ser (first &rest others) (cons first others))");

        SExpression result = session.eval("(con-ser 1 2)");
        assertTrue(result.isList());
        assertEquals(2, result.toList().lengthAsInt());
        assertEquals("( 1 2 )", result.toString());

        result = session.eval("(con-ser 1 2 3 4)");
        assertTrue(result.isList());
        assertEquals(4, result.toList().lengthAsInt());
        assertEquals("( 1 2 3 4 )", result.toString());
    }

    @Test
    public void testDefunWithRestParameterAndOperation() {
        // Define a function that sums all arguments.
        Session session = newSession();
        session.eval("(defun sum-all (first &rest numbers) (+ first (apply '+ numbers)))");

        SExpression result = session.eval("(sum-all 1 2 3 4)");
        assertEquals(10, result.toAtom().toI());
    }

    @Test
    public void testDefunWithOptionalParameter() {
        // Test function with one optional parameter
        Session session = newSession();
        session.eval("(defun greet (name &optional title) (if title (list title name) name))");

        // Test without optional arg
        SExpression result = session.eval("(greet 'John)");
        assertEquals("John", result.toString());

        // Test with optional arg
        result = session.eval("(greet 'John 'Dr)");
        assertEquals("( Dr John )", result.toString());
    }

    @Test
    public void testDefunWithOptionalAndTooFewArguments() {
        Session session = newSession();
        session.eval("(defun test-args (req1 req2 &optional opt) (list req1 req2 opt))");

        try {
            session.eval("(test-args 1)");  // Should throw - missing required arg
        } catch (final EvaluationException e) {
            assertTrue(e.getMessage().startsWith("Too few arguments"));
            assertTrue(e.getMessage().endsWith("expected 4"));
        }
    }

    @Ignore("Default values for optional parameters not implemented")
    @Test
    public void testOptionalParameterDefaultValue() {
        Session session = newSession();
        session.eval("(defun test-default (x &optional (y 10)) (+ x y))");

        SExpression result = session.eval("(test-default 5)");
        assertEquals(15, result.toAtom().toI());

        result = session.eval("(test-default 5 20)");
        assertEquals(25, result.toAtom().toI());
    }

    @Ignore("Default values for optional parameters not implemented")
    @Test
    public void testDefunWithMultipleOptionalParameters() {
        // Test function with multiple optional parameters and default values
        Session session = newSession();
        session.eval("(defun make-point (x &optional (y 0) (z 0)) (list x y z))");

        // Test with just required arg
        SExpression result = session.eval("(make-point 1)");
        assertEquals("( 1 0 0 )", result.toString());

        // Test with one optional arg
        result = session.eval("(make-point 1 2)");
        assertEquals("( 1 2 0 )", result.toString());

        // Test with all args
        result = session.eval("(make-point 1 2 3)");
        assertEquals("( 1 2 3 )", result.toString());
    }

    @Test
    public void testDefunWithOptionalAndRestParameters() {
        // Define function with both optional and rest parameters
        Session session = newSession();
        session.eval("(defun complex-args (req &optional opt &rest others) (list req opt others))");

        // Test with just required arg
        SExpression result = session.eval("(complex-args 1)");
        assertEquals("( 1 NIL NIL )", result.toString());

        // Test with optional arg
        result = session.eval("(complex-args 1 2)");
        assertEquals("( 1 2 NIL )", result.toString());

        // Test with rest args
        result = session.eval("(complex-args 1 2 3 4 5)");
        assertEquals("( 1 2 ( 3 4 5 ) )", result.toString());
    }

    @Test
    public void testInvalidRestParameter() {
        assertThrows(EvaluationException.class, () -> {
            eval("(defun bad-rest (x &rest r1 &rest r2) (list x r1 r2))");
        });
    }

    @Test
    public void testRestWithNoParameter() {
        assertThrows(EvaluationException.class, () -> {
            eval("(defun missing-rest-param (x &rest) x)");
        });
    }

    @Test
    public void testOptionalAfterRest() {
        assertThrows(EvaluationException.class, () -> {
            eval("(defun bad-optional-order (x &rest r &optional opt) (list x r opt))");
        });
    }

    @Test
    public void testMultipleOptional() {
        assertThrows(EvaluationException.class, () -> {
            eval("(defun multiple-optional (x &optional o1 &optional o2) (list x o1 o2))");
        });
    }

    @Test
    public void testSimpleEval() {
        final SExpression result = eval("(EVAL '(+ 1 2 3))");
        assertEquals(6, result.toAtom().toI());
    }

    @Test
    public void testEvalNil() {
        final SExpression result = eval("(EVAL NIL)");
        assertTrue(result.isNil());
    }

    @Test
    public void testEvalVariableDereference() {
        Session session = UnitTestUtilities.newSession();
        session.eval("(SETQ A 'B)");
        session.eval("(SETQ B 'C)");
        assertEquals("B", session.eval("A").toAtom().toS());
        assertEquals("C", session.eval("B").toAtom().toS());
        assertEquals("C", session.eval("(EVAL A)").toAtom().toS());
    }

    @Test
    public void testEvalRecursiveFunction() {
        Session session = UnitTestUtilities.newSession();
        session.eval(" (defun factorial (x) (if (eql x 0) 1 (* x (factorial (- x 1)))))");
        assertEquals(720, session.eval("(EVAL '(factorial 6))").toAtom().toI());
    }

    @Test
    public void testSimpleConditional() {
        final SExpression result = eval("(IF (> 2 1) (QUOTE BAZ) (QUOTE FOO) )");
        assertEquals("BAZ", result.toString());
    }

    @Test
    public void testSimpleConditionalElse() {
        final SExpression result = eval("(IF (> 1 2) (QUOTE BAZ) (QUOTE FOO) )");
        assertEquals("FOO", result.toString());
    }

    @Test
    public void testSimpleLambda() {
        final SExpression result = eval("((LAMBDA (x) (+ x x)) 17)");
        assertTrue(result.isAtom());
        assertEquals(34, result.toAtom().toI());
    }

    @Test
    public void testMapcarWithLambda() {
        final SExpression result = eval("(MAPCAR (LAMBDA (x) (+ x 2)) '(3 5 7))");
        assertEquals("( 5 7 9 )", result.toString());
    }

    @Test
    public void testMapcarWithTwoArguments() {
        final SExpression result = eval("(MAPCAR 'PLUS '(3 5 7) '(4 5 6))");
        assertEquals("( 7 10 13 )", result.toString());
    }

    @Test
    public void testMapcarWithListVariable() {
        final Session session = newSession();
        session.eval(" (SETQ a '(1 2 3))");
        final SExpression result = session.eval("(MAPCAR (LAMBDA (x) (+ 5 x)) a)");
        assertEquals("( 6 7 8 )", result.toString());
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
    public void testQuoteWithPlusOperator() {
        final SExpression sexp = eval("(list '+ x 1)");
        assertTrue(sexp.isList());
        assertEquals("( + x 1 )", sexp.toString());
        assertEquals(3, sexp.toList().lengthAsInt());
    }

    @Test
    public void testQuoteWithMultiplierOperator() {
        final SExpression sexp = eval("(list '* x 2)");
        assertTrue(sexp.isList());
        assertEquals("( * x 2 )", sexp.toString());
        assertEquals(3, sexp.toList().lengthAsInt());
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
