package org.ulithi.jlisp.test.mem;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.mem.NilReference;
import org.ulithi.jlisp.mem.Ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link org.ulithi.jlisp.mem.NilReference}.
 */
public class NilReferenceTestCase {

    @Test
    public void testNilReferenceDefaults() {
        final Ref ref = NilReference.NIL;
        assertTrue("isAtom", ref.isAtom());
        assertFalse("isCell", ref.isCell());
        assertTrue("isList", ref.isList());
        assertTrue("isNil", ref.isNil());
        assertEquals("NIL", ref.toString());
    }

    @Test
    public void testNilReferenceToAtom() {
        final Atom atom = NilReference.NIL.toAtom();
        assertTrue(atom.isAtom());
        assertTrue(atom.isNil());
        assertEquals("NIL", atom.toString());

    }
}
