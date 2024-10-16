package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.AbstractFunction;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.Environment;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;

import java.util.Arrays;

/**
 * Contains core LISP language functions.
 */
public class Lang implements BindingProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<Binding> getBindings() {
        return Arrays.asList(new Binding(new Lang.CAR()),
                             new Binding(new Lang.CDR()),
                             new Binding(new Lang.CONS()),
                             new Binding(new Lang.DEFUN()),
                             new Binding(new Lang.IF()),
                             new Binding(new Lang.QUOTE()),
                             new Binding(new Lang.SETQ()));
    }

    /**
     * Implements the LISP {@code CAR} function. The {@code CAR} function accepts a list and returns
     * the first element in the list.
     */
    public static class CAR extends AbstractListFunction {
        public CAR() { super("CAR"); }

        @Override
        public SExpression applyImpl(final List list) {
            return list.car();
        }
    }

    /**
     * Implements the LISP {@code CDR} function. The {@code CDR} function accepts a list and returns
     * the list except for the first item. If the list is a single element list, {@code CDR} returns
     * {@code NIL}.
     */
    public static class CDR extends AbstractListFunction {
        public CDR() { super("CDR"); }

        @Override
        public SExpression applyImpl(final List list) {
            return list.cdr();
        }
    }

    /**
     * Implements the LISP {@code CONS} function. The {@code CONS} function accepts two arguments
     * and returns a {@link List} such that the {@code CAR} of the list is the first argument and the
     * {@code CDR} of the list is the second element.
     */
    public static class CONS extends AbstractFunction {
        public CONS() { super("CONS"); }

        @Override
        public SExpression apply(final SExpression sexp) {
            if (!sexp.isList()) { throw new EvaluationException("List argument expected"); }

            List args = sexp.toList();

            if (args.lengthAsInt() != 2) {
                throw new WrongArgumentCountException("Expected 2 arguments: received " + args.length());
            }

            final List cons = List.create();

            do {
                final SExpression arg = args.car();
                if (arg.isAtom()) {
                    cons.add(arg.toAtom());
                } else if (arg.isList()) {
                    final List list = arg.toList();
                    if (!list.isEmpty()) {
                        cons.append(list);
                    }
                }

                if (args.endp()) break;
                args = args.cdr().toList();
            } while (true);

            return cons;
        }
    }

    /**
     * Implements the LISP {@code DEFUN} function. Returns a literal {@code Atom} representing
     * the name of the newly created function.
     */
    public static class DEFUN extends AbstractFunction {
        public DEFUN() { super("DEFUN"); }

        @Override
        public boolean isSpecial() { return true; }

        @Override
        public boolean isDefining() { return true; }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            throw new EvaluationException("Defining function invoked without environment reference");
        }

        /** {@inheritDoc} **/
        @Override
        public SExpression apply(final SExpression sexp, final Environment env) {
            final List args = sexp.toList();

            final SExpression name = args.car();
            final SExpression arguments = args.cdr().toList().car();
            final SExpression definition = args.cdr().toList().cdr().toList().car();
            final UserFunction function = new UserFunction(name.toString(), arguments, definition);

            env.addUserBinding(new Binding(name.toAtom().toS(), function));

            return name.toAtom();
        }
    }

    /**
     * Implements the LISP {@code IF} special function. {@code IF} takes two or three
     * {@link SExpression S-Expressions}:<pre>
     *     (IF (test-sexp) (then-sexp) (else-sexp) )</pre>
     * It evaluates the {@code test-sexp}. If the {@code text-sexp} evaluates to a non-false value,
     * then {@code IF} evaluates {@code then-sexp} and returns the resulting value. Otherwise, it
     * evaluates {@code else-sexp} and returns that value, or returns {@code F} if {@code else-sexp}
     * isn't provided. {@code IF} evaluates {@code then-sexp} and {@code else-sexp} lazily: only
     * one will be evaluated when the {@code IF} function is invoked, depending on the value of
     * {@code test-sexp}.
     */
    public static class IF extends AbstractFunction {
        public IF() { super("IF"); }

        @Override
        public boolean isSpecial() { return true; }

        @Override
        public boolean isReentrant() { return true; }

        /** {@inheritDoc */
        @Override
        public SExpression apply(final SExpression sexp, final Environment env, final Eval eval) {
            final List args = sexp.toList();

            if (args.lengthAsInt() < 2 || args.lengthAsInt() > 3) {
                throw new WrongArgumentCountException("IF expects two or three arguments");
            }

            final SExpression testSexp = eval.apply(args.car());

            final boolean condition = (testSexp.isList() && !testSexp.toList().isEmpty()) ||
                                      (testSexp.isAtom() && testSexp.toAtom().toB());

            if (condition) {
                return eval.apply(args.cdr().toList().car());
            } else {
                return eval.apply(args.cdr().toList().cdr());
            }
        }
    }

    /**
     * Implements the LISP {@code QUOTE} function. The {@code QUOTE} function returns its arguments
     * as-is, and is therefore a "special" function. In modern LISP, the {@code '} token is
     * shorthand for {@code QUOTE}.
     */
    public static class QUOTE extends AbstractFunction {
        public QUOTE() { super("QUOTE"); }

        /** {@inheritDoc} */
        @Override
        public boolean isSpecial() { return true; }

        /** {@inheritDoc} */
        @Override
        public SExpression apply(final SExpression sexp) {
            if (sexp.isAtom()) { return sexp.toAtom(); }
            return sexp.toList().car();
        }
    }

    /**
     * Implements the LISP {@code SETQ} function. The {@code SETQ} function assigns the value of
     * its second argument to the symbol specified by the first argument. When the symbol has not
     * been defined previously (e.g. by {@code DEFVAR}), {@code SETQ} creates a corresponding
     * global variable.
     */
    public static class SETQ extends AbstractFunction {
        public SETQ() { super("SETQ"); }

        @Override
        public boolean isReentrant() { return true; }

        @Override
        public boolean isSpecial() { return true; }

        /** {@inheritDoc} **/
        @Override
        public SExpression apply(final SExpression sexp, final Environment env, final Eval eval) {
            final List args = sexp.toList();

            final SExpression varNameAtom = args.car();

            if (!varNameAtom.isAtom()) {
                throw new EvaluationException("First argument to SETQ must be a symbol: received " + varNameAtom);
            }

            final SExpression definition = eval.apply(args.cdr());
            env.addUserBinding(new Binding(varNameAtom.toString(), definition));

            return definition;
        }
    }
}
