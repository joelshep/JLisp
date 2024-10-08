package org.ulithi.jlisp.core;

/**
 * As a language element, {@link Symbol} is essentially an enumerated value. Evaluating the symbol
 * results in its associated value. Once defined, a symbol and its value are immutable, and the
 * symbol itself can be considered to be a singleton.
 */
public interface Symbol extends Bindable {
    /**
     * @return The programmatic name of this {@link Symbol}: e.g., "T", "F", etc.
     */
    String name();

    /**
     * @return An {@link SExpression} representing the value of this {@link Symbol}
     */
    SExpression eval();
}
