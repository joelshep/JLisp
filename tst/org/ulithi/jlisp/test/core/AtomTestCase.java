package org.ulithi.jlisp.test.core;

import org.junit.Test;
import org.ulithi.jlisp.exception.TypeConversionException;
import org.ulithi.jlisp.core.Atom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for {@link Atom}.
 */
public class AtomTestCase {

    @Test
    public void testIntegerLiterals() {
        Atom atom = Atom.create(43);
        assertTrue(atom.isLiteral());
        assertFalse(atom.isNil());
        assertTrue(atom.isNumber());
        assertFalse(atom.isSymbol());
        assertEquals("43", atom.toS());
        assertTrue(atom.toB());
        assertEquals(43, atom.toI());
        assertEquals("43", atom.toString());

        atom = Atom.create(0);
        assertTrue(atom.isLiteral());
        assertFalse(atom.isNil());
        assertTrue(atom.isNumber());
        assertFalse(atom.isSymbol());
        assertEquals("0", atom.toS());
        assertFalse(atom.toB());
        assertEquals(0, atom.toI());
        assertEquals("0", atom.toString());

        atom = Atom.create(-1);
        assertTrue(atom.isLiteral());
        assertFalse(atom.isNil());
        assertTrue(atom.isNumber());
        assertFalse(atom.isSymbol());
        assertEquals("-1", atom.toS());
        assertTrue(atom.toB());
        assertEquals(-1, atom.toI());
        assertEquals("-1", atom.toString());
    }

    @Test
    public void testStringLiterals() {
        Atom atom = Atom.create("Hello");
        assertTrue(atom.isLiteral());
        assertFalse(atom.isNil());
        assertFalse(atom.isNumber());
        assertFalse(atom.isSymbol());
        assertEquals("Hello", atom.toS());
        assertTrue(atom.toB());
        try {
            atom.toI();
            fail("Expected TypeConversionException not thrown");
        } catch (TypeConversionException e) {
            // Expected
        }
        assertEquals("Hello", atom.toString());

        atom = Atom.create("");
        assertTrue(atom.isLiteral());
        assertFalse(atom.isNil());
        assertFalse(atom.isNumber());
        assertFalse(atom.isSymbol());
        assertEquals("", atom.toS());
        assertFalse(atom.toB());
        try {
            atom.toI();
            fail("Expected TypeConversionException not thrown");
        } catch (TypeConversionException e) {
            // Expected
        }
        assertEquals("", atom.toString());

        atom = Atom.create("123");
        assertTrue(atom.isLiteral());
        assertFalse(atom.isNil());
        assertFalse(atom.isNumber());
        assertFalse(atom.isSymbol());
        assertEquals("123", atom.toS());
        assertTrue(atom.toB());
        try {
            atom.toI();
            fail("Expected TypeConversionException not thrown");
        } catch (TypeConversionException e) {
            // Expected
        }
        assertEquals("123", atom.toString());

        atom = Atom.create("0");  // Zero
        assertTrue(atom.isLiteral());
        assertFalse(atom.isNil());
        assertFalse(atom.isNumber());
        assertFalse(atom.isSymbol());
        assertEquals("0", atom.toS());
        assertTrue(atom.toB());
        try {
            atom.toI();
            fail("Expected TypeConversionException not thrown");
        } catch (TypeConversionException e) {
            // Expected
        }
        assertEquals("0", atom.toString());
    }

    @Test
    public void testBooleanLiterals() {
        Atom atom = Atom.T;
        assertTrue(atom.isLiteral());
        assertFalse(atom.isNil());
        assertFalse(atom.isNumber());
        assertFalse(atom.isSymbol());
        assertEquals("T", atom.toS());
        assertTrue(atom.toB());
        assertEquals(-1, atom.toI());
        assertEquals("T", atom.toString());

        atom = Atom.F;
        assertTrue(atom.isLiteral());
        assertFalse(atom.isNil());
        assertFalse(atom.isNumber());
        assertFalse(atom.isSymbol());
        assertEquals("F", atom.toS());
        assertFalse(atom.toB());
        assertEquals(0, atom.toI());
        assertEquals("F", atom.toString());

        atom = Atom.create(true);
        assertTrue(atom.isLiteral());
        assertFalse(atom.isNil());
        assertFalse(atom.isNumber());
        assertFalse(atom.isSymbol());
        assertEquals("T", atom.toS());
        assertTrue(atom.toB());
        assertEquals(-1, atom.toI());
        assertEquals("T", atom.toString());
        assertEquals(Atom.T, atom);

        atom = Atom.create(false);
        assertTrue(atom.isLiteral());
        assertFalse(atom.isNil());
        assertFalse(atom.isNumber());
        assertFalse(atom.isSymbol());
        assertEquals("F", atom.toS());
        assertFalse(atom.toB());
        assertEquals(0, atom.toI());
        assertEquals("F", atom.toString());
        assertEquals(Atom.F, atom);
    }

    @Test
    public void testSymbols() {
        Atom atom = Atom.createSymbol("FOO");
        assertFalse(atom.isLiteral());
        assertFalse(atom.isNil());
        assertFalse(atom.isNumber());
        assertTrue(atom.isSymbol());
        try {
            atom.toS();
            fail("Expected TypeConversionException not thrown");
        } catch (TypeConversionException e) {
            // Expected.
        }

        try {
            atom.toB();
            fail("Expected TypeConversionException not thrown");
        } catch (TypeConversionException e) {
            // Expected.
        }

        try {
            atom.toI();
            fail("Expected TypeConversionException not thrown");
        } catch (TypeConversionException e) {
            // Expected.
        }

        assertEquals("FOO", atom.toString());
    }
}
