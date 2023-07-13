package org.ulithi.jlisp.exception;

/**
 * Exception thrown when an undefined symbol is encountered.
 */
public class UndefinedSymbolException extends JLispRuntimeException {
    /**
     * Default constructor.
     */
    public UndefinedSymbolException() {  }

    public UndefinedSymbolException(final String message) {
        super(message);
    }

    public UndefinedSymbolException(final String message, final Exception cause) {
        super(message, cause);
    }

    public UndefinedSymbolException(final Exception cause) {
        super(cause);
    }

}
