package org.ulithi.jlisp.test.utils;

import org.ulithi.jlisp.commons.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link StringUtils}.
 */
public class StringUtilsTestCase {

    /**
     * List of string tokens used by tests in this test case.
     */
    private List<String> tokens;

    /**
     * Initialize the string token list.
     */
    @Before
    public void setUp() {
        tokens = Arrays.asList("( A B ( C D ) E )".split("\\s"));
    }

    /**
     * Release the string token list.
     */
    @After
    public void tearDown() {
        tokens = null;
    }

    /**
     * Searches for a target token, in a sublist that starts before the token.
     */
    @Test
    public void indexOf_HappyCase() {
        int index = StringUtils.indexOf(tokens, 4, ")");
        assertEquals(6, index);
        assertEquals(")", tokens.get(index));
    }

    /**
     * Searches for a target token from the very beginning of the original token list.
     */
    @Test
    public void indexOf_WithFullSublist() {
        int index = StringUtils.indexOf(tokens, 0, ")");
        assertEquals(6, index);
        assertEquals(")", tokens.get(index));
    }

    /**
     * Searches for a target token, in a sublist that starts at the target token.
     */
    @Test
    public void indexOf_TargetAtSublistStart() {
        int index = StringUtils.indexOf(tokens, 6, ")");
        assertEquals(6, index);
        assertEquals(")", tokens.get(index));
    }

    /**
     * Searches for the target token, in a sublist that does not include the target token.
     */
    @Test
    public void indexOf_TargetBeforeSublistStart() {
        int index = StringUtils.indexOf(tokens, 7, "(");
        assertEquals(-1, index);
    }

    /**
     * Searches for a target token using an invalid start index.
     */
    @Test(expected=IllegalArgumentException.class)
    public void indexOf_InvalidSublistStart() {
        StringUtils.indexOf(tokens, tokens.size() + 3, "(");
    }

    /**
     * Searches for a target token in an empty list.
     */
    @Test(expected=IllegalArgumentException.class)
    public void indexOf_EmptyList() {
        StringUtils.indexOf(new ArrayList<>(), 2, "N");
    }
}
