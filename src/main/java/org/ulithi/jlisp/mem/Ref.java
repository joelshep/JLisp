package org.ulithi.jlisp.mem;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Bindable;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.exception.TypeConversionException;

/**
 * A {@link Ref} is the concrete representation of an <em>S-Expression</em>. An S-Expression is
 * either an {@link Atom Atom} (a literal or symbol) or a "cons cell", which here is represented
 * by a {@link List}.
 * <p>
 * The {@link Ref} interface is also a simple "marker" interface for things that can be referred
 * to from {@link Cell} fields, namely: {@link Atom Atoms} which are containers for single literal
 * values and symbols, {@link List Lists}, the special {@link NilReference} element, and finally
 * other {@code Cells}: either the root/head node of a list, or the next cell in the current list.
 */
public interface Ref extends Bindable {

    /**
     * Indicates if this is a reference to an {@link Atom}.
     * @return True if this is a reference to an {@code Atom}, false otherwise.
     */
    default boolean isAtom() { return this instanceof Atom; }

    /**
     * If possible, returns this {@link Ref} as an {@link Atom}. Callers should check
     * {@code isAtom()} before calling this method.
     * @return This {@link Ref} as an {@link Atom}.
     */
    Atom toAtom();

    /**
     * Indicates if this is a reference to a {@link Cell}.
     * @return True if this is a reference to a {@code Cell}, false otherwise.
     */
    default boolean isCell() { return this instanceof Cell; }

    /**
     * If possible, returns this {@link Ref} as a {@link Cell}. Callers should check
     * {@code isCell()} before calling this method.
     * @return This {@link Ref} as a {@link Cell}.
     */
    default Cell toCell() {
        throw new TypeConversionException("Ref is not a Cell");
    }

    /**
     * Indicates if this is a reference to a {@link List}.
     * @return True if this is a reference to a {@code List}, false otherwise.
     */
    default boolean isList() { return this instanceof List; }

    /**
     * If possible, returns this {@link Ref} as a {@link List}. Callers should check
     * {@code isList()} before calling this method.
     * @return This {@link Ref} as a {@link List}.
     */
    List toList();

    /**
     * Indicates if this is a {@code NIL} reference.
     * @return True if this is a {@code NIL} reference, false otherwise.
     */
    default boolean isNil() { return this == NilReference.NIL; }
}
