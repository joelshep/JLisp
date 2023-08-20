package org.ulithi.jlisp.primitive;

/**
 * A {@link FunctionRegistrar} is a registry for functions provided by a {@link FunctionProvider}:
 * typically an {@code Eval} implementation that invokes the functions.
 */
public interface FunctionRegistrar {
    /**
     * Invoked to register a {@link Function} with this registrar, typically by a
     * {@link FunctionProvider}. Registering a function enables the registrar to find the
     * function implementation by a programmatic name.
     *
     * @param function A {@link Function} to register.
     */
    void registerFunction(Function function);
}
