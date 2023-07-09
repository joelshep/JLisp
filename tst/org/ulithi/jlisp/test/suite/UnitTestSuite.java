package org.ulithi.jlisp.test.suite;

import junit.framework.TestSuite;

/**
 * Junit test suite for JLisp unit tests.
 */
public class UnitTestSuite {
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        return suite;
    }

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
