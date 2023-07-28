package org.ulithi.jlisp.mem;

import org.ulithi.jlisp.core.SExpressionOld;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ulithi.jlisp.parser.Grammar.LPAREN;

/**
 * A {@link TreeNode} This is the central data structure for representing Atoms and S-Expressions.
 * It maintains a list tokens which make up the object, and exposes static methods for creating
 * {@link AtomOld Atoms} representing integer and boolean values.
 */
abstract public class TreeNode {
	/**
	 * Indicates if this {@link TreeNode} represents a List or an Atom.
	 * @return True if this {@code TreeNode} represents a List, and false if it represents
	 *         an Atom.
	 */
	public abstract boolean isList();

	/**
	 * An ordered list of tokens associated with this {@link TreeNode}.
	 */
	public List<String> tokens = new ArrayList<>();

	/**
	 * Creates and returns either an {@link SExpressionOld} or an {@link AtomOld} representing the
	 * given tokenized LISP program. {@code SExpression}s are identified by the first element of
	 * the given list matching an opening parenthesis ("(").
     *
	 * @param tokens A {@code List} of tokens parsed from a string representing a LISP program.
	 * @return A {@link TreeNode} that is the root node of the parse tree for the given program.
	 */
	public static TreeNode create(final List<String> tokens) throws ParseException {
		if (tokens == null || tokens.isEmpty()) {
			throw new ParseException("Tried to create a TreeNode with no data");
		}

		if (tokens.size() == 1) {
			return new AtomOld(tokens.get(0));
		}

		if (tokens.get(0).equals(LPAREN)) {
			return new SExpressionOld(tokens);
		}

		throw new ParseException("Tokens represent neither a list nor an atom");
	}

	/**
	 * Creates an {@link AtomOld} representing a boolean value.
	 *
	 * @param b A boolean value.
	 * @return An {@link AtomOld} (sliced to a {@link TreeNode}) representing the given boolean value.
	 */
	public static TreeNode create(final boolean b) {
		return new AtomOld(b);
	}

	/**
	 * Creates an {@link AtomOld} representing an integer value.
	 *
	 * @param i An integer value.
	 * @return An {@link AtomOld} (sliced to a {@link TreeNode}) representing the given integer value.
	 */
	public static TreeNode create(final int i) {
		return new AtomOld(i);
	}

	/**
	 * The evaluation of TreeNodes returns a new TreeNode.
	 *
	 * @return The result of evaluating the TreeNode
	 */
	public abstract TreeNode evaluate() throws EvaluationException;

	/**
	 * The evaluation of TreeNodes returns a new TreeNode.
	 *
	 * @param flag Whether or not to take numericals literally
	 * @return The result of evaluating the TreeNode
	 */
	public abstract TreeNode evaluate(boolean flag) throws EvaluationException;

	/**
	 * The evaluation of TreeNodes returns a new TreeNode.
     *
	 * @param flag Whether or not to take numericals literally
	 * @param env The scoped variables
	 * @return The result of evaluating the TreeNode
	 *
	 */
	public abstract TreeNode evaluate(boolean flag, Map<String, TreeNode> env) throws EvaluationException;

	/**
	 * The evaluation of TreeNodes returns a new TreeNode.
	 *
	 * @param env The scoped variables
	 * @return The result of evaluating the TreeNode
	 */
	public abstract TreeNode evaluate(Map<String, TreeNode> env) throws EvaluationException;
}
