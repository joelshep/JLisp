package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.BindableFunction;
import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.DefiningFunction;
import org.ulithi.jlisp.core.Environment;
import org.ulithi.jlisp.core.LambdaFunction;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.Macro;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.core.UserFunction;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.exception.SyntaxException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;
import org.ulithi.jlisp.mem.Cell;

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
        return Arrays.asList(new Binding(new Lang.APPLY()),
                             new Binding(new Lang.CAR()),
                             new Binding(new Lang.CDR()),
                             new Binding(new Lang.COND()),
                             new Binding(new Lang.CONS()),
                             new Binding(new Lang.DEFMACRO()),
                             new Binding(new Lang.DEFUN()),
                             new Binding(new Lang.EVAL()),
                             new Binding(new Lang.IF()),
                             new Binding((new Lang.LAMBDA())),
                             new Binding((new Lang.LET())),
                             new Binding(new Lang.MACROEXPAND()),
                             new Binding((new Lang.MAPCAR())),
                             new Binding(new Lang.QUOTE()),
                             new Binding(new Lang.SETQ()));
    }

    /**
     * Implements the LISP {@code APPLY} function. The {@code APPLY} function takes two arguments:
     * a function specification (a function object, a function name or a lambda expression), and a
     * list of arguments for a single invocation of the function. Returns the result of applying
     * the function to the arguments.
     */
    public static class APPLY extends BindableFunction {
        public APPLY() { super("APPLY"); }

        @Override
        public boolean isReentrant() { return true; }

        @Override
        public SExpression apply(final SExpression sexp, final Environment env, final Eval eval) {
            List args = sexp.toList();

            if (args.lengthAsInt() != 2) {
                throw new WrongArgumentCountException("Expected 2 arguments: received " + args.length());
            }

            if (!args.nth(1).isList()) {
                throw new WrongArgumentCountException("Second argument to APPLY must be list of arguments");
            }

            final SExpression function = args.nth(0);
            final List parameters = args.nth(1).toList();
            return eval.apply(function, parameters);
        }
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

    public static class COND extends BindableFunction {
        public COND() { super("COND");  }

        @Override
        public boolean isSpecial() { return true; }

        @Override
        public boolean isReentrant() { return true; }

        /** {@inheritDoc */
        @Override
        public SExpression apply(final SExpression sexp, final Environment env, final Eval eval) {
            // Expects a list of lists. Evaluates the first element in each list. If it evaluates
            // to T, then evaluates and returns the value of the second element. Iterates through
            // the list of lists start to finish and terminates on the first one that evaluates to T.
            if (sexp.isAtom()) { return sexp; }

            List args = sexp.toList();

            while (!args.isEmpty()) {
                final SExpression cond = args.car();

                if (!cond.isList() || cond.isNil()) {
                    throw new EvaluationException("Argument to COND must be a list, got: " + cond);
                }

                final List conditional = cond.toList();

                final SExpression condition = conditional.nth(0);

                final SExpression truth = eval.eval(condition);

                if (truth.toAtom().toB()) {
                    final SExpression consequent = conditional.nth(1);
                    return consequent.isNil() ? truth : eval.eval(consequent);
                }

                args = args.cdr().toList();
            }

            return Atom.NIL;
        }
    }

    /**
     * Implements the LISP {@code CONS} function. The {@code CONS} function accepts two arguments
     * and returns a {@link List} such that the {@code CAR} of the list is the first argument and the
     * {@code CDR} of the list is the second element.
     */
    public static class CONS extends BindableFunction {
        public CONS() { super("CONS"); }

        @Override
        public SExpression apply(final SExpression sexp) {
            final Args args = Args.create(sexp).expectLength(2);

            final Cell consCell = Cell.create();
            consCell.setFirst(args.takeAny().toRef());
            consCell.setRest(args.takeAny().toRef());
            return List.create(consCell);
        }
    }

    /**
     * Implements the LISP {@code DEFMACRO} function. Returns a literal {@code Atom} representing
     * the name of the newly created macro.
     */
    public static class DEFMACRO extends DefiningFunction {
        public DEFMACRO() { super("DEFMACRO"); }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void define(String name, SExpression formals, SExpression definition, Environment env) {
            final Macro macro = new Macro(name, formals, definition);
            env.addMacro(name, macro);
        }
    }

    /**
     * Implements the LISP {@code DEFUN} function. Returns a literal {@code Atom} representing
     * the name of the newly created function.
     */
    public static class DEFUN extends DefiningFunction {
        public DEFUN() { super("DEFUN"); }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void define(String name, SExpression formals, SExpression definition, Environment env) {
            final UserFunction function = new UserFunction(name, formals, definition);
            env.addUserBinding(new Binding(name, function));
        }
    }

    /**
     * Implements the LISP {@code MACROEXPAND} function. Takes a QUOTE'd expression representing a
     * macro application, and returns the expanded form as evaluated against the current environment.
     */
    public static class MACROEXPAND extends BindableFunction {
        public MACROEXPAND() { super("MACROEXPAND"); }

        /** {@inheritDoc} */
        @Override
        public boolean isReentrant() { return true; }

        /** {@inheritDoc} **/
        @Override
        public SExpression apply(final SExpression sexp, final Environment env, final Eval eval) {
            final Args args = Args.create(sexp);
            final SExpression form = args.takeList();

            if (form.isNil() || !form.isList()) { return form; }

            // Recursively expand the macro.
            SExpression expanded = expandMacro(form, env, eval);

            if (expanded.isList()) {
                expanded = expandNestedMacros(expanded, env, eval);
            }

            return expanded;
        }

        /**
         * Performs a single macro expansion. If the first element of the given form is a macro
         * reference, expands the macro against the given form and environment, but does not
         * recursively expand any inner forms.
         * @param form A form that may begin with a macro reference.
         * @param env Reference to the current runtime environment.
         * @param eval Reference to the effective Eval function.
         * @return The expanded macro form.
         */
        private SExpression expandMacro(SExpression form, Environment env, Eval eval) {
            final SExpression car = form.toList().car();

            if (!car.isAtom()) { return form; }

            final String name = car.toAtom().toS();

            if (!env.isMacro(name)) { return form; }

            return env.getMacro(name).expand(form, env, eval);
        }

        /**
         * Performs full macro expansion, recursively expanding nested macros.
         * @param form A form that may contain macro references.
         * @param env Reference to the current runtime environment.
         * @param eval Reference to the effective Eval function.
         * @return The form with all contained macros expanded.
         */
        private SExpression expandNestedMacros(SExpression form, Environment env, Eval eval) {
            if (!form.isList()) { return form; }

            SExpression expanded = expandMacro(form, env, eval);

            if (!expanded.isList()) { return expanded; }

            List expandedElements = List.create();

            for (int i = 0; i < expanded.toList().lengthAsInt(); i++) {
                expandedElements.add(expandNestedMacros(expanded.toList().nth(i), env, eval));
            }

            return expandedElements;
        }
    }

    /**
     * Implements the LISP {@code EVAL} function. The {@code EVAL} function evaluates a form and
     * returns the result. Note that the form itself is the result of evaluating the arguments
     * to EVAL: e.g., with {@code (EVAL '(+ 1 2 3))}, the EVAL function receives {@code (+ 1 2 3)}
     * as its argument (the result of evaluating {@code (QUOTE (1 2 3))}.
     */
    public static class EVAL extends BindableFunction {
        public EVAL() { super("EVAL"); }

        @Override
        public boolean isReentrant() { return true; }

        /** {@inheritDoc */
        @Override
        public SExpression apply(final SExpression sexp, final Environment env, final Eval eval) {
            final List arg = sexp.toList();

            if (!(arg.lengthAsInt() == 1 || arg.isNil())) {
                throw new EvaluationException("Argument to " + name() + " must be a single form");
            }

            return eval.eval(arg.car());
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
    public static class IF extends BindableFunction {
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

            final SExpression testSexp = eval.eval(args.nth(0));

            final boolean condition = (testSexp.isList() && !testSexp.toList().isEmpty()) ||
                                      (testSexp.isAtom() && testSexp.toAtom().toB());

            if (condition) {
                return eval.eval(args.nth(1));
            } else {
                return eval.eval(args.nth(2));
            }
        }
    }

    /**
     * Implements the LISP {@code LAMBDA} function (macro). Accepts a potentially empty list of
     * formal parameters and a form representing a function body, and returns a
     * {@link org.ulithi.jlisp.core.Function} object.
     */
    public static class LAMBDA extends BindableFunction {
        public LAMBDA() { super("LAMBDA"); }

        @Override
        public boolean isSpecial() { return true; }

        public SExpression apply(final SExpression sexp) {
            final List args = sexp.toList();
            final SExpression arguments = args.nth(0);
            final SExpression definition = args.nth(1);
            return new LambdaFunction(arguments, definition);
        }
    }

    public static class LET extends BindableFunction {
        public LET() { super("LET"); }

        @Override
        public boolean isReentrant() { return true; }

        @Override
        public boolean isSpecial() { return true; }

        public SExpression apply(final SExpression sexp, final Environment env, final Eval eval) {
            final Args args = Args.create(sexp);

            // First argument should be list of bindings
            final List bindings = args.takeList();

            // Process each binding
            for (int i = 0; i < bindings.lengthAsInt(); i++) {
                final SExpression binding = bindings.nth(i);

                if (!binding.isList()) {
                    throw new SyntaxException("Invalid binding specification for LET: " + binding);
                }

                final List bindingPair = binding.toList();

                final SExpression nameAtom = bindingPair.nth(0);

                if (!nameAtom.isAtom()) {
                    throw new EvaluationException("Binding argument for LET must be a symbol: received " + nameAtom);
                }

                final String name = nameAtom.toAtom().toS();
                final SExpression definition = eval.eval(bindingPair.nth(1));
                env.addBinding(name, definition);
            }

            // Evaluate body forms in sequence, return last result
            SExpression result = Atom.NIL;

            while (args.hasNext()) {
                result = eval.eval(args.takeAny());
            }

            return result;
        }
    }

    public static class MAPCAR extends BindableFunction {
        public MAPCAR() { super("MAPCAR"); }

        @Override
        public boolean isReentrant() { return true; }

        public SExpression apply(final SExpression sexp, final Environment env, final Eval eval) {
            final List args = sexp.toList();
            final SExpression function = args.nth(0);
            final List[] argLists = new List[args.lengthAsInt()-1];

            for (int i = 0; i < argLists.length; i++) {
                argLists[i] = args.nth(i + 1).toList();
            }

            final List result = List.create();

            for (int i = 0; i < argLists[0].lengthAsInt(); i++) {
                final List invocationArgs = List.create();
                for (int j = 0; j < argLists.length; j++) {
                    final SExpression arg = argLists[j].nth(i);
                    invocationArgs.add(arg);
                }

                result.add(eval.apply(function, invocationArgs));
            }

            return result;
        }
    }

    /**
     * Implements the LISP {@code QUOTE} function. The {@code QUOTE} function returns its arguments
     * as-is, and is therefore a "special" function. In modern LISP, the {@code '} token is
     * shorthand for {@code QUOTE}.
     */
    public static class QUOTE extends BindableFunction {
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
    public static class SETQ extends BindableFunction {
        public SETQ() { super("SETQ"); }

        @Override
        public boolean isReentrant() { return true; }

        @Override
        public boolean isSpecial() { return true; }

        /** {@inheritDoc} **/
        @Override
        public SExpression apply(final SExpression sexp, final Environment env, final Eval eval) {
            final List args = sexp.toList();

            final SExpression varNameAtom = args.nth(0);

            if (!varNameAtom.isAtom()) {
                throw new EvaluationException("First argument to SETQ must be a symbol: received " + varNameAtom);
            }

            final SExpression definition = eval.eval(args.nth(1));
            env.addUserBinding(new Binding(varNameAtom.toString(), definition));

            return definition;
        }
    }
}
