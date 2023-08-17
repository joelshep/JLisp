package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.primitive.Eval;

import static org.junit.Assert.assertEquals;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.parse;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.CONS}.
 */
public class CONSTestCase {

    @Test
    public void testConsEmptyListToEmptyList() {
        // CONS () () -> ()
        final String expression = "(CONS () ()))";
        final PTree pTree = parse(expression);
        System.out.println(pTree);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        System.out.println(sexp);
        assertEquals("(NIL . NIL)", String.valueOf(sexp));
    }

    @Test
    public void testConsAtoms() {
        // CONS 1 2 3 => (1 2 3)
        final String expression = "(CONS 1 2 3)";
        final PTree pTree = parse(expression);
        System.out.println(pTree);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        System.out.println(sexp);
        assertEquals("(1 . (2 . (3 . NIL)))", String.valueOf(sexp));
    }

    @Test
    public void testConsLiteralToEmptyList() {
        // CONS HELLO () -> (HELLO)   (HELLO . NIL)
        final String expression = "(CONS HELLO ())";
        final PTree pTree = parse(expression);
        System.out.println(pTree);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        System.out.println(sexp);
        assertEquals("(HELLO . NIL)", String.valueOf(sexp));
    }

    @Test
    public void testConsListToEmptyList() {
        // CONS (PHONE HOME) () -> ((PHONE HOME))  ((PHONE . (HOME . NIL)) . NIL)
        final String expression = "(CONS (QUOTE (PHONE HOME)) ())";
        final PTree pTree = parse(expression);
        System.out.println(pTree);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        assertEquals("((PHONE . (HOME . NIL)) . NIL)", String.valueOf(sexp));
    }

    @Test
    public void testConsAtomToList() {
        // CONS BAR (BAZ) -> (BAR BAZ)  (BAR . (BAZ . ))
        final String expression = "(CONS (QUOTE BAR) (QUOTE (BAZ)))";
        final PTree pTree = parse(expression);
        System.out.println(pTree);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        System.out.println(sexp);
        assertEquals("(BAR . (BAZ . NIL))", String.valueOf(sexp));
    }

    @Test
    public void testConsAtomsToList() {
        // CONS FOO (CONS BAR (BAZ)) -> (FOO BAR BAZ)  (FOO . (BAR . (BAZ . NIL)))
        final String expression = "(CONS (QUOTE FOO) (CONS (QUOTE BAR) (QUOTE (BAZ))))";
        final PTree pTree = parse(expression);
        System.out.println(pTree);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        System.out.println(sexp);
        assertEquals("(FOO . (BAR . (BAZ . NIL)))", String.valueOf(sexp));
    }

    @Test
    public void testConsListToList() {
        // CONS (NOW IS) (THE TIME) -> ((NOW IS) THE TIME)  ((NOW . (IS . NIL)) . (THE . (TIME . NIL)))
        final String expression = "(CONS (QUOTE (NOW IS)) (QUOTE (THE TIME)))";
        final PTree pTree = parse(expression);
        System.out.println(pTree);
        final SExpression sexp = (new Eval()).apply(pTree.root());
        System.out.println(sexp);
        assertEquals("((NOW . (IS . NIL)) . (THE . (TIME . NIL)))", String.valueOf(sexp));
        assertEquals(3, sexp.toList().length().toAtom().toI());
    }
}
