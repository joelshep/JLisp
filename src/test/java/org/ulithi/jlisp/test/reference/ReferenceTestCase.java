package org.ulithi.jlisp.test.reference;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.ulithi.jlisp.main.Interpreter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ulithi.jlisp.parser.Grammar.FULL_LINE_COMMENT;

/**
 * Reads a file of unit tests as LISP forms and evaluates them.
 */
public class ReferenceTestCase {
    Interpreter lisp;
    Thread thread;

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Before
    public void setUp() {
        systemErrRule.muteForSuccessfulTests();
        systemOutRule.mute();
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
                Optional<Boolean> result = lisp.offer(line);
                assertTrue(line, result.isPresent() && result.get());
                if (isCode(line)) {
                    assertEquals(
                            line + ": " + systemErrRule.getLog().trim(), "T", systemOutRule.getLog().trim());
                }
                systemOutRule.clearLog();
                systemErrRule.clearLog();
            }

            reader.close();
        } catch (Exception e) {
            fail("Failed to run reference tests: " + e.getMessage());
        }
    }

    private static boolean isCode(final String line) {
        return !line.isBlank() && !(line.matches(FULL_LINE_COMMENT));
    }
}
