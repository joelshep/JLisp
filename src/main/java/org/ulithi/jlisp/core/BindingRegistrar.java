package org.ulithi.jlisp.core;

/**
 * A {@link BindingRegistrar} is a registry for function, symbol and other bindings provided by a
 * {@link BindingProvider} such as a "package". A {@code BindingRegistrar} -- such as the
 * {@code Environment} -- typically provides lookup and other binding management at runtime.
 */
public interface BindingRegistrar {
    /**
     * Invoked to register a {@link Binding} for a function, symbol, etc., with this registrar,
     * typically by a {@link BindingProvider}. Registering a binding enables the registrar to find
     * the binding given its programmatic name or a synonym.
     *
     * @param binding A {@link Binding} to register.
     */
    void register(Binding binding);
}
