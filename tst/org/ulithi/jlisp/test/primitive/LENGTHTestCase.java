package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.primitive.Util.LENGTH;

import static org.junit.Assert.assertEquals;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.parse;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Util.LENGTH}.
 */
public class LENGTHTestCase {

    @Test
    public void testLengthOfEmptyList() {
        final String expression = "( )";
        final PTree pTree = parse(expression);
        final Atom atom = (new LENGTH().apply(SExpression.create(pTree.root()))).toAtom();
        assertEquals(0, atom.toI());
    }

    @Test
    public void testLengthOfSimpleList() {
        final String expression = "(1 2 3)";
        final PTree pTree = parse(expression);
        final Atom atom = (new LENGTH().apply(SExpression.create(pTree.root()))).toAtom();
        assertEquals(3, atom.toI());
    }

    @Test
    public void testLengthOfNestedList() {
        final String expression = "(1 (A B C) 3 (DEF))";
        final PTree pTree = parse(expression);
        final Atom atom = (new LENGTH().apply(SExpression.create(pTree.root()))).toAtom();
        assertEquals(4, atom.toI());
    }

    @Test
    public void testLengthOfNestedList2() {
        final String expression = "((A B C) 1 (DEF) 3)";
        final PTree pTree = parse(expression);
        final Atom atom = (new LENGTH().apply(SExpression.create(pTree.root()))).toAtom();
        assertEquals(4, atom.toI());
    }

}
