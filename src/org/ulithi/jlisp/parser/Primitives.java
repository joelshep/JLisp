package org.ulithi.jlisp.parser;

import java.lang.Integer;
import java.lang.String;
import java.lang.reflect.Method;

/**
 * Implements the primitive LISP functions supported by this interpreter.
 */

class Primitives {

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
	 * Creates a new atom representing the "true" value.
	 *
	 * @return The T Atom
	 */
	public static TreeNode T() {
		return new Atom(true);
	};

	/**
	 * Creates an atom to represent the NIL value.
	 *
	 * @return The NIL Atom
	 */
	public static TreeNode NIL() {
		return new Atom(false);
	};

	/**
	 * Carries out the CONS operation as defined by the Lisp operational semantics.
	 * It combines the CAR and CADR of the argument list into a single list. If
	 * the arguments fail for these operations, an Exception will be thrown in the
	 * SExpression constructor.
	 *
	 * @param sexpr The SExpression arguments in dot-notation
	 * @return CONS[ CAR[s], CADR[s] ] - the semantically defined CONS
	 * @throws Exception if the arguments are inappropriate
	 */
	public static SExpression CONS (final SExpression sexpr ) throws Exception {
		final SExpression tmp = new SExpression(sexpr.dataTokens);
		return new SExpression(sexpr.address.evaluate(), tmp.address.evaluate());
	}

	/**
	 * returns the car of the given S-Expression as defined by the operational
	 * semantics.
	 *
	 * @param sexpr The argument S-Expression
	 * @return The address of the given S-Exp
	 */
	public static TreeNode CAR (final SExpression sexpr) {
		return sexpr.address;
	}

	/**
	 * Returns the data of the given S-Expression
	 *
	 * @param sexpr An S-Expression
	 * @return The data of the S-Expression
	 */
	public static TreeNode CDR (final SExpression sexpr) {
		return sexpr.data;
	}

	/**
	 * Determines if an S-Expression is semantically an Atom
	 *
	 * @param sexpr The S-Expression in question
	 * @return True if it is an atom literal, false otherwise.
	 */
	public static TreeNode ATOM (final SExpression sexpr) throws Exception {
		return TreeNode.create(sexpr.address.evaluate().toString().matches(Patterns.LITERAL));
	}

	/**
	 * Determines if the value of two S-Expressions are equal
	 *
	 * @param sexpr The paramenter S-Expression
	 * @return T or NIL whether or not they are the same
	 * @throws Exception If the S-Expression is malformed
	 */
	public static TreeNode EQ (final SExpression sexpr) throws Exception {
		return TreeNode.create(sexpr.address.evaluate(true).toString().matches(sexpr.data.evaluate(true).toString()));
	}

	/**
	 * Determines if the S-Expression is NIL
	 *
	 * @param sexpr The S-Expression in question
	 * @return T or NIL whether or not the S-Expression is NIL
	 * @throws Exception If the S-Expression is malformed
	 */
	public static TreeNode NULL (final SExpression sexpr) throws Exception {
		return TreeNode.create(sexpr.data.evaluate().toString().matches("NIL"));
	}

	/**
	 * Determines if an S-Expression is an integer
	 *
	 * @param sexpr An S-Expression
	 * @return T or NIL whether or not it is an integer
	 */
	public static TreeNode INT (final SExpression sexpr) throws Exception{
		return TreeNode.create(sexpr.address.evaluate(true).toString().matches(Patterns.NUMERIC_ATOM));
	}

	/**
	 * Adds two numbers.
	 *
	 * @param sexpr S-Expression
	 * @return The sum of the two elements in the given list (dot-notation form)
	 * @throws Exception If the S-Expression is malformed
	 */
	public static TreeNode PLUS (final SExpression sexpr) throws Exception {
		return TreeNode.create(Integer.parseInt(sexpr.address.evaluate(true).toString()) + Integer.parseInt(sexpr.data.evaluate(true).toString()));
	}

	/**
	 * Subtracts two numbers.
	 *
	 * @param sexpr S-Expression list in dot-notation
	 * @return The difference of the two paramenters as an atom
	 * @throws Exception If the S-Expression is malformed
	 */
	public static TreeNode MINUS (final SExpression sexpr) throws Exception {
		return TreeNode.create(Integer.parseInt(sexpr.address.evaluate(true).toString()) - Integer.parseInt(sexpr.data.evaluate(true).toString()));
	}

