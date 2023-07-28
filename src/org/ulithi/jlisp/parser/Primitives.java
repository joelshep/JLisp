package org.ulithi.jlisp.parser;

import org.ulithi.jlisp.core.Environment;
import org.ulithi.jlisp.core.SExpressionOld;
import org.ulithi.jlisp.exception.ParseException;
import org.ulithi.jlisp.core.AtomOld;
import org.ulithi.jlisp.mem.TreeNode;

import java.lang.Integer;
import java.lang.String;
import java.lang.reflect.Method;

import static org.ulithi.jlisp.parser.Grammar.*;

/**
 * Implements the primitive LISP functions supported by this interpreter.
 */

public final class Primitives {

	// public static enum Primitive{
	// 	ATOM,
	// 	CAR,
	// 	CDR,
	// 	COND,
	// 	CONS,
	// 	DEFUN
	// 	EQ,
	// 	GREATER,
	// 	INT,
	// 	LESS,
	// 	MINUS,
	// 	NIL,
	// 	NULL,
	// 	PLUS,
	// 	QUOTE,
	// 	QUOTIENT,
	// 	REMAINDER,
	// 	T,
	// 	TIMES,
	// }

	/**
	 * Returns an {@link AtomOld} representing the "true" value.
	 *
	 * @return The T Atom.
	 */
	public static TreeNode T() {
		return AtomOld.T;
	};

	/**
	 * Returns an {@link AtomOld} to represent the NIL (or false) value.
	 *
	 * @return The NIL Atom.
	 */
	public static TreeNode NIL() {
		return AtomOld.NIL;
	};

	/**
	 * returns the car of the given S-Expression as defined by the operational
	 * semantics.
	 *
	 * @param sexpr The argument S-Expression
	 * @return The address of the given S-Exp
	 */
	public static TreeNode CAR (final SExpressionOld sexpr) {
		return sexpr.address;
	}

	/**
	 * Returns the data of the given S-Expression
	 *
	 * @param sexpr An S-Expression
	 * @return The data of the S-Expression
	 */
	public static TreeNode CDR (final SExpressionOld sexpr) {
		return sexpr.data;
	}

	/**
	 * Carries out the CONS operation as defined by the Lisp operational semantics.
	 * It combines the CAR and CADR of the argument list into a single list. If
	 * the arguments fail for these operations, an Exception will be thrown in the
	 * SExpression constructor.
	 *
	 * @param sexpr The SExpression arguments in dot-notation
	 * @return CONS[ CAR[s], CADR[s] ] - the semantically defined CONS
	 */
	public static SExpressionOld CONS (final SExpressionOld sexpr ) {
		final SExpressionOld tmp = new SExpressionOld(sexpr.dataTokens);
		return new SExpressionOld(sexpr.address.evaluate(), tmp.address.evaluate());
	}

	/**
	 * Determines if an S-Expression is semantically an Atom
	 *
	 * @param sexpr The S-Expression in question
	 * @return T if it is an atom literal, NIL otherwise.
	 */
	public static TreeNode ATOM (final SExpressionOld sexpr) {
		return TreeNode.create(sexpr.address.evaluate().toString().matches(ALPHA_LITERAL));
	}

	/**
	 * Determines if the value of two S-Expressions are equal
	 *
	 * @param sexpr The paramenter S-Expression
	 * @return T if the two S-Expressions are equal, NIL otherwise.
	 */
	public static TreeNode EQ (final SExpressionOld sexpr) {
		return TreeNode.create(sexpr.address.evaluate(true).toString().matches(sexpr.data.evaluate(true).toString()));
	}

	/**
	 * Indicates if an S-Expression evaluates to NIL
	 *
	 * @param sexpr The S-Expression in question.
	 * @return T if the S-Expression evaluates to NIL, NIL otherwise.
	 */
	public static TreeNode NULL (final SExpressionOld sexpr) {
		return TreeNode.create(sexpr.data.evaluate().toString().matches(NIL));
	}

	/**
	 * Indicates if an S-Expression evaluates to an integer.
	 *
	 * @param sexpr An S-Expression
	 * @return T if the S-Expression evaluates to an integer, NIL otherwise.
	 */
	public static TreeNode INT (final SExpressionOld sexpr) throws Exception{
		return TreeNode.create(sexpr.address.evaluate(true).toString().matches(NUMERIC_LITERAL));
	}

	/**
	 * Adds two numbers and returns an Atom representing the sum.
	 *
	 * @param sexpr S-Expression
	 * @return The sum of the two elements in the given list (dot-notation form)
	 */
	public static TreeNode PLUS (final SExpressionOld sexpr) {
		return TreeNode.create(toInt(sexpr.address.evaluate(true)) + toInt(sexpr.data.evaluate(true)));
	}

	/**
	 * Subtracts two numbers and returns the difference.
	 *
	 * @param sexpr S-Expression list in dot-notation
	 * @return The difference of the two paramenters as an atom
	 */
	public static TreeNode MINUS (final SExpressionOld sexpr) throws Exception {
		return TreeNode.create(toInt(sexpr.address.evaluate(true)) - toInt(sexpr.data.evaluate(true)));
	}

