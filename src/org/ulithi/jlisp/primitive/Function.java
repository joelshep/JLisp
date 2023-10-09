package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Bindable;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.Environment;
import org.ulithi.jlisp.core.SExpression;

/**
 * A {@link Function} is the core unit of execution in LISP. A function has a programmatic name,
 * may have alternate programmatic names ("synonyms"), and an implementation which is evaluated
 * by invoking the {@code apply} method. Functions that evaluate their own arguments are
 * "special" functions: most functions are not special and their arguments are recursively
 * evaluated before the function itself is invoked.
 */
public interface Function extends Bindable {

    /**
     * Returns the programmatic name for this {@link Function}.
     * @return The programmatic name for this {@code Function}.
     */
    String name();

    /**
     * Returns synonyms that can be used to refer to this {@link Function}: alternate programmatic
     * names. Most functions do not have synonyms.
     * @return The synonyms, if any, for this {@code Function}.
     */
    default String[] synonyms() { return Binding.EMPTY_SYNONYMS; }

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
     * Applies this {@link Function} the given {@link SExpression} and returns the result as
     * a new {@link SExpression}. Additionally, the function may modify the given
     * {@link Environment} as a side effect. This is usually reserved for functions used to
     * define new language elements (see {@code needsEnv()}). The elements of the given
     * {@code sexp} is assumed to have been evaluated already: i.e., they are generally literals
     * or symbolic <em>names</em>. For example, if the original expression was
     * {@code (* (+ 2 3) (+ 3 4))} and this function was the {@code *} function, the given
     * {@code sexp} would be (6 12). {@code Functions} generally do not evaluate their arguments.
     *
     * @param sexp An {@link SExpression} representing the arguments to this {@link Function}.
     * @param environment Reference to the current runtime {@code Environment}.
     * @return The result of applying this {@code Function} to the given {@code SExpression}.
     */
    default SExpression apply(SExpression sexp, Environment environment) { return null; }

    /**
     * Indicates if this {@link Function} is a LISP "special" function, meaning that its arguments
     * should not be evaluated before the function is invoked, but passed in as-is to the function
     * to handle as needed.
     *
     * @return True if this function will process its own arguments, false otherwise.
     */
    default boolean isSpecial() { return false; }

    /**
     * Indicates if this {@link Function} is used to define another language element, such as a
     * variable or user-function. If so, the version of {@code apply} that includes an
     * {@code Environment} parameter should be used to invoke it.
     *
     * @return True if this function is used to define another language element, false otherwise.
     */
    default boolean needsEnv() { return false; }
}
