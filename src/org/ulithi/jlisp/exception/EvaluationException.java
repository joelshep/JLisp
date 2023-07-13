package org.ulithi.jlisp.exception;

/**
 * Exception thrown when the evaluation of a function fails at runtime.
 */
public class EvaluationException extends JLispRuntimeException {
    /**
     * Default constructor.
     */
    public EvaluationException() {  }

    public EvaluationException(final String message) {
        super(message);
    }

    public EvaluationException(final String message, final Exception cause) {
        super(message, cause);
    }

    public EvaluationException(final Exception cause) {
        super(cause);
    }

}
