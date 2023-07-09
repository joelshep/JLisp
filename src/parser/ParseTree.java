package parser;

import java.util.List;

/**
 * This files contains the main tree structure of the Lisp program.
 */
class ParseTree {
	private final TreeNode root;

	/**
	 * Uses the given vector to create the root. It calls the
	 * TreeNode factory constructor
	 *
	 * @param s A vector representing the outermost expression
	 * @throws Exception If node creation fails
	 */
	public ParseTree(final List<String> s) throws Exception {
		root = TreeNode.create(s);
	}

	/**
	 * Evaluates the root node and returns the result as a string
	 *
	 * @return The string of the executed statement
	 * @throws Exception If evaluation fails
	 */
	protected String evaluate() throws Exception {
		return root.evaluate().toString();
	}

	/**
	 * Stringifies and returns the parse tree as-is.
	 *
	 * @return String of the root node
	 */
	@Override
	public String toString() {
		return root.toString();
	}
}
