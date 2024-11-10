package org.ulithi.jlisp.test.primitive;

import org.junit.Test;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.InvalidArgumentException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;
import org.ulithi.jlisp.primitive.Args;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.eval;

/**
 * Unit tests for {@link org.ulithi.jlisp.primitive.Args}.
 */
public class ArgsTestCase {

    @Test
    public void testSimpleArgList() {
        final SExpression params = eval("'(1 2 3)");
        final Args args = Args.create(params);
        assertEquals(3, args.length());
        assertTrue(args.hasNext());
        for (int i = 0; i < args.length(); i++) {
            assertEquals(i+1, args.wantAtom().toI());
        }
        assertFalse(args.hasNext());
    }

    @Test
    public void testListArguments() {
        final SExpression params = eval("'((A B) (B C) (C D) (D E))");
        final Args args = Args.create(params);
        assertEquals(4, args.length());
        assertTrue(args.hasNext());
        assertEquals("( A B )", args.wantList().toString());
        assertEquals("( B C )", args.wantList().toString());
        assertEquals("( C D )", args.wantList().toString());
        assertEquals("( D E )", args.wantList().toString());
        assertFalse(args.hasNext());
    }

    @Test
    public void testMixedArguments() {
        final SExpression params = eval("'(A (B C) 3 () FOO)");
        final Args args = Args.create(params);
        assertEquals(5, args.length());
        assertTrue(args.hasNext());
        assertEquals("A", args.wantAtom().toS());
        assertEquals("( B C )", args.wantAny().toList().toString());
        assertEquals(3, args.wantAtom().toI());
        assertEquals("NIL", args.wantAny().toString()); // TODO - This is messed up.
        assertEquals("FOO", args.wantAtom().toS());
        assertFalse(args.hasNext());
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testWantTooManyArguments() {
        final SExpression params = eval("'(A)");
        final Args args = Args.create(params);
        assertEquals("A", args.wantAtom().toS());
        args.wantAny();
    }

    @Test(expected = InvalidArgumentException.class)
    public void testWantAtomOnListArg() {
        final SExpression params = eval("'(A (B C))");
        final Args args = Args.create(params);
        assertEquals("A", args.wantAtom().toS());
        args.wantAtom();
    }

    @Test(expected = InvalidArgumentException.class)
    public void testWantListOnAtomArg() {
        final SExpression params = eval("'((C B) A)");
        final Args args = Args.create(params);
        assertEquals("( C B )", args.wantList().toString());
        args.wantList();
    }

    @Test
    public void testEmptyArgumentList() {
        final SExpression params = eval("'()");
        final Args args = Args.create(params);
        assertEquals(0, args.length());
        assertFalse(args.hasNext());
    }

    @Test
    public void testNILArgs() {
        final Args args = Args.create(Atom.NIL);
        assertEquals(0, args.length());
        assertFalse(args.hasNext());
    }
}
