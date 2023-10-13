package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.parser.Grammar;
import org.ulithi.jlisp.primitive.Eval;
import org.ulithi.jlisp.primitive.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a user-defined function: e.g., a function created by the {@code DEFUN} procedure.
 */
public class UserFunction implements Function {

	/** The programmatic name of this function. **/
	private final String name;

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
		if (!formals.isList()) {
			throw new EvaluationException("Formal parameters to a function must be a list");
		}

		if (!body.isList()) {
			throw new EvaluationException("Function body must be a list");
		}

		if (!Grammar.isFunctionName(name)) {
			throw new EvaluationException("'" + name + "' is not a legal function name");
		}

		this.name = name;
		this.formals = parseFormals(formals);
		this.body = body;
	}

	/**
	 * Simple transforms the given SExpression representing a list of formal parameters, to a
	 * Java list of strings with those same formal parameters, preserving order.
	 *
	 * @param formals An SExpression representing a (possibly empty) list of formal function
	 *                parameters.
	 * @return An ordered list of the formals' names.
	 */
	private static List<String> parseFormals(final SExpression formals) {
		org.ulithi.jlisp.core.List it = formals.toList();

		if (it.isEmpty()) {
			return Collections.emptyList();
		}

		final List<String> params = new ArrayList<>(it.lengthAsInt());

		addFormal(params, it.car().toAtom().toS());

		while (!it.endp()) {
			it = it.cdr().toList();
			addFormal(params, it.car().toAtom().toS());
		}

		return params;
	}

	/**
	 * Validates the given formal name, and then appends it to the given list.
	 * @param params A partial list of formal parameters defined for this function.
	 * @param formal A formal parameter to add to the list.
	 */
	private static void addFormal(final List<String> params, final String formal) {
		if (!Grammar.isFunctionName(formal)) {
			throw new EvaluationException("'" + formal + "' is not a legal parameter name");
		}

		if (params.contains(formal)) {
			throw new EvaluationException("Duplicate parameter name: " + formal);
		}

		params.add(formal);
	}

	/**
	 * Binds the given function invocation arguments to this function's formal parameters and
	 * returns the bindings as a {@code Map}.
	 *
	 * @param args The function arguments list as prepared by {@code Eval}.
	 * @return A map of this function's formal parameter names to the arguments for this
	 *         specific invocation.
	 */
	private Map<String, SExpression> bindFormals(final SExpression args) throws EvaluationException {
		org.ulithi.jlisp.core.List it = args.toList();

		if (it.lengthAsInt() != formals.size()) {
			throw new EvaluationException("Expected " + formals.size() +
										  " arguments: got " + it.lengthAsInt());
		}

		if (it.isEmpty()) { return Collections.emptyMap(); }

		final Map<String, SExpression> context = new HashMap<>(it.lengthAsInt());

		int index = 0;

		context.put(formals.get(index), it.car());

		while (!it.endp()) {
			it = it.cdr().toList();
			index++;
			context.put(formals.get(index), it.car());
		}

		if (!(it.endp() && context.size() == formals.size()) ) {
			throw new EvaluationException("Count of arguments didn't match number of formals");
		}

		return context;
	}

	/** {@inheritDoc} */
	@Override
	public String name() {
		return this.name;
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
	 * {@link SExpression} list, and returns the body of this user function for evaluation.
	 *
	 * @param sexp An {@link SExpression} representing the arguments to this {@link Function}.
	 * @param environment Reference to the current runtime {@code Environment}.
	 * @return The body of this user function, to be evaluated against the updated environment.
	 */
	@Override
	public SExpression apply(final SExpression sexp, final Environment environment, final Eval eval) {
		final Map<String, SExpression> locals = bindFormals(sexp);

		for (final Map.Entry<String, SExpression> entry : locals.entrySet()) {
			environment.addBinding(entry.getKey(), entry.getValue());
		}

		return eval.apply(body);
	}

	/**
	 * Indicates that this {@code UserFunction} needs its {@code apply()} method to be invoked
	 * with the current runtime environment.
	 * @return True.
	 */
	@Override
	public boolean isReentrant() { return true; }
}
