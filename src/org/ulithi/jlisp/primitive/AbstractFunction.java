package org.ulithi.jlisp.primitive;

/**
 * Abstract implementation of {@link Function} that provides the function name.
 */
public abstract class AbstractFunction implements Function {
    /** The programmatic name of this function. */
    private final String name;

    /**
     * Constructs a new {@link AbstractFunction} for a function with the specified
     * programmatic {@code name}.
     * @param name The programmatic name of the function: e.g., "CAR", "PLUS", etc.
     */
    public AbstractFunction(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String name() { return this.name; }
}
