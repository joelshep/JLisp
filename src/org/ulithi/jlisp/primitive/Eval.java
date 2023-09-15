package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
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

        // See if the car referee is a defined function. If so, we'll use it below,
        // but if not then finally check to see if it's a literal: return it if it
        // is and otherwise throw because we don't know what to do.
        final Function func = (Function) env.getBinding(car.toString());

        if (func == null) {
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
     * Invokes the given function on the specified arguments, using bindings in the current
     * environment plus any bindings created by the function itself.
     *
     * @param func The function to be evaluated.
     * @param args The arguments to the function.
     * @param env The current environment.
     * @return The result of applying the function to the arguments.
     */
    private static SExpression invokeFunction(final Function func,
                                              final SExpression args,
                                              final Environment env) {
        env.startScope();

        try {
            return func.apply(args);
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
