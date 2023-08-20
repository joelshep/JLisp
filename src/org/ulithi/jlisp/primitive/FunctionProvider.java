package org.ulithi.jlisp.primitive;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A {@link FunctionProvider} provides an interface for {@code Eval} to bootstrap with primitive
 * function implementations provided by the {@code FunctionProvider}.
 */
public interface FunctionProvider {

    /**
     * Returns the list of {@link Function Functions} provided by this {@link FunctionProvider}. The
     * default implementation simply returns an empty list.
     *
     * @return The list of {@code Function Functions} provided by this {@code FunctionProvider}.
     */
    default List<Function> getFunctions() { return Collections.emptyList(); }

    /**
     * Requests that the {@link FunctionProvider} provide functions to the given
     * {@link FunctionRegistrar}, via the registrar's {@code registerFunction} method. The default
     * implementation requests all the provider's functions via this interface's
     * {@code getFunctions} method and registers them.
     *
     * @param registrar The {@code FunctionRegistrar} that is requesting the provider to provide
     *                  functions.
     */
    default void provideFunctions(FunctionRegistrar registrar) {
        for (final Function function: getFunctions()) {
            registrar.registerFunction(function);
        }
    }
}
