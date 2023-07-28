package org.ulithi.jlisp.core;

import org.ulithi.jlisp.mem.Ref;

/**
 * An {@link SExpression} is the fundamental LISP language element, the basis for both {@link Atom Atoms}
 * (literals and symbols) and lists.
 * @deprecated I believe this interface may be redundant with the {@link Ref} interface, which is
 * better defined.
 */
@Deprecated()
public interface SExpression {
    boolean isList();
    boolean isNil();
    boolean isAtom();
}
