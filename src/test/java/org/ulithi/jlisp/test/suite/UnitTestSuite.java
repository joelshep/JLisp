package org.ulithi.jlisp.test.suite;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;
import org.ulithi.jlisp.test.core.EnvironmentTestCase;
import org.ulithi.jlisp.test.core.ListTestCase;
import org.ulithi.jlisp.test.core.SExpressionTestCase;
import org.ulithi.jlisp.test.core.AtomTestCase;
import org.ulithi.jlisp.test.main.InterpreterTestCase;
import org.ulithi.jlisp.test.mem.CellTestCase;
import org.ulithi.jlisp.test.mem.NilReferenceTestCase;
import org.ulithi.jlisp.test.mem.PTreeTestCase;
import org.ulithi.jlisp.test.parser.GrammarTestCase;
import org.ulithi.jlisp.test.parser.LexerTestCase;
import org.ulithi.jlisp.test.parser.ParserTestCase;
import org.ulithi.jlisp.test.primitive.CollectionsTestCase;
import org.ulithi.jlisp.test.primitive.UtilTestCase;
import org.ulithi.jlisp.test.primitive.LangTestCase;
import org.ulithi.jlisp.test.primitive.LogicTestCase;
import org.ulithi.jlisp.test.primitive.MathTestCase;
import org.ulithi.jlisp.test.primitive.PredicateTestCase;
import org.ulithi.jlisp.test.reference.ReferenceTestCase;
import org.ulithi.jlisp.test.utils.StringUtilsTestCase;

/**
 * Junit test suite for JLisp unit tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AtomTestCase.class,
        CellTestCase.class,
        CollectionsTestCase.class,
        EnvironmentTestCase.class,
        GrammarTestCase.class,
        InterpreterTestCase.class,
        LangTestCase.class,
        LexerTestCase.class,
        ListTestCase.class,
        LogicTestCase.class,
        MathTestCase.class,
        NilReferenceTestCase.class,
        ParserTestCase.class,
        PredicateTestCase.class,
        PTreeTestCase.class,
        SExpressionTestCase.class,
        ReferenceTestCase.class,
        StringUtilsTestCase.class,
        UtilTestCase.class,
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
