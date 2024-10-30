package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.primitive.Eval;

import java.util.List;
import java.util.Map;

import static org.ulithi.jlisp.core.FunctionUtils.bindFormals;
import static org.ulithi.jlisp.core.FunctionUtils.parseFormals;

/**
 * Represents a user-defined function: e.g., a function created by the {@code DEFUN} procedure.
 */
public class UserFunction extends BindableFunction {

	/** The formal parameters for this function. **/
	private final List<String> formals;

	/** The parsed source code of the function implementation. **/
	private final SExpression body;

	/**
	 * Creates a user-defined function with the specified name, formal parameters, and body.
	 *
	 * @param name The name of the function.
	 * @param formals A list of formal parameters to the function. It may be an empty list, but
	 *                otherwise must be a list of valid symbol names.
	 * @param body The parsed implementation of the function.
	 */
	public UserFunction(final String name, final SExpression formals, final SExpression body) {
        super(name);
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
	 * Not implemented in {@link UserFunction}. A {@code UserFunction} invocation requires both
	 * arguments and a reference to the current {@link Environment}.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public SExpression apply(final SExpression sexp) {
		throw new EvaluationException("Not implemented in UserFunction");
	}

	/**
	 * Updates the given {@link Environment} with formals bound to the arguments in the given
	 * {@link SExpression}, and returns the body of this user function for evaluation.
	 *
	 * @param sexp An {@link SExpression} representing the arguments to this {@link Function}.
	 * @param environment Reference to the current runtime {@code Environment}.
	 * @return The body of this user function, to be evaluated against the updated environment.
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
	 * Indicates that this {@code UserFunction} needs its {@code apply()} method to be invoked
	 * with the current runtime environment.
	 * @return True.
	 */
	@Override
	public boolean isReentrant() { return true; }
}
