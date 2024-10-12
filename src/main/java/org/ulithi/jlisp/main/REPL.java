package org.ulithi.jlisp.main;

import org.apache.commons.lang3.ArrayUtils;
import org.ulithi.console.Console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;

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
	public static void main(final String [] args) {
		final Console console = new Console();
        final Interpreter lisp = new Interpreter();

		console.setTitle(lisp.getName());

		runRepl(lisp);
	}

	/**
	 * Initializes the JLISP interpreter and runs the basic REPL (Read-Evaluate-Print-Loop).
     *
     * @param lisp An instance of the JLISP {@link Interpreter}.
	 */
	private static void runRepl(final Interpreter lisp) {

		// Create a reader to read from STDIN.
		final InputStreamReader isr = new InputStreamReader(System.in);
		final BufferedReader term = new BufferedReader(isr);

		// Initialize and start the VM.
		lisp.initialize();
		(new Thread(lisp)).start();

        System.out.printf("%s %s. %s%n",
                          lisp.getName(), lisp.getVersion(),
                          "\":help\" for help.");

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
                    ok = processReplCommand(input, lisp);
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
    private static boolean processReplCommand(final String input, final Interpreter lisp) {
        if (input.length() < 2) {
            showHelp();
        }

        // Commands should consist of a ":", an action keyword, and then zero or more space-
        // delimited arguments. Extract those and then carry out the specified action.
        final String command = input.substring(1).trim();

        final String[] tokens = command.split(" ");
        final String action = tokens[0].toUpperCase();
        final String[] args = tokens.length > 1 ? Arrays.copyOfRange(tokens, 1, tokens.length)
                                                : ArrayUtils.EMPTY_STRING_ARRAY;

        switch (action) {
            case "PARSE":
                parseOnlyMode = true;
                echoMode = false;
                System.err.println("Entering 'parse' mode ...");
                break;
            case "EVAL":
                parseOnlyMode = false;
                echoMode = false;
                lisp.verbose(false);
                System.err.println("Entering 'eval' mode ...");
                break;
            case "ECHO":
                echoMode = true;
                parseOnlyMode = false;
                System.err.println("Entering 'echo' mode ...");
                break;
            case "LOAD":
                loadAndEvalFromFile(args, lisp);
                break;
            case "VERBOSE":
                lisp.verbose(true);
                System.err.println("Verbose error output enabled ...");
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
        System.err.println("\tVERBOSE: Dump stack trace in the event of a processing error");
        System.err.println("\tHELP: Print this message");
        System.err.flush();
    }

    /**
     * Expects a valid file path and name in {@code args}. Opens and reads the file as text, with
     * one LISP expression per line, and runs each through the interpreter in order.
     *
     * @param args Expects a single-element array, containing the full path and file name of the
     *             file to load.
     * @param lisp An initialized JLisp interpreter.
     */
    private static void loadAndEvalFromFile(final String[] args, final Interpreter lisp) {
        if (ArrayUtils.isEmpty(args) || args.length > 1) {
            System.err.println("LOAD expects file name as an argument");
            return;
        }

        // See if the specified file exists
        final String fileName = args[0];
        final File f = new File(fileName);

        if (!f.exists()) {
            System.err.println("File '" + fileName + "' not found");
            return;
        }

        try (final BufferedReader file = new BufferedReader((new FileReader(fileName)))) {
            String text = file.readLine();

            while (text != null) {
                lisp.offer(text);
                text = file.readLine();
            }
        } catch (final Exception e) {
            System.err.println("Error reading file '" + fileName + "': " + e.getMessage());
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
