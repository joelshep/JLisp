package org.ulithi.jlisp.mem;

/**
 * The special {@link NilReference NIL} reference. The {@code NIL} reference is both an atom and
 * a list.
 */
public final class NilReference implements Ref {

    /**
     * The global singleton {@link NilReference NIL} reference.
     */
    public static final Ref NIL = new NilReference();

    @Override
    public boolean isAtom() { return false; }

    @Override
    public boolean isCell() { return false; }

    @Override
    public boolean isList() { return true; }

    @Override
    public boolean isNil() { return true; }

    @Override
    public String toString() { return "NIL"; }
}
