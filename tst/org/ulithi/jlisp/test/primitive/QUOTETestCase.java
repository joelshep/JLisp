package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.primitive.Eval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.parse;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.QUOTE}.
 */
public class QUOTETestCase {

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
