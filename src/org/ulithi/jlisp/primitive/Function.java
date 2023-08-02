package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.SExpression;

/**
 * A {@link Function} is the core unit of execution in LISP.
 */
public interface Function {
    /**
     * Applies this {@link Function} the given {@link SExpression} and returns the result as
     * a new {@link SExpression}. The elements of the given {@code sexp} is assumed to have been
     * evaluated already: i.e., they are generally literals or symbolic <em>names</em>. For example,
     * if the original expression was {@code (* (+ 2 3) (+ 3 4))} and this function was the
     * {@code *} function, the given {@code sexp} would be (6 12). {@code Functions} generally
     * do not evaluate their arguments.
     *
     * @param sexp An {@link SExpression} representing the arguments to this {@link Function}.
     * @return The result of applying this {@code Function} to the given {@code SExpression}.
     */
    SExpression apply (SExpression sexp);
}
