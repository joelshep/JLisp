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
        assertEquals(3, args.remaining());
        assertTrue(args.hasNext());
        for (int i = 0; i < args.length(); i++) {
            assertTrue(args.hasAtom());
            assertFalse(args.hasList());
            assertEquals(i+1, args.takeAtom().toI());
            assertEquals(args.length() - (i+1), args.remaining());
        }
        assertFalse(args.hasAtom());
        assertFalse(args.hasList());
        assertFalse(args.hasNext());
    }

    @Test
    public void testListArguments() {
        final SExpression params = eval("'((A B) (B C) (C D) (D E))");
        final Args args = Args.create(params);
        assertEquals(4, args.length());
        assertEquals(4, args.remaining());
        assertTrue(args.hasNext());
        assertFalse(args.hasAtom());
        assertTrue(args.hasList());
        assertEquals("( A B )", args.takeList().toString());
        assertEquals(3, args.remaining());
        assertTrue(args.hasList());
        assertEquals("( B C )", args.takeList().toString());
        assertEquals(2, args.remaining());
        assertTrue(args.hasList());
        assertEquals("( C D )", args.takeList().toString());
        assertEquals(1, args.remaining());
        assertTrue(args.hasList());
        assertEquals("( D E )", args.takeList().toString());
        assertEquals(0, args.remaining());
        assertFalse(args.hasList());
        assertFalse(args.hasNext());
    }

    @Test
    public void testMixedArguments() {
        final SExpression params = eval("'(A (B C) 3 () FOO)");
        final Args args = Args.create(params);
        assertEquals(5, args.length());
        assertEquals(5, args.remaining());
        assertTrue(args.hasNext());
        assertTrue(args.hasAtom());
        assertFalse(args.hasList());
        assertEquals("A", args.takeAtom().toS());
        assertEquals(4, args.remaining());
        assertFalse(args.hasAtom());
        assertTrue(args.hasList());
        assertEquals("( B C )", args.takeAny().toList().toString());
        assertEquals(3, args.remaining());
        assertTrue(args.hasAtom());
        assertFalse(args.hasList());
        assertEquals(3, args.takeAtom().toI());
        assertEquals(2, args.remaining());
        assertFalse(args.hasAtom());
        assertTrue(args.hasList());
        assertEquals("NIL", args.takeAny().toString()); // TODO - This is messed up.
        assertEquals(1, args.remaining());
        assertTrue(args.hasAtom());
        assertFalse(args.hasList());
        assertEquals("FOO", args.takeAtom().toS());
        assertEquals(0, args.remaining());
        assertFalse(args.hasAtom());
        assertFalse(args.hasList());
        assertFalse(args.hasNext());
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testWantTooManyArguments() {
        final SExpression params = eval("'(A)");
        final Args args = Args.create(params);
        assertEquals("A", args.takeAtom().toS());
        args.takeAny();
    }

    @Test
    public void testMinimumNumberOfArguments() {
        final SExpression params = eval("'(A B C)");
        final Args args = Args.create(params);
        assertEquals(3, args.length());
        args.expectMinLength(0);
        args.expectMinLength(1);
        args.expectMinLength(2);
        args.expectMinLength(3);
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testTooFewArguments() {
        final SExpression params = eval("'(A B C)");
        final Args args = Args.create(params);
        assertEquals(3, args.length());
        args.expectMinLength(4);
    }

    @Test(expected = InvalidArgumentException.class)
    public void testTakeAtomOnListArg() {
        final SExpression params = eval("'(A (B C))");
        final Args args = Args.create(params);
        assertEquals("A", args.takeAtom().toS());
        args.takeAtom();
    }

    @Test(expected = InvalidArgumentException.class)
    public void testTakeListOnAtomArg() {
        final SExpression params = eval("'((C B) A)");
        final Args args = Args.create(params);
        assertEquals("( C B )", args.takeList().toString());
        args.takeList();
    }

    @Test
    public void testEmptyArgumentList() {
        final SExpression params = eval("'()");
        final Args args = Args.create(params);
        assertEquals(0, args.length());
        assertFalse(args.hasNext());
    }

    @Test(expected = WrongArgumentCountException.class)
    public void testFewerArgumentsThanExpected() {
        final SExpression params = eval("'(A B C)");
        final Args args = Args.create(params);
        args.expectLength(4);
    }

    @Test
    public void testNILArgs() {
        final Args args = Args.create(Atom.NIL);
        assertEquals(0, args.length());
        assertEquals(0, args.remaining());
        assertFalse(args.hasNext());
    }
}
