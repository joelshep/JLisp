package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.primitive.Eval;

import java.util.List;
import java.util.Map;

import static org.ulithi.jlisp.core.FunctionUtils.bindFormals;
import static org.ulithi.jlisp.core.FunctionUtils.parseFormals;

/**
 * Represents a user-defined macro: i.e. a macro created using the {@code DEFMACRO} procedure.
 */
public class Macro {
    /** The programmatic name of this macro. */
    private final String name;

    /** The formal parameters for this macro. **/
    private final List<String> formals;

    /** The parsed source code of the macro expansion. **/
    private final SExpression body;

    /**
     * Creates a macro with the specified name, formal parameters, and body.
     *
     * @param name The name of the macro.
     * @param formals A list of formal parameters to the macro. It may be an empty list, but
     *                otherwise must be a list of valid symbol names.
     * @param body The parsed implementation of the macro expansion.
     */
    public Macro(final String name, final SExpression formals, final SExpression body) {
        this.name = name;

        if (!formals.isList()) {
            throw new EvaluationException("Formal parameters to a macro must be a list");
        }

        if (!body.isList()) {
            throw new EvaluationException("Macro body must be a list");
        }

        this.formals = parseFormals(formals);
        this.body = body;
    }

    /**
     * Expands a macro expression in the context of {@code eval}.
     * @param form THe form containing the macro application.
     * @param environment The current runtime environment.
     * @param eval A reference to the effective {@link Eval} function.
     * @return The given form with the macro expanded.
     */
    public SExpression expand(final SExpression form, final Environment environment, final Eval eval) {
        final Map<String, SExpression> locals = bindFormals(form.toList().cdr(), formals);

        environment.startScope();

        for (final Map.Entry<String, SExpression> entry : locals.entrySet()) {
            environment.addBinding(entry.getKey(), entry.getValue());
        }

        // Replace, without further evaluation, the parameters in body with the corresponding
        // values found in locals (the bindings of the parameters to values). Then return the
        // rewritten body.
        SExpression expanded = eval.eval(body);

        environment.endScope();

        return expanded;
    }

    /**
     * @return The programmatic name of this macro.
     */
    public final String name() { return this.name; }
}
