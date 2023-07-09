package org.ulithi.jlisp.main;

import org.ulithi.jlisp.lexer.Lexer;
import org.ulithi.jlisp.parser.Parser;

import java.io.IOException;

/**
 * This is the driver file for the Lisp Interpreter project.
 * <p>
 * It takes input from stdin and evaluates it line by line.
 * One should note that if muliple statements are on the same line,
 * their results will not be output until all have executed
 * successfully.
 */
class LispInterpreter {

	/**
	 * The main function of the program.
	 *
	 * @param args Any command line arguments. None are used as of this version.
	 */
    public static void main(final String[] args) {
    	try {
			//final String expression = "(+ 3 2 7 9)";
			final String expression = "(PLUS 3 2 7 9)";
	    	//final Lexer l = new Lexer(System.in);
			final Lexer lexer = new Lexer(expression);
	    	final Parser p = new Parser(lexer.getTokens());
	    	// System.out.println(p.printParseTree());
			p.evaluate();
		} catch (final IOException e) {
			System.out.println("End of input...");
    	} catch (final Exception e) {
			System.err.println("Error: " + e);
			if ( args.length > 0 && args[0].matches("-d") ){
    			System.err.println(e.getMessage());
	    		e.printStackTrace();
			}
    		System.exit(3);
    	}
    }
}
