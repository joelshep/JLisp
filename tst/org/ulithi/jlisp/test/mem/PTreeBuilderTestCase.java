package org.ulithi.jlisp.test.mem;

import org.junit.Test;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.PTree;

import java.util.Arrays;
import java.util.List;

import static org.ulithi.jlisp.parser.Symbols.*;

/**
 * At this point, these aren't really tests: these are just me trying to work out how
 * to build parse trees from tokens.
 */
public class PTreeBuilderTestCase {

    /**
     * Makes a PTree from a simple list of tokens.
     */
    @Test
    public void buildSymbolList() {
        List<String> tokens = Arrays.asList("(", "+", "1", "2", "4", ")");
        PTree ptree = null;
        PTree.Builder builder = new PTree.Builder();

        for (final String token: tokens) {
            if (token.equals(LPAREN)) {
                // Do nothing for now: we already have a builder. But when we get to an
                // inner list we'd have to go recursive.
            } else if (token.equals(RPAREN)) {
                ptree = builder.build();
                break;
            } else {
                builder.addCell(Cell.create(token));
            }
        }

        System.out.println(ptree);
    }
}
