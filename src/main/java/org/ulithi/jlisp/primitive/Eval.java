package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Bindable;
import org.ulithi.jlisp.core.Environment;
import org.ulithi.jlisp.core.Function;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.Macro;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.core.Symbol;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.UndefinedSymbolException;
import org.ulithi.jlisp.mem.Ref;

import java.util.Optional;

/**
 * Implements the LISP {@code eval} function. The {@code eval} function accepts a "form" -- an
 * {@link SExpression} representing either an {@code Atom} or a list whose first element is a symbol
 * that identifies an operator or function -- and evaluates it according the LISP language semantics,
 * returning the result as an {@link SExpression}.
 */
public class Eval {

    /** Function, variable and other bindings for this eval instance. */
    private final Environment env;

    /**
     * Flags indicating the context in which evalImpl() is being called. The primary difference is
     * that in "arg" context, no attempt is made to apply S-expressions that evaluate to a function
     * to following arguments, while in "function" context (i.e., where the S-expression being
     * evaluated is expected to resolve to a function of some sort), it is.
     */
    private static final boolean FUNCTION_CONTEXT = false;
    private static final boolean ARG_CONTEXT = true;

    /**
     * "Standard" constructor used by the REPL. Eval owns its own {@code Environment}.
     */
    public Eval() {
        env = new Environment();
    }

    /**
     * Constructs a new {@code Eval} instance with a previously instantiated {@code Environment}.
     * This is primarily to support unit testing.
     * @param env A previously instantiated {@code Environment}.
     */
    public Eval(final Environment env) {
        this.env = env;
    }

    /**
     * Given a "form" as an {@link SExpression}, evaluates the form and returns the result.
     *
     * @param form An {@code SExpression} representing the LISP form to evaluate.
     * @return The resulting value of the evaluation, also as an {@code SExpression}.
     */
    public SExpression eval(final SExpression form) {
        return evalImpl(form, FUNCTION_CONTEXT);
    }

    /**
     * Performs the actual evaluation of the parsed JLISP form referenced by the given
     * {@link SExpression}.
     * @param form A LISP form, as an {@link SExpression}, to evaluate.
     * @param asArg If true, then S-expressions that evaluate to a function simply produce a reference
     *              to the function. Otherwise, {@code eval} continues on to apply the function to the
     *              following arguments ("normal" evaluation).
     * @return The resulting value of the evaluation as an {@code SExpression}.
     */
    private SExpression evalImpl(final SExpression form, final boolean asArg) {
        if (form.isNil()) { return List.create(); }

        if (form.isAtom()) { return evaluateSymbolOrLiteral(form.toAtom()); }

        final List list = form.toList();

        // The CAR of the list should be a function or defined macro reference.
        final SExpression car = list.car();

        // If the first element is an atom, try to resolve it as a function or macro symbol.
        if (car.isAtom()) {
            Optional<Macro> macro = resolveMacro(car.toAtom().toS());

            if (macro.isPresent()) {
                final SExpression expansion = macro.get().expand(list, env, this);
                return eval(expansion);
            }

            return resolveFunction(car.toAtom().toS())
                .map(f -> applyImpl(f, list.cdr()))
                .orElseGet(() -> {
                    final SExpression evaluated = evaluateSymbolOrLiteral(car.toAtom());
                    if (!evaluated.isFunction()) {
                        throw new EvaluationException("Expected function!");
                    }
                    return applyImpl(evaluated.toFunction(), list.cdr());
                });
        }

        // If the first element is a list, recursively evaluate it: it's expected to evaluate to
        // a function to apply to the remainder of the parsed expression.
        if (car.isList()) {
            final SExpression evaluated = eval(car.toList());
            return asArg ? evaluated : applyImpl(evaluated.toFunction(), list.cdr());
        }

        throw new EvaluationException(String.format("Unexpected car %s in form %s", car, form));
    }

    /**
     * Applies a function named or specified by {@code funcSpec} to the given arguments.
     *
     * @param funcSpec A {@link Function}, the name of a function, or a lambda expression.
     * @param args A list of arguments to the function. The arguments are assumed to have already
     *             been evaluated and ready for the specified function to consume.
     * @return The result of applying the function to the arguments.
     */
    public SExpression apply(final SExpression funcSpec, final List args) {
        final Function function = resolveFunction(funcSpec);

        if (function.isSpecial()) {
            return invokeFunction(function, SExpression.fromRef(args), env);
        }

        return invokeFunction(function, args, env);
    }

