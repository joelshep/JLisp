package org.ulithi.jlisp.test.core;

import org.junit.Test;
import org.ulithi.jlisp.core.Environment;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.primitive.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for {@link org.ulithi.jlisp.core.Environment}.
 */
public class EnvironmentTestCase {

    @Test
    public void testCoreFunctionLookup() {
        final Environment env = new Environment();
        assertTrue(env.isDefined("plus"));
        assertTrue(env.isDefined("PLUS"));
        assertTrue(env.isDefined("+"));
        final Function function = (Function) env.getBinding("plus");
        assertEquals("plus", function.name().toLowerCase());
    }

    @Test
    public void testUndefinedLookup() {
        final Environment env = new Environment();
        assertFalse(env.isDefined("foo"));
        final Function function = (Function) env.getBinding("foo");
        assertNull(function);
    }

    @Test
    public void testScopeIndexUnderflow() {
        final Environment env = new Environment();
        env.startScope();
        env.endScope();

        try {
            env.endScope();
            fail("Expected EvaluationException");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    @Test
    public void testSimpleScope() {
        final Environment env = new Environment();
        env.startScope();
        env.addBinding("foo", createFunction("foo"));

        final Function func = (Function) env.getBinding("foo");
        assertNotNull(func);
        assertEquals("foo", func.name());

        env.endScope();
        assertNull(env.getBinding("foo"));
    }

    @Test
    public void testNestedScope() {
        final Environment env = new Environment();
        env.startScope();
        env.addBinding("foo", createFunction("foo"));

        env.startScope();
        env.addBinding("bar", createFunction("bar"));

        final Function foo = (Function) env.getBinding("foo");
        assertNotNull(foo);
        assertEquals("foo", foo.name());

        final Function bar = (Function) env.getBinding("bar");
        assertNotNull(bar);
        assertEquals("bar", bar.name());

        env.endScope();
        assertNull(env.getBinding("bar"));
        assertNotNull(env.getBinding("foo"));
        assertEquals("foo", ((Function)env.getBinding("foo")).name());

        env.endScope();
        assertNull(env.getBinding("bar"));
        assertNull(env.getBinding("foo"));
    }

    @Test
    public void testOuterScopeShadowsInnerScope() {
        final Environment env = new Environment();
        env.startScope();
        env.addBinding("foo", createFunction("foo"));

        env.startScope();
        env.addBinding("foo", createFunction("bar"));

        final Function bar = (Function) env.getBinding("foo");
        assertNotNull(bar);
        assertEquals("bar", bar.name());

        env.endScope();

        assertNotNull(env.getBinding("foo"));
        assertEquals("foo", ((Function)env.getBinding("foo")).name());

        env.endScope();
        assertNull(env.getBinding("foo"));
    }

    @Test
    public void testAddingBindingOutsideAScopeThrows() {
        final Environment env = new Environment();
        try {
            env.addBinding("foo", createFunction("foo"));
            fail("Expected EvaluationException");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    @Test
    public void testBindingPackageProvidedNameThrows() {
        final Environment env = new Environment();
        env.startScope();
        env.addBinding("foo", createFunction("foo"));
        try {
            env.addBinding("PLUS", createFunction("bar"));
            fail("Expected EvaluationException");
        } catch (final EvaluationException e) {
            // Expected.
        }
    }

    private static Function createFunction(final String name) {
        return new Function() {
            @Override
            public String name() { return name; }

            @Override
            public SExpression apply(SExpression sexp) {
                return SExpression.create(Cell.create(name));
            }
        };
    }


}
