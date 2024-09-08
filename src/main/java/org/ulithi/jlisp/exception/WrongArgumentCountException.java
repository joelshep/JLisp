package org.ulithi.jlisp.exception;

/**
 * Thrown when a function is invoked with the wrong number of arguments.
 */
public class WrongArgumentCountException extends JLispRuntimeException {
    public WrongArgumentCountException(final String message) {
        super(message);
    }
}
