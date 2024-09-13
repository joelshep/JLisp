package org.ulithi.jlisp.parser;

import org.ulithi.jlisp.commons.CollectionUtils;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.exception.ParseException;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.PTree;
import org.ulithi.jlisp.mem.Ref;

import java.util.List;
import java.util.Stack;

import static org.ulithi.jlisp.mem.NilReference.NIL;

/**
 * Transforms an ordered list of tokens generated by the {@link Lexer} into a {@link PTree}, which
 * can then be evaluated as a LISP expression.
 */
public class Parser {

    /**
     * Parses and construct parse trees for the LISP statements in the given list of tokens. Parsed
     * expressions are appended to this parser's expressions list.
     *
     * @param tokens An ordered list of LISP language tokens produced by lexical analysis of one
     *               or more LISP expressions.
     */
    public PTree parse(final List<String> tokens) {
        if (CollectionUtils.isEmpty(tokens)) {
            return new PTree();
        }

        if (tokens.size() == 1) {
            final Ref ref = parseToken(tokens.get(0));
            return new PTree(Cell.createStorage(ref));
        }

        return parseTokens(tokens);
    }

    private static PTree parseTokens(final List<String> tokens) {
        final Stack<PTree> stack = new Stack<>();
        PTree pTree = new PTree();
        boolean sublist = false;
        int depth = 0;
        boolean inQuote = false;
        int quoteDepth = 0;

        for (final String token: tokens) {
            if (token.equals(Grammar.QUOTE) && !inQuote) {
                inQuote = true;
                quoteDepth = depth;
                sublist = true;
                pTree.add(Cell.create(parseToken("QUOTE")));
            } else if (token.equals(Grammar.LPAREN)) {
                if (sublist) {
                    stack.push(pTree);
                    pTree = new PTree();
                }
                sublist = true;
                depth++;
            } else if (token.equals(Grammar.RPAREN)) {
                if (!stack.empty()) {
                    final PTree newOne = pTree;
                    pTree = stack.pop();
                    pTree.addList(newOne.root());
                }
                depth--;
                if (quoteDepth == depth) { inQuote = false; }
            } else {
                pTree.add(Cell.create(parseToken(token)));
                if (quoteDepth == depth) { inQuote = false; }
            }
        }

        if (depth != 0) {
            throw new ParseException("Mismatched parentheses");
        }

        return pTree;
    }

    /**
     * Parses a single atomic token and returns the appropriate {@link Ref reference}.
     * @param token The token to be parsed.
     * @return A {@link Ref}: either the special {@code NIL} {@code Ref} or a numeric, string or
     *         symbolic {@link Atom} corresponding to the token, depending on the detected data type
     *         of the token.
     */
    private static Ref parseToken(final String token) {
        if (Grammar.isNumeric(token)) { return Atom.create(Integer.parseInt(token)); }

        if ("NIL".equals(token)) { return NIL; }

        // TODO Symbols, properly.
        if (token.equals("+") || token.equals("*")) {
            return Atom.createSymbol(token);
        }

        return Atom.create(token);
    }
}
