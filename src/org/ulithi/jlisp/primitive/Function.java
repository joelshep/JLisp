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

    /**
     * Indicates if this {@link Function} is a LISP "special" function, meaning that its arguments
     * should not be evaluated before the function is invoked, but passed in as-is to the function
     * to handle as needed.
     *
     * @return True if this function will process its own arguments, false otherwise.
     */
    default boolean isSpecial() { return false; }
}
