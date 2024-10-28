package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.parser.Grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A small assortment of static routines to support (primarily) user-defined functions
 * and lambdas.
 */
public class FunctionUtils {

    /** Do not construct. */
    private FunctionUtils() { }

    /**
     * Transforms the given SExpression representing a list of formal parameters, to a Java list
     * of strings with those same formal parameters, preserving order.
     *
     * @param formals An SExpression representing a (possibly empty) list of formal function
     *                parameters.
     * @return An ordered list of the formals' names.
     */
    static List<String> parseFormals(final SExpression formals) {
        if (formals.isNil()) {
            return Collections.emptyList();
        }

        org.ulithi.jlisp.core.List it = formals.toList();
        final List<String> params = new ArrayList<>(it.lengthAsInt());

        addFormal(params, it.car().toAtom().toS());

        while (!it.endp()) {
            it = it.cdr().toList();
            addFormal(params, it.car().toAtom().toS());
        }

        return params;
    }

    /**
     * Binds the given function invocation arguments to the function's formal parameters and
     * returns the bindings as a {@code Map}.
     *
     * @param args The function arguments list as prepared by {@code Eval}.
     * @param formals The formal parameters for a function.
     * @return A map of the function's formal parameter names to the arguments for this
     *         specific invocation.
     */
    static Map<String, SExpression> bindFormals(final SExpression args, List<String> formals) throws EvaluationException {
        org.ulithi.jlisp.core.List it = args.toList();
        final int argCount = it.lengthAsInt();

        if (argCount != formals.size()) {
            throw new EvaluationException("Expected " + formals.size() +
                                          " arguments: got " + it.lengthAsInt());
        }

        if (it.isEmpty()) { return Collections.emptyMap(); }

        final Map<String, SExpression> context = new HashMap<>(it.lengthAsInt());

        final Iterator<String> formalIterator = formals.iterator();

        while (!it.isNil()) {
            if (!formalIterator.hasNext()) {
                throw new EvaluationException("More arguments than formal parameters");
            }
            context.put(formalIterator.next(), it.car());
            it = it.cdr().toList();
        }

        if (formalIterator.hasNext()) {
            throw new EvaluationException("More formal parameters than arguments");
        }

        return context;
    }

    /**
     * Validates the given formal name, and then appends it to the given list.
     * @param params A partial list of formal parameters defined for a function.
     * @param formal A formal parameter to add to the list.
     */
    private static void addFormal(final List<String> params, final String formal) {
        if (!Grammar.isFunctionName(formal)) {
            throw new EvaluationException("'" + formal + "' is not a legal parameter name");
        }

        if (params.contains(formal)) {
            throw new EvaluationException("Duplicate parameter name: " + formal);
        }

        params.add(formal);
    }
}
