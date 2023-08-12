package org.ulithi.jlisp.test.smoke;

import org.junit.Test;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.lexer.Lexer;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.parser.Parser;
import org.ulithi.jlisp.primitive.Eval;

import static org.junit.Assert.assertEquals;

/**
 * Unit test that drives several simple LISP expressions through the interpreter and validates the
 * results. This test is by no means exhaustive or a reference suite: it's simply to verify that
 * the lex-parse-evaluate process works together successfully.
 */
public class SmokeTestCase {
    @Test
    public void evaluateNumericLiteral() {
        final String expression = "43";
        final int result = evaluate(expression).toAtom().toI();
        assertEquals(43, result);
    }

    @Test
    public void evaluateSimpleMathExpression() {
        final String expression = "(+ 3 2)";
        final int result = evaluate(expression).toAtom().toI();
        assertEquals(5, result);
    }

    @Test
    public void evaluateCarCdrExpression() {
        final String expression = "(CDR (CAR ((1 2 3) (4 5 6)) ) )";
        final String result = evaluate(expression).toString();
        assertEquals("(2 3)", result);
    }

    private static SExpression evaluate(final String expression){
        final Lexer lexer = new Lexer(expression);
        final Parser p = new Parser();
        final PTree pTree = p.parse(lexer.getTokens());
        final Eval eval = new Eval();
        return eval.apply(pTree.root());
    }
}
