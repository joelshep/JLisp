package org.ulithi.jlisp.exception;

/**
 * Exception thrown when a function receives an argument of the wrong type or of an inappropriate
 * value.
 */
public class InvalidArgumentException extends JLispRuntimeException {
    /**
     * Default constructor.
     */
    public InvalidArgumentException() {  }

    public InvalidArgumentException(final String message) {
        super(message);
    }

    public InvalidArgumentException(final String message, final Exception cause) {
        super(message, cause);
    }

    public InvalidArgumentException(final Exception cause) {
        super(cause);
    }
}