	/**
	 * Divides two numbers with integer division and returns the quotient.
	 *
	 * @param sexpr S-Expression
	 * @return The quotient of the two parameters given by the S-Expression.
	 */
	public static TreeNode QUOTIENT(final SExpressionOld sexpr) throws Exception {
		return TreeNode.create(toInt(sexpr.address.evaluate(true)) / toInt(sexpr.data.evaluate(true)));
	}

	/**
	 * Computes and returns the product of two numbers.
	 *
	 * @param sexpr S-Expression
	 * @return The product of the two parameters as derived from the S-Expression
	 */
	public static TreeNode TIMES (final SExpressionOld sexpr) throws Exception {
		return TreeNode.create(toInt(sexpr.address.evaluate(true)) * toInt(sexpr.data.evaluate(true)));
	}

	/**
	 * Returns the r term in the integer division of x by y as
	 * given by x = y * q + r
	 *
	 * @param sexpr S-Expression
	 * @return The remainder after division as derived from the S-Expression
	 */
	public static TreeNode REMAINDER (final SExpressionOld sexpr) throws Exception {
		return TreeNode.create(toInt(sexpr.address.evaluate(true)) % toInt(sexpr.data.evaluate(true)));
	}

	/**
	 * Compares two objects and computes the strict 'less than' operator
	 *
	 * @param sexpr S-Expression
	 * @return the boolean answer to the 'less than' operation
	 */
	public static TreeNode LESS (final SExpressionOld sexpr) throws Exception {
		return TreeNode.create(toInt(sexpr.address.evaluate(true)) < toInt(sexpr.data.evaluate(true)));
	}

	/**
	 * Computer the 'greater than' operator using the arguments* found in the paramter S-Expression
	 *
	 * @param sexpr S-Expression
	 * @return The boolean answer to the 'greater than' operator
	 * @throws Exception If the S-Expression is malformed
	 */
	public static TreeNode GREATER (final SExpressionOld sexpr) throws Exception {
		return TreeNode.create(toInt(sexpr.address.evaluate(true)) > toInt(sexpr.data.evaluate(true)));
	}

	/**
	 * As per the operational semantics of Lisp, takes an S-Expression
	 * which represents a list of conditions. It evaluates them until
	 * one's CAR evaluates to T and then returns the second item.
	 *
	 * This roughly approximates the evcon function in the operational semantics
	 * where the error condition is taken care of by the SExpression
	 * constructor.
	 *
	 * @param sexpr The S-Expression describing the conditions
	 * @return The result of evaluating the expression in the list with the first boolean component
	 * @throws Exception If the S-Expression is malformed
	 *
	 */
	public static TreeNode COND (final SExpressionOld sexpr) throws Exception {
		SExpressionOld a = new SExpressionOld(sexpr.addressTokens);
		if ( a.address.evaluate().toString().matches(T) ){
			SExpressionOld tmp = new SExpressionOld(a.dataTokens);
			return tmp.address.evaluate(true);
		} else {
			SExpressionOld b = new SExpressionOld(sexpr.dataTokens);
			return COND(b);
		}
	}

	/**
	 * As per the operational semantics, returns the CADR of the containing S-Expression.
	 * Note that in this context, the 'data' has been passed as an argument similar to
	 * the other primitives, so we must only take the address portion and return it.
	 *
	 * @param sexpr S-Expression
	 * @return The address of s
	 */
	public static TreeNode QUOTE (final SExpressionOld sexpr) {
		return sexpr.address;
	}

	/**
	 * This function takes a List denoting the name of a new function, its parameter list, and
	 * its body. It is then broken down and entered into the environment, the representation
	 * of the operational 'd-list' and then simply returns an Atom of the name of the function.
	 *
	 * @param sexpr S-Expression containing all the necessary information
	 * @return An Atom of the function name if the registration is successful
	 * @throws Exception If the S-Expression is malformed
	 */
	public static TreeNode DEFUN (final SExpressionOld sexpr) throws ParseException {
		final String name = sexpr.address.toString();

		if (!name.matches(FUNCTION_NAME)) {
			throw new ParseException("Error! Function names must be character literals only");
		}

		if (Primitives.primitiveExists(name)) {
			throw new ParseException("Error! Cannot override a primitive function.");
		}

		SExpressionOld d = new SExpressionOld(sexpr.dataTokens);
		TreeNode params = TreeNode.create(d.addressTokens);
		SExpressionOld tmp = new SExpressionOld(d.dataTokens);
		TreeNode body = TreeNode.create(tmp.addressTokens);

		Environment.registerFunction(name, params, body);

		return new AtomOld(name);
	}

	/**
	 * Attempts to cast the literal value of the given {@link TreeNode} to an integer.
	 * @param node A {@link TreeNode}.
	 * @return The integer value of the given node, interpreted as a literal.
	 */
	private static Integer toInt(final TreeNode node) {
		return Integer.parseInt(node.toString());
	}

	/**
	 * Indicates if a particular primitive is defined.
	 *
	 * @param name The name of the primitive in question.
	 * @return True if the named primitive exists, false otherwise.
	 */
	private static boolean primitiveExists(final String name) {
		try{
			final Method method = Primitives.class.getDeclaredMethod(name, SExpressionOld.class);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}
}
