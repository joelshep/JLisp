package org.ulithi.jlisp.test.suite;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;
import org.ulithi.jlisp.test.smoke.SmokeTestCase;
import org.ulithi.jlisp.test.utils.StringUtilsTestCase;

/**
 * Junit test suite for JLisp unit tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        SmokeTestCase.class,
        StringUtilsTestCase.class
})
public class UnitTestSuite {
    public static void main(final String[] args) {
        Result result = JUnitCore.runClasses(UnitTestSuite.class);
        printResult(result);
    }

    private static void printResult(final Result result) {
        final int succeedCount = result.getRunCount() - result.getFailureCount();

        System.out.println("Unit Test Suite Run " + (result.wasSuccessful()? "SUCCESSFUL" : "FAILED"));
        System.out.println("Ran " + result.getRunCount() + " tests: " +
                           succeedCount + " succeeded, " +
                           result.getFailureCount() + " failed " +
                           "(" + result.getRunTime() + " ms)");

        if (result.getIgnoreCount() > 0) {
            System.out.println("  " + "Ignored " + result.getIgnoreCount() + " test(s)");
        }

        if (!result.wasSuccessful()) {
            System.out.println("\nFAILURES:");
            for (final Failure failure: result.getFailures()) {
                System.out.println(failure.getTestHeader() + ": " + failure.getMessage() + "\n" + failure.getTrace());
            }
        }
    }
}
