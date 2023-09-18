package org.ulithi.jlisp.primitive;

/**
 * A {@link Bindable is something that can be bound in an environment: functions, variables and
 * symbols.
 */
public interface Bindable {
    /** Singleton instance of an empty {@code String} array. */
    String[] EMPTY_SYNONYMS = new String[]{ };

    /**
     * The programmatic name for this {@link Bindable}.
     *
     * @return The programmatic name for this {@link Bindable}.
     */
    String name();

    /**
     * Returns synonyms for this {@link Bindable}: alternate programmatic names. Most
     * bindables do not have synonyms.
     *
     * @return An array of alternate programmatic names for this {@code Bindable}, or an
     *         empty array if there are none.
     */
    default String[] synonyms() { return EMPTY_SYNONYMS; }
}
