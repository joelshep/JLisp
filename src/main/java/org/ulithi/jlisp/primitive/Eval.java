package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Bindable;
import org.ulithi.jlisp.core.Environment;
import org.ulithi.jlisp.core.Function;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.core.Symbol;
import org.ulithi.jlisp.exception.UndefinedSymbolException;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.Ref;

import java.util.Optional;

/**
 * Implements the LISP {@code eval} function. The {@code eval} function accepts a "form" -- a list
 * whose first element is a symbol that identifies an operator or function -- and evaluates it
 * according the LISP language semantics, returning the result as a {@link Ref}.
 */
public class Eval {

    /** Function, variable and other bindings for this eval instance. */
    private final Environment env = new Environment();

    /**
     * Given a "form" as an {@link SExpression}, evaluates the form and returns the result.
     *
     * @param sexp An {@code SExpression} representing the LISP form to evaluate.
     * @return The resulting value of the evaluation.
     */
    public SExpression apply(final SExpression sexp) {
        return sexp.isAtom() ? apply(Cell.create(sexp.toAtom())) : apply(sexp.toList().getRoot());
    }

    /**
     * Given the root {@link Cell} of a parsed JLISP expression, evaluates the expression and
     * returns the result.
     *
     * @param cell The root {@link Cell} of the parsed expression to evaluate.
     * @return The resulting value of the evaluation.
     */
    public SExpression apply(final Cell cell) {
        if ( cell.isNil() ) { return List.create(); }

        // Get the root cell's first element as an s-expression.
        final SExpression car = SExpression.fromRef(cell.getFirst());

        // If the first element is a list, recursively evaluate it and return the result.
        if (car.isList()) { return apply((Cell) cell.getFirst()); }

        // If car is a number, return it.
        final Atom atom = car.toAtom();

        if (atom.isNumber()) { return car; }

        // See if car is a defined function. If so, we'll use it below.
        final String lexeme = atom.toS();

        // If the lexeme resolves to a function, evaluate the function, otherwise try
        // to evaluate it as a symbol or a literal.
        return resolveFunction(lexeme)
                .map(function -> evaluateFunction(function, cell.getRest()))
                .orElseGet(() -> evaluateSymbolOrLiteral(atom));
    }

    /**
     * Attempts to resolve the binding in the current environment for the given name as a function.
     *
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
     * Invokes the given Function on the argument(s) referred to by {@code rest}. If the function
     * is "special", the argument(s) are passed directly to the function without evaluation.
     * Otherwise, the argument(s) are evaluated recursively, and the function is invoked on the
     * fully evaluated arguments.
     *
     * @param func The function to invoke.
     * @param rest A Ref to the arguments for the function, typically a list.
     * @return The resulting value of the evaluation.
     */
    private SExpression evaluateFunction(final Function func, final Ref rest) {
        if (func.isSpecial()) {
            return invokeFunction(func, SExpression.fromRef(rest), env);
        }

        final List args = evaluateArgs(rest);

        return invokeFunction(func, args, env);
    }

    /**
     * Assumes the given Ref is a List of arguments to a function. Iterates over the list,
     * recursively evaluates each element and accumulates the results.
     * @param rest A Ref to a list of arguments to a LISP function.
     * @return A List of the evaluated arguments.
     */
    private List evaluateArgs(final Ref rest) {
        Ref it = rest;
        final List args = List.create();

        while (!it.isNil()) {
            final Ref intermediate = apply((Cell) it);
            if (intermediate.isAtom()) {
                args.add(intermediate.toAtom());
            } else if (intermediate.isList()) {
                args.add(intermediate.toList());
            }
            it = ((Cell)it).getRest();
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
                    if (atom.isLiteral()) {
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
