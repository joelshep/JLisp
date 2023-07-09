package parser;

import java.util.ArrayList;
import java.util.Map;

/**
 * An {@link Atom} is an element of a LISP program which is either an alphanumeric literal or
 * strictly numeric. See the {@link Patterns} class to validate string literals
 * against valid syntactic patterns.
 */
class Atom extends TreeNode {
	/**
	 * The string representation of the token represented by this Atom.
	 */
	private final String literalString;

	/**
	 * Indicates if this Atom -- as a TreeNode -- represents a list (it doesn't).
	 * @return False.
	 */
	protected boolean isList() { return false; }

	/**
	 * Constructs an {@link Atom} from an alphanumeric string as a literal.
	 *
	 * @param s A literal string value for the atom.
	 * @throws Exception If the string is not purely alphanumeric.
	 */
	public Atom(final String s) throws Exception {
		if ( ! s.matches(Patterns.LITERAL) && ! s.matches(Patterns.NUMERIC_ATOM) ){
			throw new Exception("Error!");
		}

		literalString = s;
		tokens = new ArrayList<>(1);
		tokens.add(literalString);
	}

	/**
	 * Constructs a boolean atom as "T" or "NIL" based on the given value.
	 *
	 * @param b A boolean value.
	 */
	public Atom(final boolean b) {
		literalString = b ? "T" : "NIL";
		tokens = new ArrayList<>(1);
		tokens.add(literalString);
	}

	/**
	 * Constructs a numeric {@link Atom} from an integer primitive.
	 *
	 * @param i An integer.
	 */
	public Atom (final int i) {
		literalString = Integer.toString(i);
		tokens = new ArrayList<>(1);
		tokens.add(literalString);
	}

	/**
	 * Returns the atom itself, and if it is representing a variable,
	 * then return the value of that variable.
	 *
	 * @return The atom or the variable value of it if applicable
	 * @throws Exception Required by parent class.
	 */
	@Override
	protected TreeNode evaluate() throws Exception {
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
	protected TreeNode evaluate(final Map<String, TreeNode> env) throws Exception {
		return evaluate();
	}

	/**
	 * This calls the base evaluate function but supports calls that come with extra parameters.
	 *
	 * @param env A Hashtable of bound variables. Not used.
	 */
	@Override
	protected TreeNode evaluate(boolean flag, final Map<String, TreeNode> env) throws Exception {
		return evaluate();
	}

	/**
	 * This calls the base evaluate function but supports calls that come with extra parameters.
	 *
	 * @param flag Ignored? TODO
	 */
	@Override
	protected TreeNode evaluate(boolean flag) throws Exception {
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
		if (literalString.matches(Patterns.NUMERIC_ATOM)) {
			return literalString.replaceAll("\\A\\+", "");
		} else {
			return literalString;
		}
	}

}
