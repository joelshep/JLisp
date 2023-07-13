package org.ulithi.jlisp.parser;

import org.ulithi.jlisp.exception.JLispRuntimeException;

/**
 * A {@link Primitive} is a function built into the LISP interpreter.
 */
public interface Primitive {
    /**
     * Applies this primitive function to the given {@link SExpression} and returns the result
     * as another {@link SExpression}.
     * @param sexpr The {@link SExpression} to apply this function to.
     * @return The result of the evaluation.
     * @throws JLispRuntimeException if evaluation fails at runtime.
     */
    TreeNode evaluate(SExpression sexpr) throws JLispRuntimeException;
}
