package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.primitive.CDR;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.parse;

public class CDRTestCase {

    @Test
    public void testCDROfLiteral() {
        final String expression = "HELLO";
        final PTree pTree = parse(expression);

        try {
            (new CDR()).apply(SExpression.create(pTree.root()));
            fail("CAR should throw exception if argument is not a list");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    @Test
    public void testCDROfSingleElementList() {
        final String expression = "(HELLO)";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new CDR()).apply(SExpression.create(pTree.root()));
        assertTrue(sexp.isList());
        final List list = sexp.toList();
        assertTrue(list.isEmpty());
    }

    @Test
    public void testCDROfSimpleList() {
        final String expression = "(4 5 6)";
        final PTree pTree = parse(expression);
        final List list = (List)(new CDR()).apply(SExpression.create(pTree.root()));
        assertEquals("(5 . (6 . NIL))", String.valueOf(list));
    }

    @Test
    public void testCDROfListOfLists() {
        final String expression = "((A B) (C D) (E F))";
        final PTree pTree = parse(expression);
        final List list = (List)(new CDR()).apply(SExpression.create(pTree.root()));
        assertEquals("((C . (D . NIL)) . ((E . (F . NIL)) . NIL))", String.valueOf(list));
    }

    @Test
    public void testCDROfEmptyList() {
        final String expression = "()";
        final PTree pTree = parse(expression);
        final SExpression sexp = (new CDR()).apply(SExpression.create(pTree.root()));
        assertTrue(sexp.isList());
        final List list = sexp.toList();
        assertTrue(list.isEmpty());
    }
}
