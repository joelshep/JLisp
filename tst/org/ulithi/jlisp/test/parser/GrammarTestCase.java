package org.ulithi.jlisp.test.parser;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ulithi.jlisp.parser.Grammar.NUMERIC_LITERAL;

/**
 * Test cases for regular expressions defined in {@link org.ulithi.jlisp.parser.Grammar}.
 */
public class GrammarTestCase {

    @Test
    public void testNumericLiteral() {
        assertTrue("43".matches(NUMERIC_LITERAL));
        assertTrue("-43".matches(NUMERIC_LITERAL));
        assertTrue("+43".matches(NUMERIC_LITERAL));
        assertFalse("+".matches(NUMERIC_LITERAL));
        assertFalse("+ 43".matches(NUMERIC_LITERAL));
        assertFalse("-".matches(NUMERIC_LITERAL));
        assertFalse("- 43".matches(NUMERIC_LITERAL));
    }
}
