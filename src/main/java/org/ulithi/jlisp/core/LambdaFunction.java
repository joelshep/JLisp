package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.primitive.Eval;

import java.util.Map;

import static org.ulithi.jlisp.core.FunctionUtils.bindFormals;
import static org.ulithi.jlisp.core.FunctionUtils.parseFormals;

/**
 * Represents a LISP Lambda function: i.e. a function created by the {@code LAMBDA} function.
 * Lambda functions differ from other LISP functions in that they are not bound to a name/symbol
 * in the runtime environment.
 */
public class LambdaFunction implements Function {

    /** The formal parameters for this function. **/
    private final java.util.List<String> formals;

    /** The parsed source code of the function implementation. **/
    private final SExpression body;

    /**
     * Creates a lambda function with the specified formal parameters, and body.
     *
     * @param formals A list of formal parameters to the function. It may be an empty list, but
     *                otherwise must be a list of valid symbol names.
     * @param body The parsed implementation of the function.
     */
    public LambdaFunction(final SExpression formals, final SExpression body) {
        if (!formals.isList()) {
            throw new EvaluationException("Formal parameters to a function must be a list");
        }

        if (!body.isList()) {
            throw new EvaluationException("Function body must be a list");
        }

        this.formals = parseFormals(formals);
        this.body = body;
    }

    /**
     * Not implemented in {@link LambdaFunction}. A {@code LambdaFunction} invocation requires
     * arguments and a reference to the current {@link Environment}.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public SExpression apply(final SExpression sexp) {
        throw new EvaluationException("Not implemented in LambdaFunction");
    }

    /**
     * Updates the given {@link Environment} with formals bound to the arguments in the given
     * {@link SExpression}, and returns the body of this lambda function for evaluation.
     *
     * @param sexp An {@link SExpression} representing the arguments to this {@link Function}.
     * @param environment Reference to the current runtime {@code Environment}.
     * @return The body of this lambda function, to be evaluated against the updated environment.
     */
    @Override
    public SExpression apply(final SExpression sexp, final Environment environment, final Eval eval) {
        final Map<String, SExpression> locals = bindFormals(sexp, formals);

        for (final Map.Entry<String, SExpression> entry : locals.entrySet()) {
            environment.addBinding(entry.getKey(), entry.getValue());
        }

        return eval.eval(body);
    }

    /**
     * Indicates that this {@code LambdaFunction} needs its {@code apply()} method to be invoked
     * with the current runtime environment.
     * @return True.
     */
    @Override
    public boolean isReentrant() { return true; }
}
