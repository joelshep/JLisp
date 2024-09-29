package org.ulithi.jlisp.test.reference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ulithi.jlisp.main.Interpreter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.fail;

/**
 * Reads a file of unit tests as LISP forms and evaluates them.
 */
public class ReferenceTestCase {

    Interpreter lisp;
    Thread thread;

    @Before
    public void setUp() {
        lisp = new Interpreter();
        lisp.initialize();
        thread = new Thread(lisp);
        thread.start();
    }

    @After
    public void tearDown() {
        thread.stop();
        thread = null;
        lisp = null;
    }

    @Test
    public void evaluateReferenceTests() {
        try (InputStream is = getClass().getResourceAsStream("/reference-tests/reference-tests.lsp")) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            String line;

            while ((line = reader.readLine()) != null) {
                lisp.offer(line);
            }

            reader.close();
        } catch (Exception e) {
            fail("Failed to run reference tests: " + e.getMessage());
        }
    }
}
