package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.BindableFunction;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.SExpression;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Functions for reading and writing data.
 */
public class IO implements BindingProvider {
    /** {@inheritDoc} */
    @Override
    public List<Binding> getBindings() {
        return List.of(new Binding(new FORMAT()));
    }

    /**
     * Implements the LISP {@code FORMAT} function. This implementation uses {@link MessageFormat}-style
     * format strings.
     */
    public static class FORMAT extends BindableFunction {
        public FORMAT() { super("FORMAT"); }

        public SExpression apply(final SExpression sexp) {
            final Args args = Args.create(sexp).expectMinLength(2);
            final Atom destination = args.takeAtom();
            final String formatString = args.takeAtom().toS();

            final List<String> params = new ArrayList<>();

            while (args.hasNext()) {
                params.add(args.takeAtom().toS());
            }

            final String formattedString = MessageFormat.format(formatString, params.toArray());

            if (destination.toB()) {
                System.out.print(formattedString);
                return Atom.NIL;
            }

            return Atom.create(formattedString);
        }
    }
}
