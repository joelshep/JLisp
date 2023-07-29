package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.mem.Cell;

/**
 * Implements the LISP {@code CDR} function. The {@code CDR} function accepts a list and returns
 * the list except for the first item. If the list is a single element list, {@code CDR} returns
 * {@code NIL}.
 */
public class CDR {
    public SExpression apply (final Cell cell) {
        if (cell.isNil()) { return Atom.NIL; }
        return SExpression.create(cell.getRest());
    }
}
