package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;

import static org.junit.Assert.assertEquals;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.eval;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Predicate}.
 */
public class PredicateTestCase {

    @Test
    public void testNumericLiteralIsAtom() {
        final SExpression sexp = eval("(ATOM (QUOTE 3))");
        assertEquals(Atom.T, sexp);
    }

    @Test
    public void testListOfNumbersIsNotAtom() {
        final SExpression sexp = eval("(ATOM (QUOTE (1 2 3)))");
        assertEquals(Atom.F, sexp);
    }

    @Test
    public void testStringLiteralIsAtom() {
        final SExpression sexp = eval("(ATOM (QUOTE HELLO))");
        assertEquals(Atom.T, sexp);
    }

    @Test
    public void testNumericLiteralIsInteger() {
        final SExpression sexp = eval("(INTEGERP (QUOTE 3))");
        assertEquals(Atom.T, sexp);
    }

    @Test
    public void testStringLiteralIsNotInteger() {
        final SExpression sexp = eval("(INTEGERP (QUOTE HELLO))");
        assertEquals(Atom.F, sexp);
    }

    @Test
    public void testListIsNotInteger() {
        final SExpression sexp = eval("(INTEGERP (QUOTE (1 2 3)))");
        assertEquals(Atom.F, sexp);
    }

    @Test
    public void testMinusP() {
        assertEquals(Atom.T, eval("(MINUSP -2)"));
        assertEquals(Atom.F, eval("(MINUSP 0)"));
        assertEquals(Atom.F, eval("(MINUSP 4)"));
    }

    @Test(expected = EvaluationException.class)
    public void testMinusPThrowsOnNonNumericArgument() {
        eval("(MINUSP 'FOO)");
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testMinusPThrowsOnMultipleArguments() {
        eval("(MINUSP -2 -2)");
    }

    @Test
    public void testPlusP() {
        assertEquals(Atom.F, eval("(PLUSP -2)"));
        assertEquals(Atom.F, eval("(PLUSP 0)"));
        assertEquals(Atom.T, eval("(PLUSP 4)"));
    }

    @Test(expected = EvaluationException.class)
    public void testPlusPThrowsOnNonNumericArgument() {
        eval("(PLUSP 'FOO)");
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testPlusPThrowsOnMultipleArguments() {
        eval("(PLUSP 2 2)");
    }

    @Test
    public void testZeroP() {
        assertEquals(Atom.F, eval("(ZEROP -2)"));
        assertEquals(Atom.T, eval("(ZEROP 0)"));
        assertEquals(Atom.F, eval("(ZEROP 4)"));
    }

    @Test(expected = EvaluationException.class)
    public void testZeroPThrowsOnNonNumericArgument() {
        eval("(ZEROP 'FOO)");
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testZeroPThrowsOnMultipleArguments() {
        eval("(ZEROP 0 0)");
    }
}
