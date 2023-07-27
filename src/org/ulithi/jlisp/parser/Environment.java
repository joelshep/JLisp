package org.ulithi.jlisp.parser;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.UndefinedSymbolException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This houses the so-called "d-list" of the program. It manages
 * the binding of functions and variables within the context of the
 * running Lisp program.
 */
public final class Environment{
	public static Map<String, UserFunction> funcs = new HashMap<>();
	public static Map<String, TreeNode> vars = new HashMap<>();

	/**
	 * Executes a requested function with the actual parameters
	 *
	 * @param name The name of the function
	 * @param params An SExpression or Atom to be used as actual parameter
	 *
	 * @return The TreeNode (S-Expression or Atom) which is the result of evaluation
	 *
	 * @throws Exception If the requested function is undefined
	 *
	 */
	public static TreeNode executeFunction(final String name, final TreeNode params) throws EvaluationException {
		if ( !funcs.containsKey(name) ){
			throw new EvaluationException("Error! Undefined function: " + name);
		}

		UserFunction f = funcs.get(name);
		return f.evaluate(params);
	}

	/**
	 * This defines a function in the "d-list"
	 *
	 * @param name The string name of the function
	 * @param params The formal parameter list of the function
	 * @param body The literal or SExpression representing the body of the function
	 *
	 * @throws Exception If the function definition is illegal
	 *
	 */
	public static void registerFunction(final String name, final TreeNode params, final TreeNode body) {
		UserFunction f = new UserFunction(name, params, body);
		funcs.put(name, f);
	}

	/**
	 * Detects if a function is in the current scope
	 *
	 * @param name The string name of the function
	 *
	 * @return True if the function is defined. False if not.
	 *
	 */
	public static boolean functionIsDefined(final String name) {
		return funcs.containsKey(name);
	}

	/**
	 * Sends the variable hashtable stringified to stdout
	 */
	public static void print() {
		System.out.println(vars.toString());
	}

	/**
	 * Returns the string version of the variable hashtable.
	 *
	 * @return The stringified hashtable of bound variables
	 *
	 * @deprecated Only for early debugging
	 *
	 */
	public static String stringify() {
		return vars.toString();
	}

	/**
	 * Detects if a variable is defined in the current scope
	 *
	 * @param name The name of the requested variable.
	 *
	 * @return True if the variable is bound. False if not.
	 */
	public static boolean varIsDefined(final String name) {
		return vars.containsKey(name);
	}

	/**
	 * Used to remove variables from the environment
	 */
	public static void unbindAll(final HashMap<String, TreeNode> tbl) {
		Iterator<Map.Entry<String, TreeNode>> it = tbl.entrySet().iterator();

	    while (it.hasNext()) {
	        Map.Entry<String, TreeNode> pairs =it.next();
	        if ( vars.get(pairs.getKey()) == pairs.getValue() ){
	        	vars.remove(pairs.getKey());
	        }
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}

	/**
	 * Unbinds a specific variable from the envrionment
	 * @param name Name of the variable.
	 */
	public static void unbind(final String name) {
		vars.remove(name);
	}

	/**
	 * Finds a variable and returns its value if it exists.
	 *
	 * @param name	The name of the variable
	 * @return The TreeNode value of the variable
	 * @throws Exception If the variable is not bound
	 */
	public static TreeNode getVarValue(final String name) throws UndefinedSymbolException {
		if ( vars.containsKey(name) ) {
			return vars.get(name);
		}

		throw new UndefinedSymbolException("Error! No such variable: " + name);
	}

	/**
	 * This substitutes in new variable bindings to the current environment
	 *
	 * @param newVars A Hashtable of the new variable bindings
	 */
	public static void mergeVars(final Map<String, TreeNode> newVars) {
		Iterator<Map.Entry<String, TreeNode>> it = newVars.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, TreeNode> pairs = it.next();
			if ( vars.containsKey(pairs.getKey()) ){ // Do not let it store multiple things in one bucket
				vars.remove(pairs.getKey());
			}
			vars.put( pairs.getKey(), pairs.getValue() );
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}

	/**
	 * Returns a copy of the current environment bindings. Uses a
	 * copy to avoid accidental corruption.
	 *
	 * @return A copy of the current environment bindings
	 */
	public static Map<String, TreeNode> getVarTable() {
		return new HashMap<>(vars);
	}

	/**
	 * This takes a table of bindings and swaps it into the current
	 * variable table.
	 *
	 * @param v The new Hashtable of variable bindings
	 */
	public static void setVars(final Map<String, TreeNode> v) {
		vars = new HashMap<>(v);
	}
}
