package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.JLispRuntimeException;
import org.ulithi.jlisp.exception.UndefinedSymbolException;
import org.ulithi.jlisp.mem.Cell;
import org.ulithi.jlisp.mem.Ref;

import java.util.HashMap;
import java.util.Map;

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
     * This is probably short-term. For now, it is a map from primitive function names/symbols
     * to their implementation.
     */
    private final Map<String, Function> primitives = new HashMap<>();

    public Eval() {
        primitives.put("CAR", new CAR());
        primitives.put("CDR", new CDR());
        primitives.put("PLUS", new PLUS());
        primitives.put("TIMES", new TIMES());
        primitives.put("+", new PLUS());
        primitives.put("*", new TIMES());
    }

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
    public SExpression apply(final Cell cell) {
        if ( cell.isNil() ) { return Atom.NIL; }

        // Get the root cell's first element as an s-expression.
        final SExpression car = refToCar(cell);

        // If the referee (the thing being referred to) is a list, recursively evaluate it
        // and return the result.
        if (car.isList()) { return apply((Cell) cell.getFirst()); }

        // If the referee is a number, return it.
        if (car.isAtom() && ((Atom)car).isNumber()) { return car; }

        // See if the car referee is a defined function. If so, we'll use it below,
        // but if not then finally check to see if it's a literal: return it if it
        // is and otherwise throw because we don't know what to do.
        final Function func = primitives.get(car.toString());

        if (func == null) {
            if (cell.isAtom() && ((Atom) car).isLiteral()) {
                return car;
            }

            throw new UndefinedSymbolException("Unknown symbol: " + car);
        }

        // Iterate over 'rest', evaluate each element, accumulate the results in a
        // sexpr/cell/list, and then invoke the function at the end.
        final List args = List.create();

        Ref curr = cell.getRest();

        while (!curr.isNil()) {
            SExpression intermediate = apply((Cell)curr);
            if (intermediate.isAtom()) { args.add(intermediate.toAtom()); }
            if (intermediate.isList()) { args.add(intermediate.toList()); }
            curr = ((Cell) curr).getRest();
        }

        return func.apply(args);
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
}
