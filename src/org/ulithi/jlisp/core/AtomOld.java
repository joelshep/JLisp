package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.ParseException;
import org.ulithi.jlisp.mem.TreeNode;
import org.ulithi.jlisp.parser.Grammar;

import java.util.Collections;
import java.util.Map;

/**
 * An {@link AtomOld} is an element of a LISP program which is either a symbol, an alphanumeric literal
 * or strictly numeric. See the {@link Grammar} class to validate string literals against valid
 * syntactic patterns.
 */
public final class AtomOld extends TreeNode {

	/**
	 * Constant singleton {@link AtomOld} representing the truth value.
	 */
	public static final AtomOld T = new AtomOld(true);

	/**
	 * Constant singleton {@link AtomOld} representing the NIL value.
	 */
	public static final AtomOld NIL = new AtomOld(false);

	/**
	 * The string representation of the value represented by this Atom.
	 */
	private final String literalString;

	/**
	 * Constructs an {@link AtomOld} from an alphanumeric string as a literal.
	 *
	 * @param s A literal string value for the atom.
	 * @throws ParseException If the string is not purely alphanumeric.
	 */
	public AtomOld(final String s) throws ParseException {
		if (!s.matches(Grammar.ALPHA_LITERAL) && !s.matches(Grammar.NUMERIC_LITERAL)) {
			throw new ParseException("Unable to parse string as valid atom");
		}

		literalString = s;
		tokens = Collections.singletonList(literalString);
	}

	/**
	 * Constructs a boolean atom as "T" or "NIL" based on the given value.
	 *
	 * @param b A boolean value.
	 */
	public AtomOld(final boolean b) {
		literalString = b ? Grammar.T : Grammar.NIL;
		tokens = Collections.singletonList(literalString);
	}

	/**
	 * Constructs a numeric {@link AtomOld} from an integer primitive.
	 *
	 * @param i An integer.
	 */
	public AtomOld(final int i) {
		literalString = Integer.toString(i);
		tokens = Collections.singletonList(literalString);
	}

	/**
	 * Indicates if this Atom -- as a TreeNode -- represents a list (it doesn't).
	 * @return False.
	 */
	@Override
	public boolean isList() { return false; }

	/**
	 * Returns the atom itself, and if it is representing a variable,
	 * then return the value of that variable.
	 *
	 * @return The atom or the variable value of it if applicable
	 */
	@Override
	public TreeNode evaluate() {
		if ( Environment.varIsDefined(literalString) ){
			return Environment.getVarValue(literalString);
		} else {
			return this;
		}
	}

	/**
	 * This calls the base evaluate function but supports calls that come with extra parameters.
	 *
	 * @param env A Hashtable of bound variables. Not used.
	 */
	@Override
	public TreeNode evaluate(final Map<String, TreeNode> env) {
		return evaluate();
	}

	/**
	 * This calls the base evaluate function but supports calls that come with extra parameters.
	 *
	 * @param env A Hashtable of bound variables. Not used.
	 */
	@Override
	public TreeNode evaluate(boolean flag, final Map<String, TreeNode> env) {
		return evaluate();
	}

	/**
	 * This calls the base evaluate function but supports calls that come with extra parameters.
	 *
	 * @param flag Ignored? TODO
	 */
	@Override
	public TreeNode evaluate(boolean flag) {
		return evaluate();
	}

	/**
	 * This simply returns the atom literal. It accounts for redundantly positive
	 * numbers - that is - numbers which are preceded by '+' and can simply be
	 * expressed without it. That prevents Integer.parseInt() from throwing format
	 * errors.
	 *
	 * @return The string version of the literal atom
	 */
	@Override
	public String toString() {
		if (literalString.matches(Grammar.NUMERIC_LITERAL)) {
			return literalString.replaceAll("\\A\\+", "");
		} else {
			return literalString;
		}
	}
}
