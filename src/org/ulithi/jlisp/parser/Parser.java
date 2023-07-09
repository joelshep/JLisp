package org.ulithi.jlisp.parser;

import org.ulithi.jlisp.commons.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the main Parser class for the program. It handles the
 * tokens from the lexical analysis, converts them to dot-notation,
 * and provides public access to program evaluation.
 */

public class Parser {

	final List<ParseTree> statements = new ArrayList<>();

	/**
	 * This function initializes the parser from the lexical tokens,
	 * splitting them into statements, and storing them.
	 *
	 * @param tokens The vector of lexical tokens from analysis
	 *
	 * @throws Exception If conversion to dot-notation fails
	 *
	 */
	public Parser(final List<String> tokens) throws Exception {
		List<String> t;
		int i = 0;
		int k, l;

		while ( i < tokens.size() && i >= 0 ) {
			// The variable l must always hold the last location of the current statement!!!
			k = StringUtils.indexOf(tokens, i, "("); //i + tokens.subList(i, tokens.size() - 1).indexOf("(");
			if ( k == i ) {
				// There is a new statement to add - starting at i
				l = endOfExpression(new ArrayList<>(tokens.subList(i, tokens.size()))) + i;
			} else if ( k > i ) {
				// The next statement is after some non-parenthetical stuff
				l = k - 1;
			} else {
				// There are no more parenthetical statements. Parse the rest of the program
				l = tokens.size() - 1;
			}

			t = convertToDotNotation(new ArrayList<>(tokens.subList(i, l+1)));
			statements.add(new ParseTree(t));

			i = l + 1;
		}
	}

	/**
	 * This evaluates the statements one-by-one and prints
	 * the results.
	 *
	 * @throws Exception If any evaluation fails on individual statements
	 *
	 */
	public void evaluate() throws Exception {
		// String rtn = "";
		for ( int i = 0; i < statements.size(); i++ ){
			System.out.println(statements.get(i).evaluate());
			// rtn = rtn + statements.get(i).evaluate();
			// 			if ( i < statements.size() - 1){
			// 				rtn = rtn + "\n";
			// 			}
		}
		// return rtn;
	}

	/**
	 * This uses a naïve grammar to split the given program and rejoin
	 * it into legal Lisp S-Expressions.
	 *
	 * @param tokens The vector of tokens representing the program
	 * @return The program converted to dot notation in a string vector
	 */
	private List<String> convertToDotNotation(final List<String> tokens) {
		List<String> r = new ArrayList<>();
		List<String> temp;
		int nextInnerToken;

		if (tokens.get(0).matches("[(]")) {
			// We have a list or S-Expression

			int closeParen = endOfExpression(tokens);
			if (closeParen > 1) {
				// The expression is not ()

				r.add("(");

				if (closeParen > 2) {
					// There is more than one token - so not ( a )

					// Is the first one a nested expression?
					if (tokens.get(1).matches("[(]")) {
						nextInnerToken = endOfExpression(tokens.subList(1, closeParen)) + 2;
					} else {
						nextInnerToken = 2;
					}

					if (!tokens.get(nextInnerToken).matches("[.]")) {
						// The expression must be a list because it is not in dot-notation
						r.addAll(convertToDotNotation(tokens.subList(1,nextInnerToken)));
						r.add(".");
						temp = new ArrayList<>();
						temp.add("(");
						temp.addAll(tokens.subList(nextInnerToken, closeParen));
						temp.add(")");
						r.addAll(convertToDotNotation(temp));
					} else {
						// Since it is in the form of ( [stuff] . [stuff] ), we pass [stuff] to be converted
						r.addAll(convertToDotNotation(tokens.subList(1,nextInnerToken)));
						r.add(".");
						r.addAll(convertToDotNotation(tokens.subList(nextInnerToken+1, closeParen)));
					}
				} else {
					// The statement is in the form ( a )
					r.add(tokens.get(1));
					r.add(".");
					r.add("NIL");
				}
				r.add(")");
			} else {
				// We have ()
				r.add("NIL");
			}
		} else {
			if (tokens.indexOf("(") > 0) {
				r.addAll(tokens.subList(0, tokens.indexOf("(")));
				r.addAll(convertToDotNotation(tokens.subList(tokens.indexOf("("), tokens.size())));
			} else {
				r = tokens;
			}
		}
		return r;
	}

	/**
	 * This finds the closing parenthesis of a given Lisp segment
	 *
	 * @param s The tokens (in dot-notation) of a segment of Lisp code
	 * @return the index of the closing parenthesis
	 *
	 * @throws IllegalArgumentException If the vector is not a parenthetical expression
	 * @throws ArrayIndexOutOfBoundsException If the statement does not have a closing parenthesis
	 *
	 */
	private int endOfExpression(final List<String> s) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
		if ( ! s.get(0).matches("[(]") ){
			throw new IllegalArgumentException("ERROR: Tried to find the end of an expression that did not begin with '('.");
		}

		int openPairs = 1;
		int end = 1;
		while (openPairs > 0) {
			if (end >= s.size()) {
				throw new ArrayIndexOutOfBoundsException("Error! Unbalanced parentheses.");
			}
			if (s.get(end).matches("[)]")) {
				openPairs--;
			} else if (s.get(end).matches("[(]")) {
				openPairs++;
			}
			if (openPairs == 0) {
				break;
			} else {
				end++;
			}
		}
		return end;
	}
}
