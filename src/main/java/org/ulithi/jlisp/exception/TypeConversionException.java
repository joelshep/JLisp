package org.ulithi.jlisp.exception;

public class TypeConversionException extends JLispRuntimeException {
        /**
         * Default constructor.
         */
        public TypeConversionException() {  }

        public TypeConversionException(final String message) {
            super(message);
        }

        public TypeConversionException(final String message, final Exception cause) {
            super(message, cause);
        }

        public TypeConversionException(final Exception cause) {
            super(cause);
        }
}
