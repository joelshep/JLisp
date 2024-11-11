package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.BindableFunction;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;

/**
 * Helper extension for functions like CAR and CDR that operate on a single list argument.
 */
public abstract class AbstractListFunction extends BindableFunction {

    /**
     * Constructs a new {@link AbstractListFunction} with the specified programmatic {@code name}.
     * @param name The programmatic name of the function: e.g., "CAR", "CDR", etc.
     */
    protected AbstractListFunction(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Validates {@code sexp} is a list with a single element which itself is a list, and then
     * invokes the superclass's {@code applyImpl} method to apply its operation to the single
     * list argument.
     */
    @Override
    public SExpression apply(final SExpression sexp) {
        final Args args = Args.create(sexp).expect(1);
        final List arg = args.wantList();
        return applyImpl(arg);
    }

    /**
     * Applies this function to the given {@code List}.
     *
     * @param list A {@code List} which is the single argument to the function.
     * @return The results of the evaluation.
     */
    protected abstract SExpression applyImpl(final List list);
}
