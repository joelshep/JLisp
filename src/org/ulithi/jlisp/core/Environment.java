package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.primitive.Lang;
import org.ulithi.jlisp.primitive.Logic;
import org.ulithi.jlisp.primitive.Math;
import org.ulithi.jlisp.primitive.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The JLISP {@link Environment}. The {@code Environment} is initialized with a core package of
 * built-in LISP functions and symbols. A function invocation creates a "scope" that is effective
 * during the lifetime of the invocation, where new bindings (e.g. variables) can be created. The
 * scope is released when the function returns.
 */
public final class Environment implements BindingRegistrar {

    /**
     * The index of the "core" package in the "bindings" list.
     */
    private static final int CORE_ENV_INDEX = 0;

    /**
     * The number of packages (whose symbols can't be redefined) currently loaded in this
	 * environment, including the built-in "core" package.
     */
    private int packageCount = 0;

    /**
     * The number of function-specific scopes in this environment. In general, the index of the
     * most recently started function-specific scope will be at:
     *    packageCount + scopeCount - 1
     */
    private int scopeCount = 0;

    /**
     * The environment bindings. The "core" language binding is the first element in the list,
     * followed by package bindings, followed by run-time dynamically scoped bindings.
     */
    private final List<Map<String, Bindable>> bindings;

	/**
	 * Initializes the {@code environment}, including creating bindings for core (built-in)
	 * language functions and symbols.
	 */
	public Environment() {
		bindings = new ArrayList<>();
		bindings.add(new HashMap<>());
		new Lang().provideBindings(this);
		new Logic().provideBindings(this);
		new Math().provideBindings(this);
		new Util().provideBindings(this);
		packageCount++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void register(final Bindable binding) {
		final Map<String, Bindable> core = bindings.get(CORE_ENV_INDEX);

		addBinding(binding.name(), binding, core);

		for (final String synonym: binding.synonyms() ) {
			addBinding(synonym, binding, core);
		}
	}

	/**
	 * Creates a new environment scope, to manage bindings for a new function invocation.
	 */
	public void startScope() {
		bindings.add(new HashMap<>());
		scopeCount++;
	}

	/**
	 * Ends the most recently started environment scope, typically to release bindings after
	 * a completed function invocation.
	 */
	public void endScope() {
		if (scopeCount <= 0) {
			throw new EvaluationException("Scope index underflow");
		}

		bindings.remove(bindings.size() - 1);
		scopeCount--;
    }

	/**
	 * Adds the named binding to the given bindings map, and warns if an existing definition
	 * is being overwritten.
	 *
	 * @param name The programmatic name to associate with the binding.
	 * @param bindable The {@link Bindable} object to be bound.
	 * @param bindings The map to which the binding should be added.
	 */
	private void addBinding(final String name,
							final Bindable bindable,
							final Map<String, Bindable> bindings) {
		if (bindings.put(name.toLowerCase(), bindable) != null) {
			System.err.println("WARNING: Binding for '" + name + "' overwritten");
		}
	}

	/**
	 * Adds the named binding to the most recently started environment scope.
     *
     * @param name The programmatic name to associate with the binding.
     * @param bindable The {@link Bindable} object to be bound.
	 * @throws EvaluationException If there is no active scope to add the binding to, or the
	 *         name is already defined in 'core' or another package.
	 */
	public void addBinding(final String name, final Bindable bindable) {
		if (scopeCount <= 0) {
			throw new EvaluationException("No  active scope to add binding '" + name + "' to");
		}

		if (isDefined(name, packageCount - 1)) {
            throw new EvaluationException("Binding '" + name + "' already defined");
        }

		bindings.get(bindings.size() - 1).put(name.toLowerCase(), bindable);
	}

	/**
	 * Indicates if the given name has a valid binding in the current environment.
	 *
	 * @param name A function, variable or symbol name.
     * @return True if the given name has a valid binding, false otherwise.
	 */
	public boolean isDefined(final String name) {
		return getBinding(name) != null;
	}

	/**
	 * Indicates if the given name has a valid binding in the current environment. The name
	 * search starts at the given {@code startIndex}. This is primarily to search for names
	 * defined in either the core or other loaded packages.
	 *
	 * @param name A function, variable or symbol name.
	 * @return True if the given name has a valid binding, false otherwise.
	 */
	private boolean isDefined(final String name, final int startIndex) {
		return getBinding(name, startIndex) != null;
	}

	/**
	 * Returns the binding for the given name, in the current environment.
     *
     * @param name A function, variable or symbol name.
     * @return The current binding for the given name.
     */
    public Bindable getBinding(final String name) {
        return getBinding(name, bindings.size() - 1);
    }

	/**
	 * Returns the binding for the given name, in the current environment. The name search
	 * starts at the given {@code startIndex} and proceeds back to the "core" package.
	 * This is primarily to search for names defined in either the core or other loaded
	 * packages, that can't be overwritten by dynamically scoped names.
	 *
	 * @param name A function, variable or symbol name.
	 * @return The current binding for the given name.
	 */
	private Bindable getBinding(final String name, final int startIndex) {
		final String bindingName = name.toLowerCase();

		for (int i = startIndex; i >= 0; i--) {
			final Map<String, Bindable> binding = bindings.get(i);
			if (binding.containsKey(bindingName)) {
				return binding.get(bindingName);
			}
		}

		return null;
	}
}
