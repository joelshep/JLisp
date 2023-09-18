package org.ulithi.jlisp.primitive;

/**
 * A {@link BindingRegistrar} is a registry for function, symbol and other bindings provided by a
 * {@link BindingProvider} such as a "package". A {@code BindingRegistrar} -- such as the
 * {@code Environment} typically provides lookup and other bindings management at runtime.
 */
public interface BindingRegistrar {
    /**
     * Invoked to register a {@link Bindable} function, symbol, etc., with this registrar, typically
     * by a {@link BindingProvider}. Registering a binding enables the registrar to find the
     * binding by a programmatic name.
     *
     * @param bindable A {@link Bindable} to register.
     */
    void register(Bindable bindable);
}
