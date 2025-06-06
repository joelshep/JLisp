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
        return List.of(new Binding(new FORMAT()),
                       new Binding(new WRITE()));
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

    /**
     * Implements the LISP {@code WRITE} function, which writes the first argument to a stream
     * specified by an optional second argument. The argument is written as is, with not additional
     * newlines or whitespace. If no stream is specified, defaults to writing to STDOUT.
     * <p>
     * Note: currently this function writes to STDOUT <em>only</em>.
     */
    public static class WRITE extends BindableFunction {
        public WRITE() { super("WRITE"); }

        public SExpression apply(final SExpression sexp) {
            final Args args = Args.create(sexp).expectMinLength(1);
            final SExpression value = args.takeAny();
            System.out.print(value);
            return value;
        }
    }
}
