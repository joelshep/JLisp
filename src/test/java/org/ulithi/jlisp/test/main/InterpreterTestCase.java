package org.ulithi.jlisp.test.main;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ulithi.jlisp.main.Interpreter;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link org.ulithi.jlisp.main.Interpreter}.
 */
public class InterpreterTestCase {

    private Interpreter lisp;

    @Before
    public void setUp() {
        lisp = new Interpreter();
    }

    @After
    public void tearDown() {
        lisp = null;
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
    }

    @Test
    public void testOfferWithError() {
        final Optional<Boolean> result = lisp.offer("+ 1 2 A");
        assertTrue(result.isPresent() && !result.get());
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
        lisp.reset();
        result = lisp.offer("(+ 1 2 3)");
        assertTrue(result.isPresent() && result.get());
    }
}
