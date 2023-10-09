package org.ulithi.jlisp.core;

import org.ulithi.jlisp.primitive.Function;
import org.ulithi.jlisp.primitive.Symbol;

/**
 * Encapsulates the binding of a programmatic name to a {@link Bindable} object, such as
 * a {@code Function} or a {@code Symbol}.
 */
public class Binding {
    /** Singleton instance of an empty {@code String} array. */
    public static final String[] EMPTY_SYNONYMS = new String[]{ };

    /** The programmatic name used to refer to the bound object. */
    private final String name;

    /**
     * Synonyms that can be used to refer to the bound object: alternate programmatic names. Most
     * bindings do not have synonyms.
     */
    private final String[] synonyms;

    /** The object bound to the programmatic name and synonyms. */
    private final Bindable bindable;

    /**
     * Creates a new {@link Binding} for the specified {@link Function}.
     * @param function The {@code Function} that the {@code Binding} will refer to.
     */
    public Binding (final Function function) {
        this (function.name(), function.synonyms(), function);
    }

    /**
     * Creates a new {@link Binding} for the specified {@link Symbol}.
     * @param symbol The {@code Symbol} that the {@code Binding} will refer to.
     */
    public Binding (final Symbol symbol) {
        this (symbol.name(), symbol);
    }

    /**
     * Binds the specified programmatic {@code name} to the given {@link Bindable}.
     * @param name A programmatic name for the {@code Bindable}.
     * @param bindable A {@code Bindable}: e.g. a function, symbol or variable.
     */
    public Binding (final String name, final Bindable bindable) {
        this(name, EMPTY_SYNONYMS, bindable);
    }

    /**
     * Binds the specified programmatic {@code name} and any synonyms to the given {@link Bindable}.
     * @param name A programmatic name for the {@code Bindable}.
     * @param synonyms An array of alternate programmatic names for the {@code Bindable}. May be
     *                 empty.
     * @param bindable A {@code Bindable}: e.g. a function, symbol or variable.
     */
    public Binding (String name, String[] synonyms, Bindable bindable) {
        this.name = name;
        this.bindable = bindable;
        this.synonyms = synonyms;
    }

    /**
     * The programmatic name for this {@link Binding}.
     * @return The programmatic name for this {@link Binding}.
     */
    public String name() { return name; }

    /**
     * Returns any synonyms for this {@link Binding}: alternate programmatic names. Most bindings
     * do not have synonyms.
     *
     * @return An array of alternate programmatic names for this {@code Binding}, or an
     *         empty array if there are none.
     */
    public String[] synonyms() { return synonyms; }

    /**
     * Returns the object of this {@link Binding}: e.g. a function, symbol or variable.
     * @return The object of this {@link Binding}.
     */
    public Bindable bindable() { return bindable; }
}
