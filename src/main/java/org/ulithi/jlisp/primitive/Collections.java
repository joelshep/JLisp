package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.BindableFunction;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.InvalidArgumentException;

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
                             new Binding(new Collections.NTH()),
                             new Binding(new Collections.SIZE()));
    }

    /**
     * Implements the LISP {@code APPEND} function, which concatenates list arguments into a single
     * list. APPEND special-cases the case where the only argument is a single atom, returning just
     * the atom.
     */
    public static class APPEND extends BindableFunction {
        public APPEND() { super("APPEND"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            final Args args = Args.create(sexp).expectMinLength(1);

            if (args.length() == 1 && args.hasAtom()) {
                return args.takeAtom();
            }

            final List result = List.create();

            while (args.hasNext()) {
                SExpression arg = args.takeAny();

                if (arg.isAtom() && args.remaining() > 0) {
                    throw new InvalidArgumentException("All arguments must be lists excpt");
                }

                if (arg.isAtom()) {
                    result.add(arg);
                } else {
                    // If the argument is a list, don't add it to result directly, but
                    // rather add its constituent elements.
                    while (!arg.toList().isEmpty()) {
                        result.add(arg.toList().car());
                        arg = arg.toList().cdr();
                    }
                }
            }

            return result;
        }
    }

    /**
     * Implements the LISP {@code ASSOC} function, which returns the first association for a
     * given key in an {@code alist} (associative list), by comparing the key to the {@code alist}
     * elements via an {@code equal} comparison.
     */
    public static class ASSOC extends BindableFunction {
        public ASSOC() { super("ASSOC"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            final Args args = Args.create(sexp).expectMinLength(2);

            final SExpression key = args.takeAny();
            List assocList = args.takeList();

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
    public static class LENGTH extends BindableFunction {
        public LENGTH() { super("LENGTH"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            final Args args = Args.create(sexp).expectLength(1);
            final List list = args.takeList();
            return list.length();
        }
    }

    /**
     * Implements the LISP {@code LIST} function, which constructs a list whose elements are the
     * given arguments.
     */
    public static class LIST extends BindableFunction {
        public LIST() { super("LIST"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            return sexp.toList();
        }
    }

    /**
     * Implements the LISP {@code NTH} function, which returns the n-th top-level element of
     * a list, using a zero-based index.
     */
    public static class NTH extends BindableFunction {
        public NTH() { super("NTH"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            final Args args = Args.create(sexp).expectLength(2);

            final int index = args.takeAtom().toI();

            if (index < 0) {
                throw new EvaluationException("First argument to NTH must be a non-negative integer");
            }

            final List list = args.takeList();

            return list.nth(index);
        }
    }

    /**
     * Implements a non-standard {@code SIZE} function, which returns the total number of elements
     * in a given list, including elements in any nested lists. If the list is empty/NIL, returns
     * 0. Throws if the given {@code sexpr} is not a list.
     */
    public static class SIZE extends BindableFunction {
        public SIZE() { super("SIZE"); }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            final Args args = Args.create(sexp).expectLength(1);
            final List list = args.takeList();
            return list.size();
        }
    }
}
