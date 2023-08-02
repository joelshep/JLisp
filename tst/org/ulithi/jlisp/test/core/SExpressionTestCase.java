package org.ulithi.jlisp.test.core;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.mem.Cell;
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
    public void testNilCell() {
        final Cell cell = Cell.create();
        final SExpression atom = SExpression.create(cell);
        assertEquals(Atom.NIL, atom);
    }

    @Test
    public void testNilRef() {
        final Ref ref = Cell.create().getFirst();
        final SExpression atom = SExpression.create(ref);
        assertEquals(Atom.NIL, atom);
    }

    @Test
    public void testStorageCell() {
        final Cell cell = Cell.createStorage("HELLO");

        SExpression atom = SExpression.create(cell);
        assertTrue(atom.isAtom());
        assertEquals("HELLO", ((Atom)atom).toS());

        final Ref ref = cell.getFirst();
        atom = SExpression.create(ref);
        assertEquals("HELLO", ((Atom)atom).toS());
    }

    @Test
    public void testListCell() {
        final Cell cell = Cell.create("HELLO");

        SExpression sexp = SExpression.create(cell);
        assertFalse(sexp.isAtom());
        assertTrue(sexp.isList());
        assertEquals("(HELLO . NIL)", String.valueOf(sexp));
    }

    @Test
    public void testNumericStorageCell() {
        final Cell cell = Cell.createStorage(1234);

        SExpression atom = SExpression.create(cell);
        assertTrue(atom.isAtom());
        assertEquals(1234, ((Atom)atom).toI());

        final Ref ref = cell.getFirst();
        atom = SExpression.create(ref);
        assertEquals(1234, ((Atom)atom).toI());
    }

    @Test
    public void testNumericListCell() {
        final Cell cell = Cell.create(1234);

        SExpression sexp = SExpression.create(cell);
        assertFalse(sexp.isAtom());
        assertTrue(sexp.isList());
        assertEquals("(1234 . NIL)", String.valueOf(sexp));

        final Ref ref = cell.getFirst();
        sexp = SExpression.create(ref);
        assertEquals(1234, ((Atom)sexp).toI());
    }

    @Test
    public void testBooleanStorageCell() {
        final Cell trueCell = Cell.createStorage(true);

        SExpression atom = SExpression.create(trueCell);
        assertTrue(atom.isAtom());
        assertTrue(((Atom) atom).toB());

        Ref ref = trueCell.getFirst();
        atom = SExpression.create(ref);
        assertTrue(((Atom) atom).toB());

        final Cell falseCell = Cell.createStorage(false);
        atom = SExpression.create(falseCell);
        assertTrue(atom.isAtom());
        assertFalse(((Atom) atom).toB());

        ref = falseCell.getFirst();
        atom = SExpression.create(ref);
        assertFalse(((Atom) atom).toB());
    }

    @Test
    public void testBooleanListCell() {
        final Cell trueCell = Cell.create(true);

        SExpression sexp = SExpression.create(trueCell);
        assertFalse(sexp.isAtom());
        assertTrue(sexp.isList());
        assertEquals("(T . NIL)", String.valueOf(sexp));

        Ref ref = trueCell.getFirst();
        sexp = SExpression.create(ref);
        assertTrue(((Atom) sexp).toB());

        final Cell falseCell = Cell.create(false);
        sexp = SExpression.create(falseCell);
        assertFalse(sexp.isAtom());
        assertTrue(sexp.isList());
        assertEquals("(NIL . NIL)", String.valueOf(sexp));

        ref = falseCell.getFirst();
        sexp = SExpression.create(ref);
        assertFalse(((Atom) sexp).toB());
    }

    @Test
    public void testSimpleList() {
        final String expression = "(1 2 3 4)";
        final PTree pTree = parse(expression);
        SExpression list = SExpression.create(pTree.root());
        assertTrue(list.isList());
        assertEquals("(1 . (2 . (3 . (4 . NIL))))", String.valueOf(list));
    }

    @Test
    public void testCarAtom() {
        final String expression = "(A B C D)";
        final PTree pTree = parse(expression);
        SExpression atom = SExpression.create(pTree.root().getFirst());
        assertTrue(atom.isAtom());
        assertEquals("A", ((Atom)atom).toS());
    }

    @Test
    public void testCarList() {
        final String expression = "((A B C) D E F)";
        final PTree pTree = parse(expression);
        SExpression list = SExpression.create(pTree.root().getFirst());
        assertTrue(list.isList());
        assertEquals("(A . (B . (C . NIL)))", String.valueOf(list));
    }

    @Test
    public void testCdr() {
        final String expression = "(A B C D)";
        final PTree pTree = parse(expression);
        SExpression list = SExpression.create(pTree.root().getRest());
        assertTrue(list.isList());
        assertEquals("(B . (C . (D . NIL)))", String.valueOf(list));
    }
}
