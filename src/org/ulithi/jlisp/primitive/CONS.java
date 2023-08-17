package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.EvaluationException;
import org.ulithi.jlisp.mem.Ref;

/**
 * Implements the LISP {@code CONS} function. The {@code CAR} function accepts a list and returns
 * the first element in the list. If the list is a single element list, {@code CDR} returns
 * {@code NIL}.
 */
public class CONS implements Function {
    @Override
    public SExpression apply(final SExpression sexp) {
        //(CONS BAR (BAZ)) => (List)SExpression (BAR . (BAZ . NIL))
        if (!sexp.isList()) {
            throw new EvaluationException("List argument expected");
        }

        List args = sexp.toList();
        final List cons = List.create();

        do {
            final SExpression arg = args.car();
            if (arg.isAtom()) {
                cons.add(arg.toAtom());
            } else if (arg.isList()) {
                final List list = arg.toList();
                if (!list.isEmpty()) {
                    cons.add(list);
                }
            }

            if (args.endp()) break;
            args = args.cdr().toList();
        } while (true);

        return cons;
    }
}
