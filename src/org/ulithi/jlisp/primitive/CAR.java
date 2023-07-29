package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.mem.Cell;

/**
 * Implements the LISP {@code CAR} function. The {@code CAR} function accepts a list and returns
 * the first element in the list. If the list is a single element list, {@code CDR} returns
 * {@code NIL}.
 */
public class CAR {
    public SExpression apply (final Cell cell) {
        if (cell.isNil()) { return Atom.NIL; }
        return SExpression.create(cell.getFirst());
    }
}
