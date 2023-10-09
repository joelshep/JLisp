package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Binding;
import org.ulithi.jlisp.core.BindingProvider;
import org.ulithi.jlisp.core.SExpression;

import java.util.Arrays;
import java.util.List;

/**
 * A collection of Boolean and logical functions.
 */
public class Logic implements BindingProvider {

    /** {@inheritDoc} */
    @Override
    public List<Binding> getBindings() {
        return Arrays.asList(new Binding(new Logic.T()),
                             new Binding(new Logic.F()));
    }

    public static class T extends AbstractSymbol {
        public T() { super("T"); }

        /** {@inheritDoc} */
        @Override
        public SExpression eval() { return Atom.T; }
    }

    public static class F extends AbstractSymbol {
        public F() { super("F"); }

        /** {@inheritDoc} */
        @Override
        public SExpression eval() { return Atom.F; }
    }
}
