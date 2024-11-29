package org.ulithi.jlisp.test.mem;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.NilReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link org.ulithi.jlisp.mem.Cell}.
 */
public class CellTestCase {

    /**
     * Creates a default new cell and verifies its properties. A new cell is (NIL . NIL). Because
     * its first element is NIL, its first element is both and atom and a list, and the list itself
     * is empty/nil. Converted to an Atom, it evaluates to Atom.F (false).
     */
    @Test
    public void testCreateNewCell() {
        final Cell cell = Cell.create();
        assertTrue(cell.isCell());
        assertEquals(NilReference.NIL, cell.getFirst());
        assertEquals(NilReference.NIL, cell.getRest());
        assertFalse(cell.isAtom());
        assertTrue(cell.hasList());  // NIL is the same as an empty list.
        assertFalse(cell.isList());
        assertTrue(cell.isNil());
        assertTrue(cell.isTerminal());
        final Atom atom = cell.toAtom();
        assertEquals(Atom.NIL, atom);
        final List list = List.create(cell);
        assertTrue(list.isEmpty());
        assertEquals("NIL", list.toString());
    }

    /**
     * An empty list and a list with just a NIL atom are the same.
     */
    @Test
    public void testCreateCellWithFalseAtomReference() {
        final Atom atom = Atom.F;
        final Cell cell = Cell.create(atom);
        assertTrue(cell.isCell());
        assertEquals(atom, cell.getFirst());
        assertEquals(NilReference.NIL, cell.getRest());
        assertFalse(cell.isAtom());
        assertTrue(cell.hasAtom());
        assertTrue(cell.getFirst().isAtom());
        assertFalse(cell.isList());
        assertFalse(cell.isNil());
        assertTrue(cell.isTerminal());
        assertEquals(Atom.F, cell.toAtom());
        final List list = List.create(cell);
        assertFalse(list.isEmpty());
        assertEquals("( F )", list.toString());
    }

    @Test
    public void testCreateCellWithAtomReference() {
        final Atom atom = Atom.create("HELLO");
        final Cell cell = Cell.create(atom);
        assertTrue(cell.isCell());
        assertFalse(cell.isAtom());
        assertTrue(cell.hasAtom());
        assertTrue(cell.getFirst().isAtom());
        assertEquals(atom, cell.getFirst());
        assertEquals(NilReference.NIL, cell.getRest());
        assertFalse(cell.isList());
        assertFalse(cell.isNil());
        assertTrue(cell.isTerminal());
        assertEquals(atom, cell.toAtom());
        assertEquals("(HELLO . NIL)", cell.toString());
    }

    @Test
    public void testCreateCellAsList() {
        final Atom atom = Atom.create("HELLO");
        final Cell cell = Cell.create(atom);
        assertTrue(cell.isCell());
        final Cell listCell = Cell.createAsList(cell);
        assertTrue(listCell.isCell());
        assertEquals(atom, cell.getFirst());
        assertEquals(NilReference.NIL, cell.getRest());
        assertFalse(listCell.isAtom());
        assertTrue(listCell.hasList());
        assertFalse(listCell.isList());
        assertFalse(listCell.isNil());
        assertTrue(cell.isTerminal());
        assertEquals(atom, cell.toAtom());
        assertEquals("((HELLO . NIL) . NIL)", listCell.toString());
    }

    @Test
    public void testCreateCellForStringLiteral() {
        final Cell cell = Cell.create("HELLO");
        assertTrue(cell.isCell());
        assertFalse(cell.isAtom());
        assertTrue(cell.hasAtom());
        assertTrue(cell.getFirst() instanceof Atom);
        assertTrue(cell.getFirst().isAtom());
        assertEquals(NilReference.NIL, cell.getRest());
        assertFalse(cell.isList());
        assertFalse(cell.isNil());
        assertTrue(cell.isTerminal());
        assertEquals("HELLO", cell.toAtom().toS());
    }

    @Test
    public void testCreateCellForNumericLiteral() {
        final Cell cell = Cell.create(989);
        assertTrue(cell.isCell());
        assertFalse(cell.isAtom());
        assertTrue(cell.hasAtom());
        assertTrue(cell.getFirst().isAtom());
        assertTrue(cell.getFirst() instanceof Atom);
        assertEquals(NilReference.NIL, cell.getRest());
        assertFalse(cell.isList());
        assertFalse(cell.isNil());
        assertTrue(cell.isTerminal());
        assertEquals(989, cell.toAtom().toI());
    }

    @Test
    public void testCreateCellForBooleanTrue() {
        final Cell cell = Cell.create(true);
        assertTrue(cell.isCell());
        assertFalse(cell.isAtom());
        assertTrue(cell.hasAtom());
        assertTrue(cell.getFirst() instanceof Atom);
        assertTrue(cell.getFirst().isAtom());
        assertEquals(NilReference.NIL, cell.getRest());
        assertFalse(cell.isList());
        assertFalse(cell.isNil());
        assertTrue(cell.isTerminal());
        assertTrue(cell.toAtom().toB());
    }

    @Test
    public void testCreateCellForBooleanFalse() {
        final Cell cell = Cell.create(false);
        assertTrue(cell.isCell());
        assertFalse(cell.isAtom());
        assertTrue(cell.hasAtom());
        assertTrue(cell.getFirst() instanceof Atom);
        assertTrue(cell.getFirst().isAtom());
        assertEquals(NilReference.NIL, cell.getRest());
        assertFalse(cell.isList());
        assertFalse(cell.isNil());
        assertTrue(cell.isTerminal());
        assertFalse(cell.toAtom().toB());
    }

    @Test
    public void testCreateConsCellWithAtoms() {
        final Cell cell = Cell.create();
        cell.setFirst(Atom.create("FOO"));
        cell.setRest(Atom.create("BAR"));
        assertTrue(cell.isCell());
        assertFalse(cell.isNil());
        assertFalse(cell.isAtom());
        assertTrue(cell.hasAtom());
        assertFalse(cell.isList());
        assertTrue(cell.isTerminal());
        assertEquals("(FOO . BAR)", cell.toString());
    }

    @Test
    public void testCreateConsCellWithNilAndAtom() {
        final Cell cell = Cell.create();
        cell.setFirst(Atom.create(Atom.NIL));
        cell.setRest(Atom.create("BAR"));
        assertTrue(cell.isCell());
        assertFalse(cell.isNil());
        assertFalse(cell.isAtom());
        assertTrue(cell.hasAtom());
        assertTrue(cell.isTerminal());
        assertFalse(cell.isList());
        assertEquals("(NIL . BAR)", cell.toString());
    }

    @Test
    public void testCreateConsCellWithListAndAtom() {
        final Cell root = Cell.create(Atom.create("FOO"));
        final Cell end = Cell.create(Atom.create("BAR"));
        root.setRest(end);
        end.setRest(Atom.create("BAZ"));
        assertTrue(root.isCell());
        assertFalse(root.isNil());
        assertFalse(root.isAtom());
        assertTrue(root.hasAtom());
        assertFalse(root.isTerminal());
        assertTrue(end.isTerminal());
        assertFalse(root.isList());
        assertEquals("(FOO . (BAR . BAZ))", root.toString());
    }
}
