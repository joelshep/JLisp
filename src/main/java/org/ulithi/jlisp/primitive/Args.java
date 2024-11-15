package org.ulithi.jlisp.primitive;

import org.ulithi.jlisp.core.Atom;
import org.ulithi.jlisp.core.List;
import org.ulithi.jlisp.core.SExpression;
import org.ulithi.jlisp.exception.InvalidArgumentException;
import org.ulithi.jlisp.exception.ParseException;
import org.ulithi.jlisp.exception.WrongArgumentCountException;


/**
 * Helper class for validating and retrieving function arguments uniformly. Note that this class
 * does not <em>evaluate</em> its arguments.
 */
public class Args {

    /** Original list of arguments as passed to a function implementation. */
    private final List args;

    /** Number of arguments (top-level elements) in the args list. */
    private final int length;

    /** Zero-based index of the next argument to process. */
    private int index = 0;

    /** Optional, client-provided expected length of the args list. */
    private Integer expectedLength;

    /**
     * Do not construct: use a Create() method instead.
     */
    private Args(final List args) {
        this.args = args;
        this.length = args.lengthAsInt();
    }

    /**
     * Creates a new {@link Args} instance to process the given arguments. The given {@code sexp}
     * is expected to be a {@link List}, but this method accepts a bare {@link SExpression} so
     * that the client doesn't have to do the conversion.
     *
     * @param sexp A {@link List} of arguments for a function.
     * @return An instance of this class.
     * @throws ParseException if the given {@code sexp} is not a {@code List}.
     */
    public static Args create(final SExpression sexp) {
        if (!(sexp.isList() || sexp.isNil())) {
            throw new ParseException(
                    "Function expected argument list: received " + sexp.getClass().getSimpleName());
        }

        return sexp.isNil() ? new Args(List.create()) : new Args(sexp.toList());
    }

    /**
     * @return The total number of arguments available through this {@link Args} instance.
     */
    public int length() {
        return length;
    }

    /**
     * Validates that argument list has the expected number of elements.
     * @param expectedLength The expected number of arguments in the list.
     * @return This instance of {@code Args}.
     * @throws WrongArgumentCountException if the argument list doesn't contain {@code expectedLength}
     *         elements.
     */
    public Args expect(final int expectedLength) {
        if (this.expectedLength == null) { this.expectedLength = expectedLength; }

        if (expectedLength != length) {
            throw new WrongArgumentCountException(
                    "Expected " + expectedLength + " arguments: received " + length);
        }

        return this;
    }

    /**
     * Validates that the argument list has at least the given number of elements.
     * @param minLength The minimum number of elements expected in the list.
     * @return This instance of {@code Args}.
     * @throws WrongArgumentCountException if the argument list has fewer than {@code minLength}
     *         elements.
     */
    public Args expectMinLength(final int minLength) {
        if (length < minLength) {
            throw new WrongArgumentCountException(
                    "Expected at least " + length + " arguments: received " + length);
        }

        return this;
    }

    /**
     * @return The next argument in the argument list as an {@link SExpression}.
     * @throws WrongArgumentCountException if the argument list has no more arguments.
     */
    public SExpression wantAny() {
        checkHasNext();
        return args.nth(index++);
    }

    /**
     * @return The next argument in the argument list as an {@link Atom}.
     * @throws WrongArgumentCountException if the argument list has no more arguments.
     * @throws InvalidArgumentException if the next argument is not an {@code Atom}.
     */
    public Atom wantAtom() {
        checkHasNext();
        final SExpression sexp = args.nth(index);
        if (sexp.isAtom()) {
            index++;
            return sexp.toAtom();
        }
        throw new InvalidArgumentException("Atom expected for argument " + index +
                                           ": found " + sexp.getClass().getSimpleName());
    }

    /**
     * @return The next argument in the argument list as a {@link List}.
     * @throws WrongArgumentCountException if the argument list has no more arguments.
     * @throws InvalidArgumentException if the next argument is not a {@code List}.
     */
    public List wantList() {
        checkHasNext();
        final SExpression sexp = args.nth(index);
        if (sexp.isList()) {
            index++;
            return sexp.toList();
        }
        throw new InvalidArgumentException("List expected for argument " + index +
                                           ": found " + sexp.getClass().getSimpleName());
    }

    /**
     * @return True if there are more arguments for retrieval in the list: false otherwise.
     */
    public boolean hasNext() {
        return index < args.lengthAsInt();
    }

    /**
     * Checks to see if there are more arguments for retrieval and throws if not.
     * @throws WrongArgumentCountException If no more arguments are available for retrieval.
     */
    private void checkHasNext() {
        if (!hasNext()) {
            throw new WrongArgumentCountException(length + " arguments provided: more needed");
        }
    }
}
