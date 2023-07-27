package org.ulithi.jlisp.parser;

import org.apache.commons.lang3.StringUtils;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.ParseException;
import org.ulithi.jlisp.exception.UndefinedSymbolException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an S-Expression (Symbolic Expression): a.k.a. a list of {@link AtomOld}s, {@code  Symbols}
 * or other {@link SExpression s-expressions}. An s-expression can also be empty. This class is able
 * to construct s-expressions from different structures, and implies the core evaluation routine
 * as well.
 */
class SExpression extends TreeNode {
	protected TreeNode address;
	protected TreeNode data;
	protected List<String> dataTokens;
	protected List<String> addressTokens;

	/**
	 * Constructs an {@link SExpression} from an ordered list of tokens.
	 *
	 * @param s An ordered list of expression tokens.
	 */
	public SExpression(final List<String> s) throws ParseException {
		consHelper(s);
	}

	/**
	 * @param t A {@link TreeNode} to be "cast" to an S-Expression.
	 */
	public SExpression(final TreeNode t) throws ParseException {
		consHelper(t.tokens);
	}

	/**
	 * This function takes two TreeNodes and puts one in the
	 * address field and the other in the data field and updates
	 * the tokens accordingly
	 *
	 * @param a The address-to-be TreeNode
	 * @param d The data-to-be TreeNode
	 */
	public SExpression(final TreeNode a, final TreeNode d) {
		address = a;
		data = d;
		dataTokens = d.tokens;
		addressTokens = a.tokens;
		tokens = new ArrayList<>();
		tokens.add(Grammar.LPAREN);
		tokens.addAll(a.tokens);
		tokens.add(Grammar.DOT);
		tokens.addAll(d.tokens);
		tokens.add(Grammar.RPAREN);
	}

	/**
	 * This is essentially a deep-copy constructor
	 *
	 * @param s The to-be-copied S-Expression
	 */
	public SExpression(final SExpression s) throws ParseException {
		data = TreeNode.create(s.dataTokens);
		address = TreeNode.create(s.addressTokens);
		dataTokens = new ArrayList<>(s.dataTokens);
		addressTokens = new ArrayList<>(s.addressTokens);
	}

	/**
	 * This is a magic function to take a string vector, make sure it is a
	 * suitable representation of an S-Expression, and calculate
	 * which parts are the address and which is the data.  It appropriately
	 * updates the tokens as well.
	 *
	 * @param s The string vector to fit into an S-Expression
	 */
	private void consHelper(final List<String> s) throws ParseException {
		// some sanity checking for now
		if (s.isEmpty() || !s.get(0).equals(Grammar.LPAREN)) {
			throw new ParseException("Error! Invalid S-Expression: " + s);
		}

		int i = 1;
		int dataStart = 3;
		if (s.get(i).equals(Grammar.LPAREN)) {
			int open = 1;
			while (open > 0 && i < s.size()) {
				i++;
				if (s.get(i).equals(Grammar.LPAREN)) {
					open++;
				} else if (s.get(i).equals(Grammar.RPAREN)) {
					open--;
				}
			}
			dataStart = i + 1;
		}

		i = dataStart > 3 ? org.ulithi.jlisp.commons.StringUtils.indexOf(s, dataStart, Grammar.DOT) : 2;
		addressTokens = new ArrayList<>(s.subList(1,i));
		address = TreeNode.create(addressTokens);
		dataTokens = new ArrayList<>(s.subList(i+1, s.size() - 1));
		data = TreeNode.create(dataTokens);
		tokens = new ArrayList<>();
		tokens.add(Grammar.LPAREN);
		tokens.addAll(addressTokens);
		tokens.add(Grammar.DOT);
		tokens.addAll(dataTokens);
		tokens.add(Grammar.RPAREN);
	}

	/**
	 * Determines if the S-Expression is part of a list by the fact that it is a list iff
	 * its data is NIL or the data of one of its 'sub-expressions' is NIL.
	 *
	 * @return True if the S-Expression is a list, false otherwise.
	 */
	@Override
	protected boolean isList() {
		return data.toString().matches(Grammar.NIL) || data.isList();
	}

	/**
	 * Outputs the SExpression data as a list using the prescribed
	 * notation.
	 *
	 * @return The String representing the list-notation of the S-Expression
	 */
	protected String toListString() throws Exception {
		return Grammar.LPAREN + StringUtils.join(toList(), Grammar.SPACE) + Grammar.RPAREN;
	}

	/**
	 * This function converts the string data to a vector suitable
	 * for conversion to list notation.
	 *
	 * @return The String vector of the S-Expression
	 * @throws Exception if the SExpression is not able to be converted
	 */
	private List<String> toList() throws Exception {
		if (!isList()) {
			throw new Exception("Error!");
		}

		final List<String> v = new ArrayList<>();

		SExpression tmp = this;

		while (tmp.isList()) {
			v.add(tmp.address.toString());
			try {
				tmp = new SExpression(tmp.dataTokens);
			} catch (final Exception e) {
				break;
			}
		}
		return v;
	}

