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
 * A small assortment of static routines to support user-defined functions, lambdas and macros.
 */
public class FunctionUtils {

    /** Do not construct. */
    private FunctionUtils() { }

    /**
     * Parses and validates formal parameters during function/lambda/macro definition. This method
     * is called during the definition/compilation phase, not during evaluation.
     *
     * @param sexp An SExpression representing a (possibly empty) list of formal function
     *                parameters.
     * @return An ordered list of formal parameter names.
     * @throws EvaluationException if the formal parameter list is malformed (e.g. &rest
     *         appears multiple times or required params appear after optional).
     */
    static List<String> parseFormals(final SExpression sexp) {

        if (sexp.isNil()) { return Collections.emptyList(); }

        org.ulithi.jlisp.core.List formals = sexp.toList();
        final List<String> params = new ArrayList<>(formals.lengthAsInt());

        addFormal(params, formals.car().toAtom().toS());

        while (!formals.endp()) {
            formals = formals.cdr().toList();
            addFormal(params, formals.car().toAtom().toS());
        }

        // Now iterate over the parameters and tokens parsed from 'formals' and validate that
        // &rest and &optional keywords are used correctly.
        boolean inOptional = false;
        boolean inRest = false;

        final Iterator<String> it = params.iterator();

        while (it.hasNext()) {
            final String param = it.next();

            if (param.equals("&optional")) {
                if (inRest || inOptional) {
                    throw new EvaluationException("Invalid parameter list structure");
                }

                inOptional = true;

            } else if (param.equals("&rest")) {
                if (inRest) {
                    throw new EvaluationException("Multiple &rest parameters not allowed");
                }

                if (!it.hasNext()) {
                    throw new EvaluationException("&rest must be followed by a parameter name");
                }

                inRest = true;
            }
        }

        return params;
    }

    /**
     * Binds the given function invocation arguments to the function's formal parameters and
     * returns the bindings as a {@code Map}. This method is called during the evaluation phase when
     * a function, lambda or macro is being applied to its arguments.
     *
     * @param args The function arguments list as prepared by {@code Eval}.
     * @param formals The pre-validated list of formal parameters names.
     * @return A map of the formal parameter names to their bound arguments for this specific
     *         evaluation.
     * @throws EvaluationException if the number/type of arguments doesn't match the formal
     *         parameter specification
     */
    static Map<String, SExpression> bindFormals(final SExpression args, List<String> formals) throws EvaluationException {
        final Iterator<String> formalIterator = formals.iterator();
        boolean inOptional = false;
        String restParam = null;

        final Map<String, SExpression> context = new HashMap<>();

        org.ulithi.jlisp.core.List it = args.toList();

        // Bind required and optional parameters
        while (formalIterator.hasNext()) {
            String formal = formalIterator.next();

            // Handle parameter type markers
            if (formal.equals("&optional")) {
                inOptional = true;
                continue;
            }

            if (formal.equals("&rest")) {
                restParam = formalIterator.next();
                break;
            }

            // Bind regular parameters
            if (!inOptional) {
                if (it.isNil()) {
                    throw new EvaluationException(
                            "Too few arguments for required parameters: expected " + formals.size());
                }
                context.put(formal, it.car());
                it = it.cdr().toList();
            }

            // Bind optional parameters if arguments remain
            else if (!it.isNil()) {
                context.put(formal, it.car());
                it = it.cdr().toList();
            }

            // Optional parameters with no matching argument get nil
            else {
                context.put(formal, Atom.NIL);
            }
        }

        // Handle rest parameter if present
        if (restParam != null) {
            context.put(restParam, it.isNil() ? Atom.NIL : it);
        }

        // Error if unused arguments and no rest parameter
        else if (!it.isNil()) {
            throw new EvaluationException("Too many arguments: expected " + formals.size());
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
