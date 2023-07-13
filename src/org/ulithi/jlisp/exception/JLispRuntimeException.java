package org.ulithi.jlisp.exception;

/**
 * Base class for exceptions defined by the JLisp interpreter.
 */
public class JLispRuntimeException extends RuntimeException {
    /**
     * Default constructor.
     */
    public JLispRuntimeException() {  }

    public JLispRuntimeException(final String message) {
        super(message);
    }

    public JLispRuntimeException(final String message, final Exception cause) {
        super(message, cause);
    }

    public JLispRuntimeException(final Exception cause) {
        super(cause);
    }
}
