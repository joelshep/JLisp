package org.ulithi.jlisp.core;

import java.util.Collections;
import java.util.List;

/**
 * A {@link BindingProvider} provides an interface for "binding registrars" (like the environment)
 * to bootstrap with primitive function and symbol definitions provided by the
 * {@code BindingProvider}.
 */
public interface BindingProvider {
    /**
     * Returns the list of {@link Binding bindings} provided by this {@link BindingProvider}. The
     * default implementation simply returns an empty list.
     *
     * @return The list of bindings provided by this {@code BindingProvider}.
     */
    default List<Binding> getBindings() { return Collections.emptyList(); }

    /**
     * Requests that the {@link BindingProvider} provide bindings to the given
     * {@link BindingRegistrar}, via the registrar's {@code register} method. The default
     * implementation requests all the provider's bindings via this interface's {@code getBindings}
     * method and registers them.
     *
     * @param registrar The {@code BindingRegistrar} that is requesting bindings from this provider.
     */
    default void provideBindings(final BindingRegistrar registrar) {
        for (final Binding binding: getBindings()) {
            registrar.register(binding);
        }
    }
}
