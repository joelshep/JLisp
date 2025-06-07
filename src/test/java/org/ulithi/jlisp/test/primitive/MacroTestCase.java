package org.ulithi.jlisp.test.primitive;

import org.junit.Ignore;
import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.test.suite.UnitTestUtilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.test.suite.UnitTestUtilities.newSession;

public class MacroTestCase {

    /**
     * Creates a simple macro to {@code write} its parameter to STDOUT, and verifies that it
     * compiles, expands and evaluates correctly in a couple different scenarios.
     */
    @Test
    public void testBasicMacroDefinitionAndExpansion() {
        final UnitTestUtilities.Session session = newSession();

        // Define the macro
        String macroDefinition = "(defmacro printer (x) (list 'write x))";
        session.eval(macroDefinition);

        // Verify macro was created and present in the environment.
        assertTrue("Macro 'printer' should be defined", session.isDefined("printer"));
        assertTrue("'printer' should be a macro", session.isMacro("printer"));

        assertEquals("Macro should expand correctly",
                     "( write 42 )",
                     session.eval("(MACROEXPAND '(printer 42))").toString());

        // Test full evaluation of expanded macro.
        SExpression result  = session.eval("(setq test-value 0)");

        assertEquals("Macro should expand correctly",
                     "( write test-value )",
                     session.eval("(MACROEXPAND '(printer test-value))").toString());

        session.eval("(printer test-value)");

        // Verify the print operation occurred with correct value
        // Note: This might need adjustment based on how your print implementation works
       // TODO assertEquals("0", interpreter.getLastOutput());
    }

    @Test
    public void testNestedMacroExpansion() {
        final UnitTestUtilities.Session session = newSession();
        // Define a macro that doubles its argument
        session.eval("(defmacro double-it (x) (list '* x 2))");

        // Define a macro that adds 1 to its argument
        session.eval("(defmacro add-one (x) (list '+ x 1))");

        // Test nested macro expansion
        String nestedMacro = "(double-it (add-one 5))";
        String expectedExpansion = "( * ( + 5 1 ) 2 )";

        assertEquals("Nested macros should expand correctly",
                     expectedExpansion,
                     session.eval("(MACROEXPAND '(double-it (add-one 5)))").toString());

        // Test evaluation of nested macro expansion
        SExpression result = session.eval(nestedMacro);
        assertTrue(result.isAtom());
        assertEquals("Nested macro should evaluate to 12",
                     12, result.toAtom().toI());
    }

    @Test
    @Ignore("progn and quoting not yet supported")
    public void testMacroWithMultipleExpressions() {
        final UnitTestUtilities.Session session = newSession();
        // Define a macro that creates a sequence of operations
        session.eval(
                "(defmacro do-twice (x) " +
                        "  (list 'progn " +
                        "        (list 'print x) " +
                        "        (list 'print x)))");

        String macroUse = "(do-twice \"hello\")";
        String expectedExpansion = "(progn (print \"hello\") (print \"hello\"))";

        assertEquals("Multi-expression macro should expand correctly",
                     expectedExpansion,
                     session.eval("(MACROEXPAND '" + macroUse + ")").toString());

        // Execute the macro
        final SExpression result = session.eval(macroUse);

        assertEquals("foo", result.toString());

        // Verify output occurred twice
        // TODO
        /*
        String output = interpreter.getLastOutput();
        assertTrue("Output should contain two hello prints",
                   output.contains("hello") &&
                           output.indexOf("hello") != output.lastIndexOf("hello"));
         */
    }
}
