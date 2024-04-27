package org.ulithi.jlisp.test.mem;

import org.junit.Test;
import org.ulithi.jlisp.mem.Ref;

import static org.junit.Assert.assertFalse;

/**
 * Unit tests for {@link org.ulithi.jlisp.mem.Ref}.
 */
public class RefTestCase {

    @Test
    public void testRefDefaults() {
        final Ref ref = new Ref() { };
        assertFalse("isAtom", ref.isAtom());
        assertFalse("isCell", ref.isCell());
        assertFalse("isList", ref.isList());
        assertFalse("isNil", ref.isNil());
    }
}
