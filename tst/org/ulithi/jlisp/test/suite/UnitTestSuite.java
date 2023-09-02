package org.ulithi.jlisp.test.suite;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;
import org.ulithi.jlisp.test.core.ListTestCase;
import org.ulithi.jlisp.test.core.SExpressionTestCase;
import org.ulithi.jlisp.test.lexer.LexerTestCase;
import org.ulithi.jlisp.test.core.AtomTestCase;
import org.ulithi.jlisp.test.mem.CellTestCase;
import org.ulithi.jlisp.test.parser.GrammarTestCase;
import org.ulithi.jlisp.test.parser.ParserTestCase;
import org.ulithi.jlisp.test.parser.PrimitivesTestCase;
import org.ulithi.jlisp.test.primitive.CARTestCase;
import org.ulithi.jlisp.test.primitive.CDRTestCase;
import org.ulithi.jlisp.test.primitive.CONSTestCase;
import org.ulithi.jlisp.test.primitive.EvalTestCase;
import org.ulithi.jlisp.test.primitive.LENGTHTestCase;
import org.ulithi.jlisp.test.primitive.LogicTestCase;
import org.ulithi.jlisp.test.primitive.QUOTETestCase;
import org.ulithi.jlisp.test.smoke.SmokeTestCase;
import org.ulithi.jlisp.test.utils.StringUtilsTestCase;

/**
 * Junit test suite for JLisp unit tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AtomTestCase.class,
        CARTestCase.class,
        CDRTestCase.class,
        CellTestCase.class,
        CONSTestCase.class,
        EvalTestCase.class,
        GrammarTestCase.class,
        LENGTHTestCase.class,
        LexerTestCase.class,
        ListTestCase.class,
        LogicTestCase.class,
        ParserTestCase.class,
        PrimitivesTestCase.class,
        QUOTETestCase.class,
        SExpressionTestCase.class,
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
