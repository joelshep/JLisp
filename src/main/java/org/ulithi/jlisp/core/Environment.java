package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.primitive.Collections;
import org.ulithi.jlisp.primitive.Lang;
import org.ulithi.jlisp.primitive.Logic;
import org.ulithi.jlisp.primitive.Math;
import org.ulithi.jlisp.primitive.Predicate;
import org.ulithi.jlisp.primitive.Util;
import org.ulithi.jlisp.test.UnitTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The JLISP {@link Environment}. An {@code Environment} is a sequence of "frames", where a frame
 * is either a "package" or a "scope". Both are collections of function and symbol bindings, but
 * packages are long-lived while a scope only exists for the lifetime of a single function invocation.
 * The {@code Environment} is initialized with a core frame/package of built-in LISP functions and
 * symbols, and an empty package for user-defined functions and symbols with global scope. A
 * function invocation creates a "scope" that is effective over the lifetime of the invocation,
 * where new dynamically-scoped bindings (e.g. variables) can be created. The scope is released
 * when the function returns.
 */
public final class Environment implements BindingRegistrar {

    /**
     * The index of the "core" frame/package in the "frames" list.
     */
    private static final int CORE_FRAME_INDEX = 0;

    /**
     * The index of the (initially empty) "user" frame/package in the "frames" list.
     */
    private static final int USER_FRAME_INDEX = 1;

    /**
     * The number of packages (whose symbols can't be redefined) currently loaded in this
     * environment, including the built-in "core" package. At this time, packages must be
     * created (not necessarily with bindings as well) when the environment is first initialized:
     * after initialization, the package count doesn't change.
     */
    private final int packageCount;

    /**
     * The number of function-specific scopes in this environment. In general, the index of the
     * most recently started function-specific scope will be at:
     *    packageCount + scopeCount - 1
     */
    private int scopeCount = 0;

    /**
     * The environment frames. The "core" language package is the first frame in the list,
     * followed by user-defined bindings, followed by dynamically scoped bindings.
     */
    private final List<Map<String, Bindable>> frames;

    /**
     * Initializes the {@code environment}, including creating a frame for core (built-in)
     * language functions and symbols, and an empty frame for user-defined bindings.
     */
    public Environment() {
        frames = new ArrayList<>();
        frames.add(new HashMap<>());
        new Collections().provideBindings(this);
        new Lang().provideBindings(this);
        new Logic().provideBindings(this);
        new Math().provideBindings(this);
        new Predicate().provideBindings(this);
        new UnitTest().provideBindings(this);
        new Util().provideBindings(this);

        // Add the "user" frame.
        frames.add(new HashMap<>());

        packageCount = frames.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(final Binding binding) {
        final Map<String, Bindable> core = frames.get(CORE_FRAME_INDEX);

        registerBinding(binding.name(), binding.bindable(), core);

        for (final String synonym: binding.synonyms() ) {
            registerBinding(synonym, binding.bindable(), core);
        }
    }

    /**
     * Adds the named {@link Bindable} (a function or symbol) to the most recently started
     * dynamic scope.
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

        if (!canDefine(name)) {
            throw new EvaluationException("Binding '" + name + "' already defined");
        }

        frames.get(frames.size() - 1).put(name.toLowerCase(), bindable);
    }

    /**
     * Adds or updates the given {@link Binding} as a user-defined function or symbol.
     * @param binding The named {@link Bindable} to add as a user-defined function or symbol.
     * @throws EvaluationException If the name is already defined in the 'core' package.
     */
    public void addUserBinding(final Binding binding) {
        final String name = binding.name();

        if (isCoreBinding(name)) {
            throw new EvaluationException("Binding '" + name + "' already defined");
        }

        // TODO Can a name be rebound to a different binding type (e.g. symbol rebound to function)?
        frames.get(USER_FRAME_INDEX).put(name.toLowerCase(), binding.bindable());
    }

    /**
     * Creates a frame/scope, to manage bindings for a new function invocation.
     */
    public void startScope() {
        frames.add(new HashMap<>());
        scopeCount++;
    }

    /**
     * Ends the most recently started frame/scope, typically to release bindings after
     * a completed function invocation.
     */
    public void endScope() {
        if (scopeCount <= 0) {
            throw new EvaluationException("Scope index underflow");
        }

        frames.remove(frames.size() - 1);
        scopeCount--;
    }

    /**
     * Registers the named binding in the given frame, and warns if an existing definition
     * is being overwritten.
     *
     * @param name The programmatic name to associate with the binding.
     * @param bindable The {@link Bindable} object to be bound.
     * @param frame The frame to which the binding should be added.
     */
    private void registerBinding(final String name,
                                 final Bindable bindable,
                                 final Map<String, Bindable> frame) {
        if (frame.put(name.toLowerCase(), bindable) != null) {
            System.err.println("WARNING: Binding for '" + name + "' overwritten");
        }
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
     * Returns the binding for the given name, in the current environment.
     *
     * @param name A function, variable or symbol name.
     * @return The current binding for the given name.
     */
    public Bindable getBinding(final String name) {
        return getBinding(name, frames.size() - 1);
    }

    /**
     * Returns the binding for the given name, in the current environment. The name search
     * starts at the frame denoted by the given {@code startIndex} and proceeds back to the
     * "core" package. This is primarily to search for names defined in either the core or
     * other loaded packages, that can't be overwritten by dynamically-scoped names.
     *
     * @param name A function, variable or symbol name.
     * @return The current binding for the given name.
     */
    private Bindable getBinding(final String name, final int startIndex) {
        final String bindingName = name.toLowerCase();

        for (int i = startIndex; i >= 0; i--) {
            final Map<String, Bindable> frame = frames.get(i);
            if (frame.containsKey(bindingName)) {
                return frame.get(bindingName);
            }
        }

        return null;
    }

    /**
     * Indicates if the given name can be defined (or redefined) in the current environment. If
     * the name is not in the "core" or user-defined packages, then it can be defined/redefined
     * in a dynamic scope and this method returns true. Otherwise, if the name is already defined
     * in the core or user-defined packages, then it can't be redefined and this method returns
     * false.
     *
     * @param name A function, variable or symbol name.
     * @return True if the given name can be defined/redefined, false otherwise.
     */
    private boolean canDefine(final String name) {
        return getBinding(name, packageCount - 1) == null;
    }

    /**
     * Indicates if the given name is defined in the core package.
     * @param name A function, variable or symbol name.
     * @return True if the given name is defined in the core package, false otherwise.
     */
    private boolean isCoreBinding(final String name) {
        return frames.get(CORE_FRAME_INDEX).containsKey(name);
    }
}
