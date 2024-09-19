package org.ulithi.jlisp.main;

import org.ulithi.console.Console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * The main application class for an interactive read-evaluate-print-loop (REPL) LISP
 * interpreter.
 */
public final class REPL {

	/** Prompt for user input. */
	private static final String PROMPT = "\n> ";

    /** Line prefix to escape to a REPL-implemented command. **/
    private static final String ESCAPE_PREFIX = ":";

    /**
     * If true, parses each statement and prints the resulting parse tree in dotted-pair
     * notation, but does not evaluate it.
     */
    private static boolean parseOnlyMode = false;

    /**
     * If true, reconstructs the statement from the parse tree and prints it, but does
     * not evaluate it.
     */
    private static boolean echoMode = false;

	/**
	 * Creates the console app, initializes the LISP interpreter and starts the outer
	 * interpreter loop.
	 *
	 * @param args Any commandline arguments passed to the application.
	 */
	public static void main (final String [] args) {
		final Console console = new Console();

		console.setTitle("JLisp");

		startRepl();
	}

	/**
	 * Initializes the JLISP interpreter and runs the basic REPL (Read-Evaluate-Print-Loop).
	 */
	private static void startRepl () {
		final Interpreter lisp = new Interpreter();

		// Create a reader to read from STDIN.
		final InputStreamReader isr = new InputStreamReader(System.in);
		final BufferedReader term = new BufferedReader(isr);

		// Initialize and start the VM.
		lisp.initialize();
		(new Thread(lisp)).start();

        System.out.println("JLisp 0.10. \":help\" for help.");

		//
		// Prompt for input and interpret it line by line, until either a
		// fatal error occurs or the VM exits.
		//
		while (true)  {
			System.out.print(PROMPT);
			boolean ok = false;

			try {
				String input = term.readLine().trim();
				// Deal with backspace characters (streams are unaware ...).
				while (input.contains("\b")) { input = input.replaceAll("^\b+|[^\b]\b", ""); }

                if (isReplCommand(input)) {
                    ok = processReplCommand(input);
                } else if (parseOnlyMode) {
                    ok = lisp.parse(input);
                } else if (echoMode) {
                    ok = lisp.parseAndEcho(input);
                } else {
                    ok = lisp.offer(input);
                }
			} catch (final Exception e) {
                System.err.println("An exception occurred: " + e.getMessage());
                e.printStackTrace(System.err);
				lisp.reset();
			} finally {
				System.err.flush();
				safeSleep(20); // Blech. But it helps ...

				if (!ok) {
					System.out.println();
				}
			}
		}
	}

    /**
     * Indicates if the given input is (likely to be) a direct command to the REPL, determined
     * by checking for the REPL escape character.
     */
    private static boolean isReplCommand(final String input) {
        return input != null && input.startsWith(ESCAPE_PREFIX);
    }

    /**
     * Attempts to process the given input as a direct command to the REPL.
     */
    private static boolean processReplCommand(final String input) {
        if (input.length() < 2) {
            showHelp();
        }

        final String command = input.substring(1).trim().toUpperCase();

        switch (command) {
            case "PARSE":
                parseOnlyMode = true;
                echoMode = false;
                System.err.println("Entering 'parse' mode ...");
                break;
            case "EVAL":
                parseOnlyMode = false;
                echoMode = false;
                System.err.println("Entering 'eval' mode ...");
                break;
            case "ECHO":
                echoMode = true;
                parseOnlyMode = false;
                System.err.println("Entering 'echo' mode ...");
                break;
            case "QUIT":
                System.exit(0);
                break;
            default:
                showHelp();
        }

        return true;
    }

    /**
     * Writes a help message for this REPL to STDERR.
     */
    private static void showHelp() {
        System.err.println("Enter \":\" (colon) for a REPL command. Available commands:");
        System.err.println("\tPARSE: Parse statement and print parse tree without evaluating");
        System.err.println("\tECHO: Parse statement and print parsed statement without evaluating");
        System.err.println("\tEVAL: Return to normal evaluation mode");
        System.err.println("\tQUIT: Quit/exit the REPL");
        System.err.println("\tHELP: Print this message");
        System.err.flush();
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
