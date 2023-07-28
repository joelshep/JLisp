package org.ulithi.jlisp.core;

/**
 * An {@link SExpression} is the fundamental LISP language element, the basis for both
 * {@link Atom Atoms} (literals and symbols) and lists.
 */
public interface SExpression {
    boolean isList();
    boolean isNil();
    boolean isAtom();
}
