package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Predicate}.
 */
public class PredicateTestCase {

    @Test
    public void testNumericLiteralIsAtom() {
        final String expression = "(ATOM (QUOTE 3))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals(Atom.T, sexp);
    }

    @Test
    public void testListOfNumbersIsNotAtom() {
        final String expression = "(ATOM (QUOTE (1 2 3)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals(Atom.F, sexp);
    }

    @Test
    public void testStringLiteralIsAtom() {
        final String expression = "(ATOM (QUOTE HELLO))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals(Atom.T, sexp);
    }

    @Test
    public void testNumericLiteralIsInteger() {
        final String expression = "(INTEGERP (QUOTE 3))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals(Atom.T, sexp);
    }

    @Test
    public void testStringLiteralIsNotInteger() {
        final String expression = "(INTEGERP (QUOTE HELLO))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals(Atom.F, sexp);
    }

    @Test
    public void testListIsNotInteger() {
        final String expression = "(INTEGERP (QUOTE (1 2 3)))";
        final SExpression sexp = UnitTestUtilities.eval(expression);
        assertEquals(Atom.F, sexp);
    }

    @Test
    public void testMinusP() {
        assertEquals(Atom.T, UnitTestUtilities.eval("(MINUSP -2)"));
        assertEquals(Atom.F, UnitTestUtilities.eval("(MINUSP 0)"));
        assertEquals(Atom.F, UnitTestUtilities.eval("(MINUSP 4)"));
    }

    @Test(expected = EvaluationException.class)
    public void testMinusPThrowsOnNonNumericArgument() {
        UnitTestUtilities.eval("(MINUSP 'FOO)");
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testMinusPThrowsOnMultipleArguments() {
        UnitTestUtilities.eval("(MINUSP -2 -2)");
    }

    @Test
    public void testPlusP() {
        assertEquals(Atom.F, UnitTestUtilities.eval("(PLUSP -2)"));
        assertEquals(Atom.F, UnitTestUtilities.eval("(PLUSP 0)"));
        assertEquals(Atom.T, UnitTestUtilities.eval("(PLUSP 4)"));
    }

    @Test(expected = EvaluationException.class)
    public void testPlusPThrowsOnNonNumericArgument() {
        UnitTestUtilities.eval("(PLUSP 'FOO)");
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testPlusPThrowsOnMultipleArguments() {
        UnitTestUtilities.eval("(PLUSP 2 2)");
    }

    @Test
    public void testZeroP() {
        assertEquals(Atom.F, UnitTestUtilities.eval("(ZEROP -2)"));
        assertEquals(Atom.T, UnitTestUtilities.eval("(ZEROP 0)"));
        assertEquals(Atom.F, UnitTestUtilities.eval("(ZEROP 4)"));
    }

    @Test(expected = EvaluationException.class)
    public void testZeroPThrowsOnNonNumericArgument() {
        UnitTestUtilities.eval("(ZEROP 'FOO)");
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testZeroPThrowsOnMultipleArguments() {
        UnitTestUtilities.eval("(ZEROP 0 0)");
    }
}
