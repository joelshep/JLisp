package org.ulithi.jlisp.main;

import org.ulithi.console.Console;
import org.ulithi.jlisp.core.Interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The main application class for an interactive read-evaluate-print-loop (REPL) LISP
 * interpreter.
 */
public final class REPL {
	/**
	 * Prompt for user input.
	 */
	private static final String PROMPT = "\n> ";

	/**
	 * Creates the console app, initializes the LISP interpreter and starts the outer
	 * interpreter loop.
	 *
	 * @param args Any commandline arguments passed to the application.
	 */
	public static void main (final String [] args) throws IOException {
		final Console console = new Console();

		console.setTitle("JLisp");

		startRepl();
	}

	/**
	 * Initializes the JLISP interpreter and runs the basic REPL (Read-Evaluate-Print-Loop).
	 */
	private static void startRepl () throws IOException {
		// Create a Forth VM that will accept strings of tokens directly (not via stream).
		final Interpreter ip = new Interpreter();

		// Create a reader to read from STDIN.
		final InputStreamReader isr = new InputStreamReader(System.in);
		final BufferedReader term = new BufferedReader(isr);

		// Initialize and start the VM.
		ip.initialize();
		(new Thread(ip)).start();

		//
		// Prompt for input and interpret it line by line, until either a
		// fatal error occurs or the VM exits.
		//
		while (true)  {
			System.out.print(PROMPT);
			boolean ok = false;

			try {
				String input = term.readLine();
				// Deal with backspace characters (streams are unaware ...).
				while (input.contains("\b")) { input = input.replaceAll("^\b+|[^\b]\b", ""); }
				ok = ip.offer(input);
			} catch (final Exception e) {
				e.printStackTrace();
				ip.reset();
			} finally {
				System.err.flush();
				safeSleep(20); // Blech. But it helps ...

				if (ok) {
					//System.out.println(ACK);
				} else {
					System.out.println();
				}
			}
		}
	}

	/**
	 * Causes the currently executing thread to stop execution for the specified number of
	 * milliseconds. This method suppresses any {@link InterruptedException} thrown while the
	 * thread is stopped.
	 *
	 * @param millis Number of milliseconds (approximately) to stop the current thread
	 *               execution.
	 */
	private static void safeSleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// Swallow it.
		}
	}
}
