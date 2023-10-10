package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Bindable;
import org.ulithi.jlisp.core.Environment;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.JLispRuntimeException;
import org.ulithi.jlisp.exception.UndefinedSymbolException;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.Ref;

/**
 * Implements the LISP {@code eval} function. The {@code eval} function accepts a "form" -- a list
 * whose first element is a symbol that identifies an operator or function -- and evaluates it
 * according the LISP language semantics, returning the result as an s-expression.
 * <p>
 * This is currently a very crude albeit functional implementation, that can only handle simple
 * arithmetic expressions. It does, however, correctly implement recursive evaluation.
 */
public class Eval {

    /** Function, variable and other bindings for this eval instance. */
    private final Environment env = new Environment();

    /**
     * Given a "form" as an {@link SExpression}, evaluates the form and returns the result.
     *
     * @param sexp An {@code SExpression} representing the LISP form to evaliate.
     * @return The resulting value of the evaluation.
     */
    public SExpression apply(final SExpression sexp) {
        return apply(sexp.toList().getRoot());
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
        final SExpression car = cellToCar(cell);

        // If the referee (the thing being referred to) is a list, recursively evaluate it
        // and return the result.
        if (car.isList()) { return apply((Cell) cell.getFirst()); }

        // If the referee is a number, return it.
        if (car.isAtom() && ((Atom)car).isNumber()) { return car; }

        final String lexeme = car.toString();

        // See if the car referee is a defined function. If so, we'll use it below.
        final Function func = resolveIfFunction(lexeme);

        // But if not then finally check to see if it's a literal: return it if it
        // is and otherwise throw because we don't know what to do.
        if (func == null) {
            // If the car referee is a bound symbol, return its value.
            SExpression val = resolveIfSymbol(lexeme);
            if (val != null) { return val; }

            val = resolveIfVariable(lexeme);
            if (val != null) { return val; }

            if (car.isAtom() && (car.toAtom()).isLiteral()) { return car; }
            throw new UndefinedSymbolException("Unknown symbol: " + car);
        }

        Ref rest = cell.getRest();

        if (func.isSpecial()) {
            return invokeFunction(func, SExpression.create(rest), env);
        } else {
            // Iterate over 'rest', evaluate each element, accumulate the results in a
            // sexpr/cell/list, and then invoke the function at the end.
            final List args = List.create();

            while (!rest.isNil()) {
                SExpression intermediate = apply((Cell) rest);
                if (intermediate.isAtom()) {
                    args.add(intermediate.toAtom());
                } else if (intermediate.isList()) {
                    args.add(intermediate.toList());
                }
                rest = ((Cell) rest).getRest();
            }

            return invokeFunction(func, args, env);
        }
    }

    /**
     * Attempts to resolve the binding in the current environment for the given name as a function.
     *
     * @param name The programmatic name to resolve.
     * @return Returns the function bound to the given name or null if a binding doesn't exist or
     *         is not a function binding.
     */
    private Function resolveIfFunction(final String name) {
        final Bindable binding = env.getBinding(name);

        if ((binding instanceof Function)) {
            return (Function) binding;
        }

        return null;
    }

    /**
     * Attempts to resolve the binding in the current environment for the given name as a symbol.
     *
     * @param name The programmatic name to resolve.
     * @return Returns the value of bound via a symbol definition to the given name or null if a
     *         binding doesn't exist or is not a symbol binding.
     */
    private SExpression resolveIfSymbol(final String name) {
        final Bindable binding = env.getBinding(name);

        if ((binding instanceof Symbol)) {
            return ((Symbol) binding).eval();
        }

        return null;
    }

    /**
     * Attempts to resolve the binding in the current environment for the given name as a variable.
     *
     * @param name The programmatic name to resolve.
     * @return Returns the value bound to the given name or null if a binding doesn't exist or
     *         is not a variable binding.
     */
    private SExpression resolveIfVariable(final String name) {
        final Bindable binding = env.getBinding(name);

        if (binding instanceof SExpression) {
            return (SExpression) binding;
        }

        return null;
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
            if (func.isPrimitive()) { return func.needsEnv() ? func.apply(args, env) : func.apply(args); };
            return apply(func.apply(args, env));
        } finally {
            env.endScope();
        }
    }

    /**
     * Given a Cell, returns an s-expression represent the referee of the cell's first element.
     * @param cell A Cell from a parse tree.
     * @return The referee of the cell's first element, as an s-expression.
     */
    private static SExpression cellToCar(final Cell cell) {
        final Ref ref = cell.getFirst();
        // Deal with the empty list first, maybe returning ... an empty list?!
        if (ref.isNil()) { return List.create(); }
        if (ref.isAtom()) { return Atom.create(ref); }
        if (ref.isCell()) { return List.create(cell.getFirst()); }
        throw new JLispRuntimeException("Don't know how to fetch CAR of ref: " + ref);
    }
}
