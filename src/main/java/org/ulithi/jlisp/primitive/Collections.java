package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.AbstractFunction;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;

import java.util.Arrays;

/**
 * Functions for working with "collections", such as lists.
 */
public class Collections implements BindingProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<Binding> getBindings() {
        return Arrays.asList(new Binding(new Collections.APPEND()),
                             new Binding(new Collections.ASSOC()),
                             new Binding(new Collections.LENGTH()),
                             new Binding(new Collections.LIST()),
                             new Binding(new Collections.SIZE()));
    }

    /**
     * Implements the LISP {@code APPEND} function, which concatenates list arguments into a single
     * list. APPEND special-cases the case where the only argument is a single atom, returning just
     * the atom.
     */
    public static class APPEND extends AbstractFunction {
        public APPEND() { super("APPEND"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            List args = sexp.toList();

            if (args.lengthAsInt() == 1 && args.car().isAtom()) {
                return args.car().toAtom();
            } else if (args.car().isAtom()) {
                throw new EvaluationException("First argument to APPEND must be a list");
            }

            final List result = List.create();

            // Iterate over the given arguments and add them to the result list.
            while (!args.isEmpty()) {
                SExpression arg = args.car();

                if (arg.isAtom()) {
                    result.add(arg.toAtom());
                } else {
                    // If the argument is a list, don't add it to result directly, but
                    // rather add its constituent elements.
                    while (!arg.toList().isEmpty()) {
                        SExpression el = arg.toList().car();
                        if (el.isAtom()) {
                            result.add(el.toAtom());
                        } else {
                            result.add(el.toList());
                        }
                        arg = arg.toList().cdr();
                    }
                }
                args = args.cdr().toList();
            }

            return result;
        }
    }

    /**
     * Implements the LISP {@code ASSOC} function, which returns the first association for a
     * given key in an {@code alist} (associative list), by comparing the key to the {@code alist}
     * elements via an {@code equal} comparison.
     */
    public static class ASSOC extends AbstractFunction {
        public ASSOC() { super("ASSOC"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            if (!sexp.isList() || sexp.toList().lengthAsInt() < 2) {
                throw new WrongArgumentCountException("ASSOC requires at least two arguments");
            }

            final List args = sexp.toList();
            final SExpression key = args.car();
            final SExpression alist = args.cadr();

            if (!alist.isList()) {
                throw new EvaluationException("Second argument to ASSOC must be an association list");
            }

            List assocList = alist.toList();

            while (!assocList.isEmpty()) {
                SExpression expr = assocList.car();

                if (!expr.isList() || expr.toList().isNil()) {
                    throw new EvaluationException("Association list elements must be lists");
                }

                if (key.isEqual(expr.toList().car())) {
                    return expr;
                }

                assocList = assocList.cdr().toList();
            }

            return Atom.NIL;
        }
    }

    /**
     * Implements the LISP {@code LENGTH} function, which returns the number of top-level elements
     * in a given list. If the list is empty/NIL, returns 0. Throws if the given {@code sexp} is
     * not a list.
     */
    public static class LENGTH extends AbstractFunction {
        public LENGTH() { super("LENGTH"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            final List args = sexp.toList();
            if (args.car().isList()) { return args.car().toList().length(); }
            throw new EvaluationException("Argument to LENGTH must be a list");
        }
    }

    /**
     * Implements the LISP {@code LIST} function, which constructs a list whose elements are the
     * given arguments.
     */
    public static class LIST extends AbstractFunction {
        public LIST() { super("LIST"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            return sexp.toList();
        }
    }

    /**
     * Implements a non-standard {@code SIZE} function, which returns the total number of elements
     * in a given list, including elements in any nested lists. If the list is empty/NIL, returns
     * 0. Throws if the given {@code sexpr} is not a list.
     */
    public static class SIZE extends AbstractFunction {
        public SIZE() { super("SIZE"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            final List args = sexp.toList();
            if (args.car().isList()) { return args.car().toList().size(); }
            throw new EvaluationException("Argument to SIZE must be a list");
        }
    }
}
