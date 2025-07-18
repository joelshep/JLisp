package org.ulithi.jlisp.mem;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Function;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;

/**
 * The special {@link NilReference NIL} reference. The {@code NIL} reference is both an atom and
 * a list.
 */
public final class NilReference implements Ref {

    /**
     * The global singleton {@link NilReference NIL} reference.
     */
    public static final Ref NIL = new NilReference();

    /**
     * No construction: use the globally defined NIL object.
     */
    private NilReference() { }

    @Override
    public SExpression toSExpression() { return Atom.NIL; }

    @Override
    public boolean isAtom() { return true; }

    @Override
    public Atom toAtom() { return Atom.NIL; }

    @Override
    public boolean isFunction() { return false; }

    @Override
    public Function toFunction() {
        throw new EvaluationException("NIL can not be converted to function");
    }

    @Override
    public boolean isList() { return true; }

    @Override
    public List toList() { return List.create(); }

    @Override
    public boolean isNil() { return true; }

    @Override
    public String toString() { return "NIL"; }
}
