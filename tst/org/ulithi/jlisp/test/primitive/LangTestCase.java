package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;
import org.ulithi.jlisp.mem.PTree;
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
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        assertEquals("((PHONE . (HOME . NIL)) . NIL)", String.valueOf(sexp));
    }

    @Test
    public void testConsAtomToList() {
        // CONS BAR (BAZ) -> (BAR BAZ)  (BAR . (BAZ . ))
        final String expression = "(CONS (QUOTE BAR) (QUOTE (BAZ)))";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        assertEquals("(BAR . (BAZ . NIL))", String.valueOf(sexp));
    }

    @Test
    public void testConsAtomsToList() {
        // CONS FOO (CONS BAR (BAZ)) -> (FOO BAR BAZ)  (FOO . (BAR . (BAZ . NIL)))
        final String expression = "(CONS (QUOTE FOO) (CONS (QUOTE BAR) (QUOTE (BAZ))))";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        assertEquals("(FOO . (BAR . (BAZ . NIL)))", String.valueOf(sexp));
    }

    @Test
    public void testConsListToList() {
        // CONS (NOW IS) (THE TIME) -> ((NOW IS) THE TIME)  ((NOW . (IS . NIL)) . (THE . (TIME . NIL)))
        final String expression = "(CONS (QUOTE (NOW IS)) (QUOTE (THE TIME)))";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        assertEquals("((NOW . (IS . NIL)) . (THE . (TIME . NIL)))", String.valueOf(sexp));
        assertEquals(3, sexp.toList().length().toAtom().toI());
    }

    @Test
    public void testQuoteStringLiteral() {
        //(QUOTE FOO) => FOO
        final String expression = "(QUOTE FOO)";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        assertTrue(sexp.isAtom());
        assertEquals("FOO", sexp.toString());
    }

    @Test
    public void testQuoteList() {
        // (QUOTE (FOO BAR)) => (FOO BAR)
        final String expression = "(QUOTE (FOO BAR))";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        assertTrue(sexp.isList());
        assertEquals("(FOO . (BAR . NIL))", sexp.toString());
    }

    @Test
    public void testQuoteLists() {
        // (QUOTE ((FOO BAR) (BAZ QUX))) => ((FOO BAR) (BAZ QUX))
        final String expression = "(QUOTE ((FOO BAR) (BAZ QUX)))";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        assertTrue(sexp.isList());
        assertEquals("((FOO . (BAR . NIL)) . ((BAZ . (QUX . NIL)) . NIL))", sexp.toString());
        assertEquals(2, sexp.toList().length().toI());
    }
}
