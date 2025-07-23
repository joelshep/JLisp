package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.JLispRuntimeException;
import org.ulithi.jlisp.exception.SyntaxException;
import org.ulithi.jlisp.parser.Grammar;
import org.ulithi.jlisp.primitive.Collections;
import org.ulithi.jlisp.primitive.IO;
import org.ulithi.jlisp.primitive.Lang;
import org.ulithi.jlisp.primitive.Logic;
import org.ulithi.jlisp.primitive.Math;
import org.ulithi.jlisp.primitive.Predicate;
import org.ulithi.jlisp.primitive.Util;
import org.ulithi.jlisp.test.Expect;

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
 * list-type form evaluation creates a "scope" that is effective over the lifetime of the evaluation,
 * where new dynamically-scoped bindings (e.g. variables) can be created. The scope is released
 * when the evaluation of the related form ends.
 */
public final class Environment implements BindingRegistrar {

    /** The index of the "core" frame/package in the "frames" list. */
    private static final int CORE_FRAME_INDEX = 0;

    /** The index of the (initially empty) "user" frame/package in the "frames" list. */
    private static final int USER_FRAME_INDEX = 1;

    /**
     * The number of form-specific scopes in this environment. In general, the index of the most
     * recently started form-specific scope will be at:
     *    packageCount + scopeCount - 1
     */
    private int scopeCount = 0;

    /**
     * The environment frames. The "core" language package is the first frame in the list,
     * followed by user-defined bindings, followed by dynamically scoped bindings.
     */
    private final List<Map<String, Bindable>> frames;

    /**
     * Macros. Macro definitions are considered "global" in scope: there is no framing, etc.,
     * of macros unlike other symbols.
     */
    private final Map<String, Macro> macros = new HashMap<>();

