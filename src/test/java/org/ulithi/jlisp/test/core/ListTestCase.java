package org.ulithi.jlisp.test.core;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.mem.Cell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link org.ulithi.jlisp.core.List}.
 */
public class ListTestCase {

    @Test
    public void testCreateEmptyList() {
        final List list = List.create();
        assertTrue(list.isEmpty());
        assertEquals(0, list.length().toI());
        assertEquals("NIL", list.toString());
    }

    @Test
    public void testCreateSingleElementList() {
        final List list = List.create(Cell.create("OCEAN"));
        assertFalse(list.isEmpty());
        assertEquals(1, list.length().toI());
        assertEquals("( OCEAN )", list.toString());
    }

    @Test
    public void testSimpleListAdd() {
        final List list = List.create()
                .add(Atom.create("HELLO"))
                .add(newSublist("THERE", "YOU"));
        assertEquals(2, list.length().toI());
        assertEquals("( HELLO ( THERE YOU ) )", list.toString());
    }

    @Test
    public void testMultipleListAdd() {
        final List list = List.create()
                .add(Atom.create("HELLO"))
                .add(newSublist("THERE", "YOU"))
                .add(newSublist("PRETTY", "GIRL"));
        assertEquals(3, list.length().toI());
        assertEquals("( HELLO ( THERE YOU ) ( PRETTY GIRL ) )", list.toString());
    }

    @Test
    public void testSimpleListAppend() {
        final List list = List.create()
                .add(Atom.create("HELLO"))
                .append(newSublist("THERE", "YOU"));
        assertEquals(3, list.length().toI());
        assertEquals("( HELLO THERE YOU )", list.toString());
    }

    @Test
    public void testMultipleListAppend() {
        final List list = List.create()
                .add(Atom.create("HELLO"))
                .append(newSublist("THERE", "YOU"))
                .append(newSublist("PRETTY", "GIRL"));
        assertEquals(5, list.length().toI());
        assertEquals("( HELLO THERE YOU PRETTY GIRL )", list.toString());
    }

    @Test
    public void testSimpleListSize() {
        final List list = List.create()
                .add(Atom.create("FOO"))
                .add(Atom.create("BAR"))
                .add(Atom.create("BAZ"));
        assertEquals(3, list.size().toI());
    }

    @Test
    public void testSingleNestedListSize() {
        final List list = List.create()
                .add(Atom.create("HELLO"))
                .add(newSublist("THERE", "YOU"));
        assertEquals(3, list.size().toI());
    }

    @Test
    public void testMultipleNestedListSize() {
        final List list = List.create()
                .add(newSublist("I", "LIKE"))
                .add(Atom.create("TO"))
                .add(newSublist("EAT", "BANANAS"));
        assertEquals(5, list.size().toI());
    }

    @Test
    public void testDeeplyNestedListSize() {
        final List innerList = List.create()
                .add(newSublist("I", "LIKE"))
                .add(Atom.create("TO"))
                .add(newSublist("EAT", "BANANAS"));

        final List outerList = List.create()
                .add(Atom.create("BUT"))
                .add(innerList);

        assertEquals(6, outerList.size().toI());
    }

    private static List newSublist(final String lhs, final String rhs) {
        return List.create()
                .add(Atom.create(lhs))
                .add(Atom.create(rhs));
    }
}
