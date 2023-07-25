package org.ulithi.jlisp.mem;

/**
 * The {@link Ref} interface is a simple "marker" interface for things that can be referred to
 * from {@link Cell} fields, namely: {@link Atom Atoms} (including {@code NIL}, {@link List Lists}
 * and other {@code Cells}.
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
