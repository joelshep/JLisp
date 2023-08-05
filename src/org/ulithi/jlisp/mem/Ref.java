package org.ulithi.jlisp.mem;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;

/**
 * The {@link Ref} interface is a simple "marker" interface for things that can be referred to
 * from {@link Cell} fields, namely: {@link Atom Atoms} which are containers for single literal
 * values and symbols, {@link List Lists}, the special {@link NilReference} element, and finally other
 * {@code Cells}: either the root/head node of a list, or the next cell in the current list.
 */
public interface Ref {

    /**
     * Indicates if this is a reference to an {@link Atom}.
     * @return True if this is a reference to an {@code Atom}, false otherwise.
     */
    default boolean isAtom() { return false; }

    /**
     * Indicates if this is a reference to a {@link Cell}.
     * @return True if this is a reference to a {@code Cell}, false otherwise.
     */
    default boolean isCell() { return false; }

    /**
     * Indicates if this is a reference to a {@link List}.
     * @return True if this is a reference to a {@code List}, false otherwise.
     */
    default boolean isList() { return false; }

    /**
     * Indicates if this is a {@code NIL} reference.
     * @return True if this is a {@code NIL} reference, false otherwise.
     */
    default boolean isNil() { return false; }
}
