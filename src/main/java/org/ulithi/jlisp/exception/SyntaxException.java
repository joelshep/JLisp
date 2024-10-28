package org.ulithi.jlisp.exception;

public class SyntaxException extends JLispRuntimeException {
    /**
     * Default constructor.
     */
    public SyntaxException() {  }

    public SyntaxException(final String message) {
        super(message);
    }

    public SyntaxException(final String message, final Exception cause) {
        super(message, cause);
    }

    public SyntaxException(final Exception cause) {
        super(cause);
    }
}
