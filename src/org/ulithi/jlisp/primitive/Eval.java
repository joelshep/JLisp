package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.exception.JLispRuntimeException;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.Ref;
import org.ulithi.jlisp.core.SExpression;

/**
 * Implements the LISP {@code eval} function. The {@code eval} function accepts a "form" -- a list
 * whose first element is a symbol that identifies an operator or function -- and evaluates it
 * according the LISP language semantics, returning the result as an s-expression. TODO - I think.
 * <p>
 * This is currently a very crude albeit functional implementation, that can only handle simple
 * arithmetic expressions. It does, however, correctly implement recursive evaluation.
 */
public class Eval {

    /**
     * Given the root {@link Cell} of a parsed JLISP expression, evaluates the expression and
     * returns the result.
     * <p>
     * TODO - The result should, I believe, be an s-expression, but for now we're just dealing
     * with ints.
     *
     * @param cell The root {@link Cell} of the parsed expression to evaluate.
     * @return The resulting value of the evaluation.
     */
    public int apply(final Cell cell) {
        if ( cell.isNil() ) { return 0; } // TODO - Is this an error?

        // Get the root cell's first element as an s-expression.
        final SExpression car = refToCar(cell);

        // If the referee (the thing being referred to) is a list, recursively evaluate it
        // and return the result.
        if (car.isList()) { return apply((Cell) cell.getFirst()); }

        // If the referee is an atom, evaluate it and return its value.
        if (car.isAtom()) {
            final Atom atom = (Atom)car;
            if (atom.isNumber()) { return atom.toI(); }
            // if (atom.isLiteral()) { return atom.toS(); }
        }

        // If the referee is neither an atom nor a list, then it must be a function.
        // Get its symbol.
        final String func = car.toString();

        Integer ret = null;

        // Iterate over the function parameters, contained in the cell's "rest" reference,
        // applying the function to each.
        Ref curr = cell.getRest();

        while (!curr.isNil()) {
            ret = (ret == null ? apply((Cell)curr) : eval_func(func, ret, apply((Cell)curr)));
            curr = ((Cell) curr).getRest();
        }

        return ret;
    }

    /**
     * Given a Cell, returns an s-expression represent the referee of the cell's first element.
     * @param cell A Cell from a parse tree.
     * @return The referee of the cell's first element, as an s-expression.
     */
    private static SExpression refToCar(final Cell cell) {
        final Ref ref = cell.getFirst();
        if (ref.isAtom()) { return (Atom)ref; }
        if (ref.isCell()) { return List.create(cell); }
        throw new JLispRuntimeException("Don't know how to fetch CAR of ref: " + ref);
    }

    /**
     * Applies the named function to the given pair of arguments.
     *
     * @param func The name/symbol of the function to apply.
     * @param lhs The first argument to the function.
     * @param rhs The second argument to the function.
     * @return The function result.
     */
    private int eval_func(final String func, final int lhs, final int rhs) {
        if (func.equals("+")) { return lhs + rhs; }
        if (func.equals("*")) {return lhs * rhs; }
        throw new JLispRuntimeException("Unknown operator " + func);
    }
}
