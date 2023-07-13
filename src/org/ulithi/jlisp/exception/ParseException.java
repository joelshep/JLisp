package org.ulithi.jlisp.exception;

/**
 * Exception thrown when parsing of an expression fails.
 */
public class ParseException extends JLispRuntimeException {
    /**
     * Default constructor.
     */
    public ParseException() {  }

    public ParseException(final String message) {
        super(message);
    }

    public ParseException(final String message, final Exception cause) {
        super(message, cause);
    }

    public ParseException(final Exception cause) {
        super(cause);
    }

}
