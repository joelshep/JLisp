package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.primitive.Args;

/**
 * Abstract implementation of {@link BindableFunction} specifically for functions used to define
 * other language elements including user functions and macros.
 */
public abstract class DefiningFunction extends BindableFunction {
    /**
     * Constructs a new {@link BindableFunction} with the specified programmatic {@code name}.
     *
     * @param name The programmatic name of the function: e.g., "CAR", "PLUS", etc.
     */
    protected DefiningFunction(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     * Defining functions generally don't want their parameters evaluated.
     */
    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDefining() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public SExpression apply(final SExpression sexp) {
        throw new EvaluationException("Defining function invoked without environment reference");
    }

    /**
     * {@inheritDoc}
     */
    public SExpression apply(final SExpression sexp, final Environment env) {
        final Args args = Args.create(sexp).expectMinLength(3);

        final SExpression name = args.takeAtom();
        final SExpression formals = args.takeList();
        final SExpression definition = args.takeAny();

        define(name.toAtom().toS(), formals, definition, env);

        return name;
    }

    /**
     * Subclasses should override this method to define (create a representation of) the specified
     * function or macro.
     *
     * @param name The programmatic name of the function or macro: e.g., "CAR", "PLUS", etc.
     * @param formals A list of formal parameters to the function or macro. It may be an empty list,
     *                but otherwise must be a list of valid symbol names.
     * @param definition The form representing the function or macro body.
     * @param env Reference to the current runtime {@link Environment}.
     */
    protected abstract void define(String name, SExpression formals, SExpression definition, Environment env);
}