    /**
     * Initializes the {@code environment}, including creating a frame for core (built-in)
     * language functions and symbols, and an empty frame for user-defined bindings.
     */
    public Environment() {
        frames = new ArrayList<>();
        frames.add(new HashMap<>());
        new Collections().provideBindings(this);
        new Expect().provideBindings(this);
        new IO().provideBindings(this);
        new Lang().provideBindings(this);
        new Logic().provideBindings(this);
        new Math().provideBindings(this);
        new Predicate().provideBindings(this);
        new Util().provideBindings(this);

        // Add the "user" frame.
        frames.add(new HashMap<>());
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
     * Removes user-defined functions and symbols from the environment, <em>except those defined in
     * the current scope.</em>. This is primarily used to support {@code EXPECT}-based unit tests,
     * and the current scope is retained to allow it to be properly ended when the function invoking
     * this method completes its evaluation.
     */
    public void resetFromCurrentScope() {
        // Clear user-defined macros ...
        macros.clear();
        // Clear global symbol and function bindings ...
        frames.get(USER_FRAME_INDEX).clear();
        // Remove all frames except the current frame.
        if (frames.size() > USER_FRAME_INDEX+1) {
            frames.subList(USER_FRAME_INDEX + 1, frames.size() - 1).clear();
        }

        scopeCount = frames.size()-2;
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
     * Adds the named {@link Bindable} (a function or symbol) to the most recently started
     * dynamic scope. This is typically used for binding formal parameters for functions,
     * lambdas and macros, as well as LET bindings, where the scope for newly-defined symbols
     * is dynamic and has a lifetime equal to the function/lambda/macro evaluation or lexical
     * scope of the LET function.
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

        if (!Grammar.isFunctionName(name)) {
            throw new SyntaxException("'" + name + "' is an invalid function or variable name");
        }

        frames.get(frames.size() - 1).put(name.toLowerCase(), bindable);
    }

    /**
     * Adds or updates the given {@link Binding} as a user-defined function or symbol. This is
     * typically used for DEFUN and SETQ bindings, where the scope for newly defined symbols is
     * global.
     * @param binding The named {@link Bindable} to add as a user-defined function or symbol.
     * @throws EvaluationException If the name is already defined in the 'core' package.
     */
    public void addUserBinding(final Binding binding) {
        final String name = binding.name().toLowerCase();

        if (isCoreBinding(name)) {
            throw new EvaluationException("Binding '" + name + "' already defined");
        }

        if (!Grammar.isFunctionName(name)) {
            throw new SyntaxException("'" + name + "' is an invalid function or variable name");
        }

        // If there is not already a binding for this name, then create a binding with global
        // scope. Otherwise, update the binding in the scope it is already defined in.
        if (getUserBinding(name, frames.size() - 1) == null) {
            // TODO Can a name be rebound to a different binding type (e.g. symbol rebound to function)?
            frames.get(USER_FRAME_INDEX).put(name, binding.bindable());
        } else {
            updateUserBinding(name, binding);
        }
    }

    /**
     * Updates an existing, user-defined binding. This method is only used to update existing
     * bindings: it is an error to call it in order to define new user-bindings.
     *
     * @param bindingName The name/symbol of the binding to be updated.
     * @param binding The new binding for the name/symbol.
     * @throws EvaluationException If the named binding isn't already a user-defined binding.
     */
    private void updateUserBinding(final String bindingName, final Binding binding) {
        for (int i = frames.size() - 1; i > CORE_FRAME_INDEX; i--) {
            final Map<String, Bindable> frame = frames.get(i);
            if (frame.containsKey(bindingName)) {
                frame.put(bindingName, binding.bindable());
                return;
            }
        }

        throw new JLispRuntimeException("Expected binding for name '" + bindingName + "' but none found");
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
        return getBinding(name) != null || getMacro(name) != null;
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
     * "core" package.
     *
     * @param name A function, variable or symbol name.
     * @param startIndex The frame index where the search should start. Note that the search
     *                   iterates backwards through the frames list.
     * @return The current binding for the given name, or null if no binding is defined.
     */
    private Bindable getBinding(final String name, final int startIndex) {
        final String bindingName = name.toLowerCase();

        final Bindable binding = getUserBinding(bindingName, startIndex);

        if (binding != null) {
            return binding;
        }

        return frames.get(CORE_FRAME_INDEX).get(bindingName);
    }

    /**
     * Returns the binding for the given name, in the user-defined frames in the current environemnt.
     * The search starts at the frame denoted by the given {@code startIndex} and proceeds back to
     * the "user" package.
     * @param bindingName A function, variable or symbol name.
     * @param startIndex The frame index where the search should start. Note that the search
     *                   iterates backwards through the frames list.
     * @return The current binding for the given name, or null if no binding is defined.
     */
    private Bindable getUserBinding(final String bindingName, final int startIndex) {
        for (int i = startIndex; i > CORE_FRAME_INDEX; i--) {
            final Map<String, Bindable> frame = frames.get(i);
            if (frame.containsKey(bindingName)) {
                return frame.get(bindingName);
            }
        }

        return null;
    }

    /**
     * Indicates if the given name can be defined (or redefined) in the current environment. If
     * the name is not in the "core" package, then it can be defined/redefined in a dynamic scope
     * and this method returns true. Otherwise, if the name is already defined in the core package,
     * then it can't be redefined and this method returns false.
     *
     * @param name A function, variable or symbol name.
     * @return True if the given name can be defined/redefined, false otherwise.
     */
    private boolean canDefine(final String name) {
        return getBinding(name, CORE_FRAME_INDEX) == null;
    }

    /**
     * Indicates if the given name is defined in the core package.
     * @param name A function, variable or symbol name.
     * @return True if the given name is defined in the core package, false otherwise.
     */
    private boolean isCoreBinding(final String name) {
        return frames.get(CORE_FRAME_INDEX).containsKey(name);
    }

    /**
     * Adds a macro definition with the given name to the global environment.
     * @param name A macro name.
     * @param macro The macro to bind to the name.
     */
    public void addMacro(final String name, final Macro macro) {
        macros.put(name.toLowerCase(), macro);
    }

    /**
     * Indicates if the given name is bound to a macro in the global environment.
     * @param name A macro name.
     * @return True if the given name has a valid macro binding, false otherwise.
     */
    public boolean isMacro(final String name) {
        return macros.containsKey(name.toLowerCase());
    }

    /**
     * Returns the macro binding for the given name, in the global environment.
     * @param name A macro name.
     * @return The macro currently bound to the given name. May be null.
     */
    public Macro getMacro(final String name) {
        return macros.get(name.toLowerCase());
    }
}
