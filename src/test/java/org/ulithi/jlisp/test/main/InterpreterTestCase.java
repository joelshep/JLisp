package org.ulithi.jlisp.test.main;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.ulithi.jlisp.main.Interpreter;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link org.ulithi.jlisp.main.Interpreter}.
 */
public class InterpreterTestCase {

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    private Interpreter lisp;

    @Before
    public void setUp() {
        lisp = new Interpreter();
        systemErrRule.muteForSuccessfulTests();
        systemOutRule.mute();
    }

    @After
    public void tearDown() {
        lisp = null;
        systemErrRule.clearLog();
        systemOutRule.clearLog();
    }

    @Test
    public void testNewInterpreter() {
        assertFalse(lisp.getName().isBlank());
        assertFalse(lisp.getVersion().isBlank());
    }

    @Test
    public void testEmptyExpression() {
        final Optional<Boolean> result = lisp.offer("");
        assertTrue(result.isPresent() && result.get());
    }

    @Test
    public void testOfferSimpleForm() {
        final Optional<Boolean> result = lisp.offer("(+ 1 2 3)");
        assertTrue(result.isPresent() && result.get());
        assertEquals("6", systemOutRule.getLog().trim());
    }

    @Test
    public void testOfferMultiLineForm() {
        Optional<Boolean> result;
        result = lisp.offer("(");
        assertFalse(result.isPresent());
        result = lisp.offer("+ 1 2");
        assertFalse(result.isPresent());
        result = lisp.offer(")");
        assertTrue(result.isPresent() && result.get());
        assertEquals("3", systemOutRule.getLog().trim());
    }

    @Test
    public void testOfferWithError() {
        final Optional<Boolean> result = lisp.offer("(+ 1 2 A)");
        assertTrue(result.isPresent() && !result.get());
        assertEquals("Can't convert string literal to number", systemErrRule.getLog().trim());
    }

    @Test
    public void testOfferMultipleForms() {
        Optional<Boolean> result;
        result = lisp.offer("(SETQ X 5)");
        assertTrue(result.isPresent() && result.get());
        result = lisp.offer("(+ 1 X)");
        assertTrue(result.isPresent() && result.get());
    }

    @Test
    public void testResetAfterOfferError() {
        Optional<Boolean> result;
        result = lisp.offer("(+ 1 2 'A)");
        assertTrue(result.isPresent() && !result.get());
        assertEquals("Can't convert string literal to number", systemErrRule.getLog().trim());
        lisp.reset();
        result = lisp.offer("(+ 1 2 3)");
        assertTrue(result.isPresent() && result.get());
    }
}
