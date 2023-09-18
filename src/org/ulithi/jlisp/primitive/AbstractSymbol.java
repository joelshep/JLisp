package org.ulithi.jlisp.primitive;

/**
 * Abstract implementation of {@link Symbol} that provides the symbol name.
 */
public abstract class AbstractSymbol implements Symbol {
    /** The programmatic name of this symbol. */
    private final String name;

    /**
     * Constructs a new {@link AbstractSymbol} with the specified programmatic {@code name}.
     * @param name The programmatic name of the symbol: e.g., "T", "F", etc.
     */
    public AbstractSymbol(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String name() { return this.name; }
}