    /**
     * Applies the given Function to the argument(s) referred to by {@code rest}. If the function
     * is "special", the argument(s) are passed directly to the function without evaluation.
     * Otherwise, the argument(s) are evaluated recursively, and the function is invoked on the
     * fully evaluated arguments.
     *
     * @param func The function to invoke.
     * @param rest An {@link SExpression} representing a list of arguments to the function.
     * @return The value resulting from applying the function to the arguments.
     */
    private SExpression applyImpl(final Function func, final SExpression rest) {
        if (func.isSpecial()) {
            return invokeFunction(func, rest, env);
        }

        final List args = evaluateArgs(rest);

        return invokeFunction(func, args, env);
    }

    /**
     * Given a Function, function name, or lambda expression, returns the corresponding
     * function object.
     *
     * @param funcSpec A Function, function name or lambda expression.
     * @return A Function object corresponding the given funcSpec.
     */
    private Function resolveFunction(final SExpression funcSpec) {
        if (funcSpec.isAtom()) {
            return resolveFunction(funcSpec.toAtom().toS())
                    .orElseThrow(() -> new UndefinedSymbolException("No function named " + funcSpec + " found"));
        }

        if (funcSpec.isList()) { return (Function) eval(funcSpec); }

        if (funcSpec.isFunction()) { return (Function) funcSpec; }

        throw new UndefinedSymbolException("Undefined function " + funcSpec);
    }

    /**
     * Attempts to resolve the binding in the current environment for the given name as a function.
     * @param name The programmatic name to resolve.
     * @return Returns an optional containing the function bound to the given name, or an empty
     *         optional if a binding doesn't exist or is not a function binding.
     */
    private Optional<Function> resolveFunction(final String name) {
        return Optional.ofNullable(env.getBinding(name))
                .filter(binding -> binding instanceof Function)
                .map(binding -> (Function) binding);
    }

    /**
     * Attempts to resolve the binding in the global environment for the given name as a macro.
     * @param name The programmatic name to resolve.
     * @return Returns an optional containing the macro bound to the given name, or an empty
     *         optional if a binding doesn't exist.
     */
    private Optional<Macro> resolveMacro(final String name) {
        return Optional.ofNullable(env.getMacro(name));
    }

    /**
     * Assumes the given SExpression is a List of arguments to a function. Iterates over the list,
     * recursively evaluates each element and accumulates the results.
     * @param rest An SExpression representing a list of arguments to a LISP function.
     * @return A List of the evaluated arguments.
     */
    private List evaluateArgs(final SExpression rest) {
        Ref it = rest;
        final List args = List.create();

        while (!it.isNil()) {
            final SExpression arg = evalImpl(it.toList().car(), ARG_CONTEXT);
            args.add(arg);
            it = it.toList().cdr();
        }

        return args;
    }

    /**
     * Attempts to evaluate the given Atom as a defined symbol, or as a literal.
     * @param atom The Atom to evaluate.
     * @return If the Atom represents a symbol, the value associated with the symbol. Otherwise,
     *         if the Atom is a literal, returns the Atom itself.
     */
    private SExpression evaluateSymbolOrLiteral(final Atom atom) {
        return resolveSymbol(atom.toS())
                .orElseGet(() -> {
                    if (atom.isLiteral() || atom.isNil()) {
                        return atom;
                    }
                    throw new UndefinedSymbolException("Unknown symbol: " + atom);
                });
    }

    /**
     * Attempts to resolve the binding in the current environment for the given name as a symbol.
     *
     * @param name The programmatic name to resolve.
     * @return Returns an optional containing the value of the symbol bound to the given name,
     *         or an empty optional if a binding doesn't exist or is not a symbol binding.
     */
    private Optional<SExpression> resolveSymbol(final String name) {
        Bindable binding = env.getBinding(name);

        if (binding instanceof Symbol) {
            return Optional.of(((Symbol) binding).eval());
        }

        if (binding instanceof SExpression) {
            return Optional.of((SExpression) binding);
        }

        return Optional.empty();
    }

    /**
     * Invokes the given function on the specified arguments, using bindings in the current
     * environment plus any bindings created by the function itself.
     *
     * @param func The function to be evaluated.
     * @param args The arguments to the function.
     * @param env The current environment.
     * @return The result of applying the function to the arguments.
     */
    private SExpression invokeFunction(final Function func,
                                       final SExpression args,
                                       final Environment env) {
        env.startScope();

        try {
            if (func.isReentrant()) {
                return func.apply(args, env, this);
            } else {
                return func.isDefining() ? func.apply(args, env) : func.apply(args);
            }
        } finally {
            env.endScope();
        }
    }
}
