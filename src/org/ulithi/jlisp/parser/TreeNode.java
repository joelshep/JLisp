package org.ulithi.jlisp.parser;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ulithi.jlisp.parser.Symbols.LPAREN;

/**
 * A {@link TreeNode} This is the central data structure for representing Atoms and S-Expressions. It maintains
 * the string vector of tokens which make up the object and also employs the factory pattern
 * to create new TreeNodes based on whether or not they are valid S-Expressions.
 * <p>
 * This class also implements the evaluate functions and the isList method.
 */
abstract public class TreeNode {
	protected abstract boolean isList();
	protected List<String> tokens = new ArrayList<>();

	/**
	 * Creates and returns either an {@link SExpression} or an {@link Atom} representing the
	 * given tokenized LISP program. {@code SExpression}s are identified by the first element of
	 * the given list matching an opening parenthesis ("(").
     *
	 * @param tokens A {@code List} of tokens parsed from a string representing a LISP program.
	 * @return A {@link TreeNode} that is the root node of the parse tree for the given program.
	 */
	static TreeNode create(final List<String> tokens) throws ParseException {
		if (tokens == null || tokens.isEmpty()) {
			throw new ParseException("Tried to create a TreeNode with no data");
		}

		if (tokens.get(0).equals(LPAREN)) {
			return new SExpression(tokens);
		}

		return new Atom(tokens.get(0));
	}

	/**
	 * Creates an {@link Atom} representing a boolean value.
	 *
	 * @param b A boolean value.
	 * @return An {@link Atom} (sliced to a {@link TreeNode}) representing the given boolean value.
	 */
	static TreeNode create(final boolean b) {
		return new Atom(b);
	}

	/**
	 * Creates an {@link Atom} representing an integer value.
	 *
	 * @param i An integer value.
	 * @return An {@link Atom} (sliced to a {@link TreeNode}) representing the given integer value.
	 */
	static TreeNode create(final int i) {
		return new Atom(i);
	}

	/**
	 * The evaluation of TreeNodes returns a new TreeNode.
	 *
	 * @return The result of evaluating the TreeNode
	 */
	abstract TreeNode evaluate() throws EvaluationException;

	/**
	 * The evaluation of TreeNodes returns a new TreeNode.
	 *
	 * @param flag Whether or not to take numericals literally
	 * @return The result of evaluating the TreeNode
	 */
	abstract TreeNode evaluate(boolean flag) throws EvaluationException;

	/**
	 * The evaluation of TreeNodes returns a new TreeNode.
     *
	 * @param flag Whether or not to take numericals literally
	 * @param env The scoped variables
	 * @return The result of evaluating the TreeNode
	 *
	 */
	abstract TreeNode evaluate(boolean flag, Map<String, TreeNode> env) throws EvaluationException;

	/**
	 * The evaluation of TreeNodes returns a new TreeNode.
	 *
	 * @param env The scoped variables
	 * @return The result of evaluating the TreeNode
	 */
	abstract TreeNode evaluate(Map<String, TreeNode> env) throws EvaluationException;
}
