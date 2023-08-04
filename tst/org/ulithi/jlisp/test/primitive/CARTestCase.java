package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.primitive.CAR;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.parse;

public class CARTestCase {

    @Test
    public void testCAROfLiteral() {
        final String expression = "HELLO";
        final PTree pTree = parse(expression);

        try {
            (new CAR()).apply(SExpression.create(pTree.root()));
            fail("CAR should throw exception if argument is not a list");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    @Test
    public void testCAROfSingleElementList() {
        final String expression = "(HELLO)";
        final PTree pTree = parse(expression);
        final Atom atom = (Atom)(new CAR()).apply(SExpression.create(pTree.root()));
        assertEquals("HELLO", atom.toS());
    }

    @Test
    public void testCAROfSimpleList() {
        final String expression = "(4 5 6)";
        final PTree pTree = parse(expression);
        final Atom atom = (Atom)(new CAR()).apply(SExpression.create(pTree.root()));
        assertEquals(4, atom.toI());
    }

    @Test
    public void testCAROfListOfLists() {
        final String expression = "((A B) (C D) (E F)))";
        final PTree pTree = parse(expression);
        final List list = (List)(new CAR()).apply(SExpression.create(pTree.root()));
        assertEquals("(A . (B . NIL))", String.valueOf(list));
    }

    @Test
    public void testCAROfEmptyList() {
        final String expression = "()";
        final PTree pTree = parse(expression);
        final Atom atom = (Atom)(new CAR()).apply(SExpression.create(pTree.root()));
        assertEquals(Atom.NIL, atom);
    }
}