	/**
	 * Divides two numbers with integer division.
	 *
	 * @param sexpr S-Expression
	 * @return The quotient of the two parameters given by the S-Expression.
	 * @throws Exception If the S-Expression is malformed
	 *
	 */
	public static TreeNode QUOTIENT(final SExpression sexpr) throws Exception {
		return TreeNode.create(Integer.parseInt(sexpr.address.evaluate(true).toString()) / Integer.parseInt(sexpr.data.evaluate(true).toString()));
	}

	/**
	 * Computes the product of two numbers.
	 *
	 * @param sexpr S-Expression
	 * @return The product of the two parameters as derived from the S-Expression
	 * @throws Exception If the S-Expression is malformed
	 */
	public static TreeNode TIMES (final SExpression sexpr) throws Exception {
		return TreeNode.create(Integer.parseInt(sexpr.address.evaluate(true).toString()) * Integer.parseInt(sexpr.data.evaluate(true).toString()));
	}

	/**
	 * Returns the r term in the integer division of x by y as
	 * given by x = y * q + r
	 *
	 * @param sexpr S-Expression
	 * @return The remainder after division as derived from the S-Expression
	 * @throws Exception If the S-Expression is malformed
	 */
	public static TreeNode REMAINDER (final SExpression sexpr) throws Exception {
		return TreeNode.create(Integer.parseInt(sexpr.address.evaluate(true).toString()) % Integer.parseInt(sexpr.data.evaluate(true).toString()));
	}

	/**
	 * Compares two objects and computes the strict 'less than' operator
	 *
	 * @param sexpr S-Expression
	 * @return the boolean answer to the 'less than' operation
	 * @throws Exception If the S-Expression is malformed
	 */
	public static TreeNode LESS (final SExpression sexpr) throws Exception {
		return TreeNode.create(Integer.parseInt(sexpr.address.evaluate(true).toString()) < Integer.parseInt(sexpr.data.evaluate(true).toString()));
	}

	/**
	 * Computer the 'greater than' operator using the arguments* found in the paramter S-Expression
	 *
	 * @param sexpr S-Expression
	 * @return The boolean answer to the 'greater than' operator
	 * @throws Exception If the S-Expression is malformed
	 */
	public static TreeNode GREATER (final SExpression sexpr) throws Exception {
		return TreeNode.create(Integer.parseInt(sexpr.address.evaluate(true).toString()) > Integer.parseInt(sexpr.data.evaluate(true).toString()));
	}

	/**
	 * Function: COND
	 *
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
	public static TreeNode COND (final SExpression sexpr) throws Exception {
		SExpression a = new SExpression(sexpr.addressTokens);
		if ( a.address.evaluate().toString().matches("T") ){
			SExpression tmp = new SExpression(a.dataTokens);
			return tmp.address.evaluate(true);
		} else {
			SExpression b = new SExpression(sexpr.dataTokens);
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
	 * @throws Exception If the S-Expression is malformed
	 *
	 */
	public static TreeNode QUOTE (final SExpression sexpr) throws Exception {
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
	public static TreeNode DEFUN (final SExpression sexpr) throws Exception {
		final String name = sexpr.address.toString();

		if (!name.matches(Patterns.VALID_FUNCTION_NAME)) {
			throw new Exception("Error! Function names must be character literals only");
		}

		if (Primitives.primitiveExists(name)) {
			throw new Exception("Error! Cannot override a primitive function.");
		}

		SExpression d = new SExpression(sexpr.dataTokens);
		TreeNode params = TreeNode.create(d.addressTokens);
		SExpression tmp = new SExpression(d.dataTokens);
		TreeNode body = TreeNode.create(tmp.addressTokens);

		Environment.registerFunction(name, params, body);

		return new Atom(name);
	}

	/**
	 * Indicates if a particular primitive is defined.
	 *
	 * @param name The name of the primitive in question.
	 * @return True if the named primitive exists, false otherwise.
	 */
	private static boolean primitiveExists(final String name) {
		try{
			final Method method = Primitives.class.getDeclaredMethod(name, SExpression.class);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}
}
