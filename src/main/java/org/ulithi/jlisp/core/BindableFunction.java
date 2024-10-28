package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.SyntaxException;
import org.ulithi.jlisp.parser.Grammar;

/**
 * Abstract implementation of {@link Function} that can be bound to a function name.
 */
public abstract class BindableFunction implements Function, Bindable {

    /** The programmatic name of this function. */
    private final String name;

    /**
     * Constructs a new {@link BindableFunction} with the specified programmatic {@code name}.
     * @param name The programmatic name of the function: e.g., "CAR", "PLUS", etc.
     */
    public BindableFunction(final String name) {
        if (!Grammar.isFunctionName(name)) {
            throw new SyntaxException("'" + name + "' is not a legal function name");
        }

        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public final String name() { return this.name; }
}
