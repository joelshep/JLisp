package org.ulithi.jlisp.test.core;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.NilReference;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.mem.Ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.parse;

/**
 * Unit tests for {@link org.ulithi.jlisp.core.SExpression};
 */
public class SExpressionTestCase {

    @Test
    public void testExplicitNilRef() {
        final Ref ref = NilReference.NIL;
        final SExpression sexp = SExpression.fromRef(ref);
        assertTrue(sexp.isAtom());
        assertEquals(Atom.NIL, sexp.toAtom());
    }

    @Test
    public void testNilCell() {
        final Cell cell = Cell.create();
        final SExpression sexp = SExpression.fromRef(cell);
        assertTrue(sexp.isAtom());
        assertEquals(Atom.NIL, sexp.toAtom());
    }

    @Test
    public void testNilRef() {
        final Ref ref = Cell.create().getFirst();
        final SExpression sexp = SExpression.fromRef(ref);
        assertTrue(sexp.isAtom());
        assertEquals(Atom.NIL, sexp.toAtom());
    }

    @Test
    public void testListCell() {
        final Cell cell = Cell.create("HELLO");

        SExpression sexp = SExpression.fromRef(cell);
        assertFalse(sexp.isAtom());
        assertTrue(sexp.isList());
        assertEquals("( HELLO )", String.valueOf(sexp));
    }

    @Test
    public void testNumericListCell() {
        final Cell cell = Cell.create(1234);

        SExpression sexp = SExpression.fromRef(cell);
        assertFalse(sexp.isAtom());
        assertTrue(sexp.isList());
        assertEquals("( 1234 )", String.valueOf(sexp));

        final Ref ref = cell.getFirst();
        sexp = SExpression.fromRef(ref);
        assertEquals(1234, ((Atom)sexp).toI());
    }

    @Test
    public void testBooleanListCell() {
        final Cell trueCell = Cell.create(true);

        SExpression sexp = SExpression.fromRef(trueCell);
        assertFalse(sexp.isAtom());
        assertTrue(sexp.isList());
        assertEquals("( T )", String.valueOf(sexp));

        Ref ref = trueCell.getFirst();
        sexp = SExpression.fromRef(ref);
        assertTrue(((Atom) sexp).toB());

        final Cell falseCell = Cell.create(false);
        sexp = SExpression.fromRef(falseCell);
        assertFalse(sexp.isAtom());
        assertTrue(sexp.isList());
        assertEquals("( F )", String.valueOf(sexp));

        ref = falseCell.getFirst();
        sexp = SExpression.fromRef(ref);
        assertFalse(((Atom) sexp).toB());
    }

    @Test
    public void testSimpleList() {
        final String expression = "(1 2 3 4)";
        final PTree pTree = parse(expression);
        SExpression sexp = SExpression.fromRef(pTree.root());
        assertTrue(sexp.isList());
        assertEquals("( 1 2 3 4 )", String.valueOf(sexp));
    }

    @Test
    public void testCarAtom() {
        final String expression = "(A B C D)";
        final PTree pTree = parse(expression);
        SExpression sexp = SExpression.fromRef(pTree.root().getFirst());
        assertTrue(sexp.isAtom());
        assertEquals("A", ((Atom)sexp).toS());
    }

    @Test
    public void testCarList() {
        final String expression = "((A B C) D E F)";
        final PTree pTree = parse(expression);
        SExpression sexp = SExpression.fromRef(pTree.root().getFirst());
        assertTrue(sexp.isList());
        assertEquals("( A B C )", String.valueOf(sexp));
    }

    @Test
    public void testCdr() {
        final String expression = "(A B C D)";
        final PTree pTree = parse(expression);
        SExpression sexp = SExpression.fromRef(pTree.root().getRest());
        assertTrue(sexp.isList());
        assertEquals("( B C D )", String.valueOf(sexp));
    }
}
