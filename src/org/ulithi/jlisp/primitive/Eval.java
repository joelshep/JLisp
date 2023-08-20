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
 * according the LISP language semantics, returning the result as an s-expression.
 * <p>
 * This is currently a very crude albeit functional implementation, that can only handle simple
 * arithmetic expressions. It does, however, correctly implement recursive evaluation.
 */
public class Eval implements FunctionRegistrar {

    /**
     * This is probably short-term. For now, it is a map from primitive function names/symbols
     * to their implementation.
     */
    private final Map<String, Function> primitives = new HashMap<>();

    /**
     * Adds the named function to the primitives map, and warms if a function definition
     * is being overwritten.
     * @param name The programmatic name to associate with the function.
     * @param function The function.
     */
    private void putFunction(final String name, final Function function) {
        if (primitives.put(name, function) != null) {
            System.err.println("WARNING: Function '" + name + "' definition overwritten");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerFunction(final Function function) {
        putFunction(function.name(), function);

        for (final String synonym: function.synonyms() ) {
            putFunction(synonym, function);
        }
    }

    /**
     * Constructs a new instance of {@link Eval}, and registers system-defined functions.
     */
    public Eval() {
        new Lang().provideFunctions(this);
        new Math().provideFunctions(this);
        new Util().provideFunctions(this);
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

        // See if the car referee is a defined function. If so, we'll use it below,
        // but if not then finally check to see if it's a literal: return it if it
        // is and otherwise throw because we don't know what to do.
        final Function func = primitives.get(car.toString());

        if (func == null) {
            if (car.isAtom() && (car.toAtom()).isLiteral()) { return car; }
            throw new UndefinedSymbolException("Unknown symbol: " + car);
        }

        Ref rest = cell.getRest();

        if (func.isSpecial()) {
            return func.apply(SExpression.create(rest));
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

            return func.apply(args);
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
        if (ref.isCell()) { return List.create(cell); }
        throw new JLispRuntimeException("Don't know how to fetch CAR of ref: " + ref);
    }
}
