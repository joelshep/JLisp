package org.ulithi.jlisp.mem;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.Bindable;
import org.ulithi.jlisp.core.Function;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.TypeConversionException;

/**
 * A {@link Ref} is a "marker" interface for things that can be referred to from {@link Cell}
 * fields, namely: {@link Atom Atoms} which are containers for single literal values and symbols,
 * {@link List Lists}, {@link Function functions}, the special {@link NilReference} element, and
 * finally other {@code Cells}: either the root/head node of a list, or the next cell in the current
 * list. The {@code Ref} interface is the bridge between the in-memory parse tree representation
 * of a LISP form, and the corresponding {@code SExpression}.
 */
public interface Ref extends Bindable {

    /**
     * If possible, returns this {@link Ref} as an {@link SExpression}.
     * @return This {@link Ref} as an {@link SExpression}.
     */
    SExpression toSExpression();

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
     * Indicates if this is a reference to a {@link Function}.
     * @return True if this is a reference to a {@code Function}, false otherwise.
     */
    default boolean isFunction() { return this instanceof Function; }

    /**
     * If possible, returns this {@link Ref} as a {@link Function}. Callers should check
     * {@code isFunction()} before calling this method.
     * @return This {@link Ref} as a {@link Function}.
     */
    Function toFunction();

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
    boolean isNil();
}
