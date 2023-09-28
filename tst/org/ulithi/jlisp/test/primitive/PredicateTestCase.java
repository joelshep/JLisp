package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Predicate}.
 */
public class PredicateTestCase {

    @Test
    public void testNumericLiteralIsAtom() {
        final String expression = "(ATOM (QUOTE 3))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals(Atom.T, sexp);
    }

    @Test
    public void testListOfNumbersIsNotAtom() {
        final String expression = "(ATOM (QUOTE (1 2 3)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals(Atom.F, sexp);
    }

    @Test
    public void testStringLiteralIsAtom() {
        final String expression = "(ATOM (QUOTE HELLO))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals(Atom.T, sexp);
    }

    @Test
    public void testNumericLiteralIsInteger() {
        final String expression = "(INTEGERP (QUOTE 3))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals(Atom.T, sexp);
    }

    @Test
    public void testStringLiteralIsNotInteger() {
        final String expression = "(INTEGERP (QUOTE HELLO))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals(Atom.F, sexp);
    }

    @Test
    public void testListIsNotInteger() {
        final String expression = "(INTEGERP (QUOTE (1 2 3)))";
        final SExpression sexp = UnitTestUtilities.evaluate(expression);
        assertEquals(Atom.F, sexp);
    }
}
