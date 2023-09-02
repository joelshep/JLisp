package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.primitive.Eval;
import org.ulithi.jlisp.primitive.Lang;

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
        final String expression = "HELLO";
        final PTree pTree = parse(expression);

        try {
            (new Lang.CAR()).apply(SExpression.create(pTree.root()));
            fail("CAR should throw exception if argument is not a list");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    @Test
    public void testCAROfSingleElementList() {
        final String expression = "(HELLO)";
        final PTree pTree = parse(expression);
        final Atom atom = (Atom)(new Lang.CAR()).apply(SExpression.create(pTree.root()));
        assertEquals("HELLO", atom.toS());
    }

    @Test
    public void testCAROfSimpleList() {
        final String expression = "(4 5 6)";
        final PTree pTree = parse(expression);
        final Atom atom = (Atom)(new Lang.CAR()).apply(SExpression.create(pTree.root()));
        assertEquals(4, atom.toI());
    }

    @Test
    public void testCAROfListOfLists() {
        final String expression = "((A B) (C D) (E F)))";
        final PTree pTree = parse(expression);
        final List list = (List)(new Lang.CAR()).apply(SExpression.create(pTree.root()));
        assertEquals("(A . (B . NIL))", String.valueOf(list));
    }

    @Test
    public void testCAROfEmptyList() {
        final String expression = "()";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Lang.CAR()).apply(SExpression.create(pTree.root()));
        assertTrue(sexp.isList());
        final List list = sexp.toList();
        assertTrue(list.isEmpty());
    }


    @Test
    public void testCDROfLiteral() {
        final String expression = "HELLO";
        final PTree pTree = parse(expression);

        try {
            (new Lang.CDR()).apply(SExpression.create(pTree.root()));
            fail("CAR should throw exception if argument is not a list");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    @Test
    public void testCDROfSingleElementList() {
        final String expression = "(HELLO)";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Lang.CDR()).apply(SExpression.create(pTree.root()));
        assertTrue(sexp.isList());
        final List list = sexp.toList();
        assertTrue(list.isEmpty());
    }

    @Test
    public void testCDROfSimpleList() {
        final String expression = "(4 5 6)";
        final PTree pTree = parse(expression);
        final List list = (List)(new Lang.CDR()).apply(SExpression.create(pTree.root()));
        assertEquals("(5 . (6 . NIL))", String.valueOf(list));
    }

    @Test
    public void testCDROfListOfLists() {
        final String expression = "((A B) (C D) (E F))";
        final PTree pTree = parse(expression);
        final List list = (List)(new Lang.CDR()).apply(SExpression.create(pTree.root()));
        assertEquals("((C . (D . NIL)) . ((E . (F . NIL)) . NIL))", String.valueOf(list));
    }

    @Test
    public void testCDROfEmptyList() {
        final String expression = "()";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Lang.CDR()).apply(SExpression.create(pTree.root()));
        assertTrue(sexp.isList());
        final List list = sexp.toList();
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsEmptyListToEmptyList() {
        // CONS () () -> ()
        final String expression = "(CONS () ()))";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        assertEquals("(NIL . NIL)", String.valueOf(sexp));
    }

    @Test
    public void testConsAtoms() {
        // CONS 1 2 3 => (1 2 3)
        final String expression = "(CONS 1 2 3)";
        final PTree pTree = parse(expression);

        try {
            (new Eval()).apply(pTree.root());
            fail("Expected WrongArgumentCountException");
        } catch (final WrongArgumentCountException e) {
            // Expected.
        }
    }

    @Test
    public void testConsAtomAndList() {
        // (CONS 1 (QUOTE (2 3))) => (1 2 3)
        final String expression = "(CONS 1 (QUOTE (2 3)))";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        assertEquals("(1 . (2 . (3 . NIL)))", String.valueOf(sexp));
    }

    @Test
    public void testConsLiteralToEmptyList() {
        // CONS HELLO () -> (HELLO)   (HELLO . NIL)
        final String expression = "(CONS HELLO ())";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new Eval()).apply(pTree.root());
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
}
