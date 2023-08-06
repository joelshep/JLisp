package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.SExpression;

/**
 * Implements the LISP {@code QUOTE} function. The {@code QUOTE} returns its arguments as-is, and is
 * therefore a "special" function. In modern LISP, the {@code '} token is shorthand for
 * {@code QUOTE}, but implementing it will require changes to the lexer, which I haven't done yet.
 */
public class QUOTE implements Function {
    /** {@inheritDoc} */
    @Override
    public SExpression apply(final SExpression sexp) {
        if (sexp.isAtom()) { return sexp.toAtom(); }
        return sexp.toList().car();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSpecial() { return true; }
}
