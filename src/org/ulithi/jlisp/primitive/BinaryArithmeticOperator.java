package org.ulithi.jlisp.primitive;

/**
 * Interface for an arithmetic operator that accepts two integer arguments and returns a result
 * of the specified type.
 */
public interface BinaryArithmeticOperator<T> {
    /**
     * Apply the operator to two integer operands and return the result.
     * @param lhs The left-hand side operand in in-fix notation.
     * @param rhs The right-hand side operand in in-fix notation.
     * @return The result of applying the operator to the operands.
     */
    T eval (int lhs, int rhs);
}