	/**
	 * This is a connector function to the main evaluation procedure.
	 * It takes no arguments and just passes a default value to the main
	 * subroutine.
	 *
	 * @return The result of evaluation of the SExpression
	 */
	@Override
	protected TreeNode evaluate() {
		return this.evaluate(false);
	}

	/**
	 * This function allows invoking of the main evaluation routine
	 * without specifying the flag. It passes a default value and the
	 * given environment varables.
	 *
	 * @param env A Hashtable of the variables to be considered during evaluation
	 */
	@Override
	protected TreeNode evaluate(final Map<String, TreeNode> env) {
		return evaluate(false, env);
	}

	/**
	 * Provides an interface to accept both the flag and environment varaibles
	 * as arguments. This swaps in the current scope variables and when finished,
	 * 'pops' them back out.
	 *
	 * @param flag Whether or not to take numericals literally
	 * @param env Variable bindings to use
	 * @return The result of evaluation
	 */
	@Override
	protected TreeNode evaluate(boolean flag, final Map<String, TreeNode> env) {
		final Map<String, TreeNode> oldVars = Environment.getVarTable();
		Environment.mergeVars(env);
		TreeNode rtn = evaluate(flag);
		Environment.setVars(oldVars);
		return rtn;
	}

	/**
	 * This is the main evaluation function for an SExpression.
	 * It takes a flag that decides whether or not to take numerical
	 * items literally. That should only happen when they are arguments
	 * to a primitive or user-defined function.
	 * <p>
	 * This function uses reflection on the Primitives object to find the
	 * appropriate primitive function to run. It also first searches the
	 * defined functions and variables for bound values.  If none are set,
	 * it uses the string name of the function call to find the appropriate
	 * primitive function. Using reflection, that primitive is invoked with
	 * the 'data' component of the current S-Expression as an argument.  This
	 * is because in the operational semantics, the primitives operate on the
	 * CADR or CDR of the S-Expression. So we simplify here by just passing
	 * the CDR and the primitives do any further chomping.
	 *
	 * @param flag Whether or not to interpret numerics literally
	 * @return The TreeNode representation of the result
	 */
	@Override
	protected TreeNode evaluate(boolean flag) {
		String a = address.evaluate().toString();
		SExpression params;

		if (flag && a.matches(Grammar.NUMERIC_LITERAL)) {
			return address.evaluate();
		} else if (a.matches(Grammar.NIL)) {
			return AtomOld.NIL;
		} else if (a.matches(Grammar.T)) {
			return AtomOld.T;
		} else if (Environment.varIsDefined(a)) {
			return Environment.getVarValue(a);
		} else if (Environment.functionIsDefined(a)) {
			return Environment.executeFunction(a, TreeNode.create(dataTokens));
		} else if (a.matches("CAR") || a.matches("CDR")) {
			SExpression s;
			if (data.isList()) {
				s = new SExpression(dataTokens);
				s = new SExpression(s.address.evaluate().tokens);
				// s = new SExpression(s.evaluate().tokens);
			} else {
				s = new SExpression(dataTokens);
			}
			params = s;
		} else if (a.matches("DEFUN")) {
			return Primitives.DEFUN((SExpression) data);
		} else {
			params = (SExpression) data;
		}

		return invokePrimitive(a, params);
	}

	/**
	 * This function contains the reflection logic to call one of the
	 * functions of the Primitives class with the appropriate SExpression
	 * passed as data.  If the return value is boolean, it returns the
	 * appropriate atom representation.
	 *
	 * @param name The string name of the requested function
	 * @param sexpr The data to be used as primitive data
	 * @return The object returned by the primitive cast as a TreeNode (what it should be anyway)
	 */
	private TreeNode invokePrimitive(final String name, final SExpression sexpr) {
		Method m;

		try {
			m = Primitives.class.getDeclaredMethod(name, SExpression.class);
		} catch (Exception e) {
			throw new UndefinedSymbolException("Primitive function " + name + " undefined", e);
		}

		m.setAccessible(true);

		Object o;

		try {
			o = m.invoke(null, sexpr);
		} catch (Exception e) {
			throw new EvaluationException("Failure during evaluation  of " + name, e);
		}

		if (o.toString().matches("true")) {
			return AtomOld.T;
		} else if (o.toString().matches("false")) {
			return AtomOld.NIL;
		} else {
			return (TreeNode) o;
		}
	}

	/**
	 * Provides the basic toString functionality. It initially tries
	 * to print it as a list but if it cannot be converted to list notation
	 * it uses standard dot notation.
	 *
	 * @return The dot- or list-notation of the S-Expression
	 */
	@Override
	public String toString() {
		if (isList()) {
			try {
				return toListString();
			} catch (final Exception e) {
				return Grammar.LPAREN + address.toString() + " . " + data.toString() + Grammar.RPAREN;
			}
		} else {
			return Grammar.LPAREN + address.toString() + " . " + data.toString() + Grammar.RPAREN;
		}
	}
}
