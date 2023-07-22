package org.ulithi.jlisp.parser;

import org.ulithi.jlisp.exception.ParseException;

import java.util.Collections;
import java.util.Map;

/**
 * An {@link Atom} is an element of a LISP program which is either a symbol, an alphanumeric literal
 * or strictly numeric. See the {@link Patterns} class to validate string literals against valid
 * syntactic patterns.
 */
class Atom extends TreeNode {

	/**
	 * Constant singleton {@link Atom} representing the truth value.
	 */
	public static final Atom T = new Atom(true);

	/**
	 * Constant singleton {@link Atom} representing the NIL value.
	 */
	public static final Atom NIL = new Atom(false);

	/**
	 * The string representation of the value represented by this Atom.
	 */
	private final String literalString;

	/**
	 * Constructs an {@link Atom} from an alphanumeric string as a literal.
	 *
	 * @param s A literal string value for the atom.
	 * @throws ParseException If the string is not purely alphanumeric.
	 */
	public Atom(final String s) throws ParseException {
		if (!s.matches(Grammar.LITERAL) && !s.matches(Grammar.NUMERIC_LITERAL)) {
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
	public Atom(final boolean b) {
		literalString = b ? Grammar.T : Grammar.NIL;
		tokens = Collections.singletonList(literalString);
	}

	/**
	 * Constructs a numeric {@link Atom} from an integer primitive.
	 *
	 * @param i An integer.
	 */
	public Atom (final int i) {
		literalString = Integer.toString(i);
		tokens = Collections.singletonList(literalString);
	}

	/**
	 * Indicates if this Atom -- as a TreeNode -- represents a list (it doesn't).
	 * @return False.
	 */
	@Override
	protected boolean isList() { return false; }

	/**
	 * Returns the atom itself, and if it is representing a variable,
	 * then return the value of that variable.
	 *
	 * @return The atom or the variable value of it if applicable
	 */
	@Override
	protected TreeNode evaluate() {
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
	protected TreeNode evaluate(final Map<String, TreeNode> env) {
		return evaluate();
	}

	/**
	 * This calls the base evaluate function but supports calls that come with extra parameters.
	 *
	 * @param env A Hashtable of bound variables. Not used.
	 */
	@Override
	protected TreeNode evaluate(boolean flag, final Map<String, TreeNode> env) {
		return evaluate();
	}

	/**
	 * This calls the base evaluate function but supports calls that come with extra parameters.
	 *
	 * @param flag Ignored? TODO
	 */
	@Override
	protected TreeNode evaluate(boolean flag) {
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
