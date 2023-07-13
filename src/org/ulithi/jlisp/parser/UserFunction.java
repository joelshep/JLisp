package org.ulithi.jlisp.parser;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the data structure which represents user-defined Lisp functions.
 */
class UserFunction {

	protected String name;
	protected List<String> formals;
	protected TreeNode body;

	/**
	 * Creates a user-defined function with the specified name, list of formal parameters,
	 * and body.
	 *
	 * @todo: allow the body to be a non-list
	 *
	 * @param n The name of the function
	 * @param f The list of formals - can be ()
	 * @param b The body of the function
	 */
	public UserFunction(final String n, final TreeNode f, final TreeNode b) {
		name = n;
		if ((!f.isList() && !f.toString().matches("NIL") ) || ( !b.isList() && !b.toString().matches("NIL"))) {
			throw new ParseException("Invalid function parameters or body.\n" + f + "\n" + b);
		}

		final String formalString = f.toString();
		formals = splitParamList(formalString);
		body = b;
	}

	/**
	 * This carries out the basic evaluation of a custom function by
	 * invoking the body with the passed actual parameters.
	 *
	 * @param actuals The list (possible NIL) of actual parameters
	 * @return The result of evaluating the body
	 */
	protected TreeNode evaluate(final TreeNode actuals) throws EvaluationException {
		final Map<String, TreeNode> bindings = bind(actuals);
		// Environment.vars.putAll(bindings);
		// Iterator it = bindings.entrySet().iterator();
		// int i = 0;
		// TreeNode tmp = TreeNode.create(body.tokens);
		// while ( it.hasNext() ){
		// 	Map.Entry pair = (Map.Entry) it.next();
		// 	tmp.replace(formals.get(i), pair.getValue().toString());
		// 	it.remove();
		// 	i++;
		// }
		return body.evaluate(true, bindings);
	}

	/**
	 * This is a private helper function to create a vector of parameters
	 * from a string. It also checks for distinct parameters and legal
	 * parameter names.
	 *
	 * @param s The string of parameters
	 * @return A string vector of parameter names
	 */
	private static List<String> splitParamList(final String s) {
		String[] chunks = s.substring(1, s.length()-1).split("\\s");
		List<String> rtn = new ArrayList<>();

		for (int i = 0; i < chunks.length; i++) {
			if (chunks[i].matches(Patterns.VALID_FUNCTION_NAME)) {
				if (!rtn.contains(chunks[i])) {
					rtn.add(chunks[i]);
				} else {
					throw new ParseException("Error! Formal parameter names must be distinct.");
				}
			} else {
				throw new ParseException("Error! Invalid parameter name: " + chunks[i]);
			}
		}
		return rtn;
	}

	/**
	 * Creates a hashtable representing the bound values of
	 * formal parameters to actual parameters.
	 * <p>
	 * Also provides some checking to make sure the number of
	 * actuals matches the number of formals.
	 *
	 * @param s The TreeNode object representing the actual paramters
	 * @return A Hashtable of bindings
	 */
	private Map<String, TreeNode> bind(final TreeNode s) throws EvaluationException {
		if (! s.isList() && !s.toString().matches("NIL")) {
			throw new EvaluationException("Error! Invalid parameters to function: " + name);
		}

		Map<String, TreeNode> env = new HashMap<>();

		if (!s.isList()) {
			return env;
		}

		SExpression tmp = new SExpression(s);
		int i;
		for (i = 0; i < formals.size(); i++) {
			env.put(formals.get(i), tmp.address.evaluate());
			try {
				tmp = new SExpression(tmp.dataTokens);
			} catch (Exception e) {
				break;
			}
		}

		if (i < formals.size() - 1) {
			throw new EvaluationException("Error! Too few arguments for: " + name);
		} else if (!tmp.data.evaluate().toString().matches("NIL")) {
			throw new EvaluationException("Error! Too many arguments for: " + name);
		}

		return env;
	}
}
