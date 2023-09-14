package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.SExpression;

/**
 * A {@link Function} is the core unit of execution in LISP. A function has a programmatic name,
 * may have alternate programmatic names ("synonyms"), and an implementation which is evaluated
 * by invoking the {@code apply} method. Functions that evaluate their own arguments are
 * "special" functions: most functions are not special and their arguments are recursively
 * evaluated before the function itself is invoked.
 */
public interface Function extends Bindable {

    /** Single instance of an empty {@code String} array. */
    String[] EMPTY_SYNONYMS = new String[]{ };

    /**
     * Returns the programmatic name for this {@link Function}.
     * @return The programmatic name for this {@code Function}.
     */
    String name();

    /**
     * Returns synonyms for this {@link Function}: alternate programmatic names. Most
     * functions do not have synonyms.
     *
     * @return An array of alternate programmatic names for this {@code Function}, or an
     *         empty array if there are none.
     */
    default String[] synonyms() { return EMPTY_SYNONYMS; }

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
    SExpression apply(SExpression sexp);

    /**
     * Indicates if this {@link Function} is a LISP "special" function, meaning that its arguments
     * should not be evaluated before the function is invoked, but passed in as-is to the function
     * to handle as needed.
     *
     * @return True if this function will process its own arguments, false otherwise.
     */
    default boolean isSpecial() { return false; }
}
