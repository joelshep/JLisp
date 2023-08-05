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
    public final boolean isAtom() { return true; }

    @Override
    public final boolean isCell() { return false; }

    @Override
    public final boolean isList() { return true; }

    @Override
    public final boolean isNil() { return true; }

    @Override
    public String toString() { return "NIL"; }
}
