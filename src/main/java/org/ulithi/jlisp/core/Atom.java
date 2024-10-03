package org.ulithi.jlisp.core;

import org.ulithi.jlisp.exception.TypeConversionException;
import org.ulithi.jlisp.mem.NilReference;
import org.ulithi.jlisp.mem.Ref;
import org.ulithi.jlisp.parser.Grammar;

import java.util.Objects;

/**
 * An {@link Atom} is an indivisible unit of literal data (e.g., number, boolean or character
 * sequence), or a symbol. {@code Atom} behaves a lot like a "variant" data type: a single
 * class type that can represent several types of data and provides seamless transformations
 * between types when possible (e.g., number to string, or string to boolean).
 */
public class Atom implements SExpression {

    /** An {@link Atom} representing the special {@code F} (false) value. */
    public static final Atom F = new Atom(Boolean.FALSE, Type.Boolean);

    /** An {@link Atom} representing the special {@code T} (true) value. */
    public static final Atom T = new Atom(Boolean.TRUE, Type.Boolean);

    /** An {@link Atom} representing the special {@code NIL} value. */
    public static final Atom NIL = new Atom(null, Type.NIL);

    /** Enumerates the data types that can be represented as an Atom. */
    private enum Type {
        String,
        Number,
        Boolean,
        Symbol,
        NIL
    }

    /** The underlying type of this Atom. */
    private final Type type;

    /**
     * The underlying value of this Atom. For Symbols, the value is the programmatic name
     * or token for the symbol: e.g., "+" for the addition operator, "flag" for a variable, etc.
     */
    private final Object value;

    /**
     * Creates and returns a new {@link Atom}: either {@code NIL} if {@code ref} is {@code NIL} or
     * {@code ref} itself if it is an {@code Atom}.
     *
     * @param ref A {@link Ref}, that represents an {@link Atom} or {@code NIL}.
     * @return An {@link Atom} as represented by the {@code ref}.
     */
    public static Atom create (final Ref ref) {
        if (ref == NilReference.NIL) { return Atom.NIL; }
        return (Atom) ref;
    }

    /**
     * Creates and returns a new {@link Atom} representing the given {@code String} literal.
     * @param sValue The {@code String} literal that this {@code Atom} will represent.
     * @return An {@code Atom} representing the given {@code String} literal.
     */
    public static Atom create(final String sValue) {
        return new Atom(sValue, Type.String);
    }

    /**
     * Creates and returns a new {@link Atom} representing the given {@code Integer} value.
     * @param nValue The {@code Integer} that this {@code Atom} will represent.
     * @return An {@code Atom} representing the given {@code Integer} value.
     */
    public static Atom create(final Integer nValue) {
        return new Atom(nValue, Type.Number);
    }

    /**
     * Creates and returns a new {@link Atom} representing the given {@code Boolean} value.
     * @param bValue The {@code Boolean} that this {@code Atom} will represent.
     * @return An {@code Atom} representing the given {@code Boolean} value.
     */
    public static Atom create(final Boolean bValue) {
        return (bValue ? T : F);
    }

    /**
     * Creates and returns a new {@link Atom} representing a symbol identified by the given
     * {@code name}.
     * @param name The programmatic name of the symbol that this {@code Atom} will represent.
     * @return An {@code Atom} representing the named symbol.
     */
    public static Atom createSymbol(final String name) {
        return new Atom(name, Type.Symbol);
    }

    /**
     * Creates a new Atom with the given value and specified underlying type.
     *
     * @param value The value of this Atom.
     * @param type The native data type of the given value.
     */
    private Atom(final Object value, final Type type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Indicates if this {@link Atom} is a symbol.
     * @return True if this {@code Atom} is a symbol, false otherwise.
     */
    public final boolean isSymbol() {
        return this.type == Type.Symbol || this.type == Type.NIL;
    }

    /**
     * Indicates if this {@link Atom} is a literal value. Note that NIL is considered a literal
     * value.
     * @return True if this {@code Atom} is a literal value, false otherwise.
     */
    public final boolean isLiteral() {
        return this.type != Type.Symbol;
    }

    /**
     * Indicates if this {@link Atom} is a numeric literal value.
     * @return True if this {@code Atom} is a numeric literal value, false otherwise.
     */
    public final boolean isNumber() {
        return this.type == Type.Number;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isNil() {
        return this.equals(Atom.NIL);
    }

    /**
     * Returns this {@link Atom Atom's} value as an integer. Number atoms have their value returned
     * directly (currently only integer numbers are supported). For Boolean atoms, returns -1 if
     * the atom's value is True, and returns 0 if it is False. String values and symbols cannot be
     * converted to integers.
     *
     * @return This {@link Atom Atom's} value as an integer.
     * @throws TypeConversionException if the type conversion is disallowed (e.g., String to
     *         Number), or if there is no known conversion for this {@code Atom's} type.
     */
    public final int toI() {
        switch (this.type) {
            case Number: return ((Number)value).intValue();
            case Boolean: return ((Boolean)value) ? -1 : 0;
            case String: throw new TypeConversionException("Can't convert string literal to number");
            case Symbol: throw new TypeConversionException("Can't convert symbol to number");
            default: throw new TypeConversionException("Unknown data type " + this.type);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Atom toAtom() {
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation throws a {@link TypeConversionException} because an {@code Atom} is not
     * a {@code List}.
     */
    @Override
    public List toList() {
        throw new TypeConversionException("Can't convert Atom to List");
    }

    /**
     * Returns this {@link Atom Atom's} value as a boolean. Boolean atoms have their value returned
     * directly. For numeric atoms, returns true if the value is non-zero, false otherwise. For
     * string atoms, returns true if the value is non-empty (i.e., not ""), false otherwise.
     *
     * @return This {@link Atom Atom's} value as a boolean.
     * @throws TypeConversionException if the type conversion is disallowed or if there is no known
     *         conversion for this {@code Atom's} type.
     */
    public final boolean toB() {
        switch (this.type) {
            case NIL: return false;
            case Number: return ((Number)value).intValue() != 0;
            case Boolean: return ((Boolean)value);
            case String: return !((String)value).isEmpty();
            case Symbol: throw new TypeConversionException("Can't convert symbol to Boolean");
            default: throw new TypeConversionException("Unknown data type " + this.type);
        }
    }

    /**
     * Returns this {@link Atom Atom's} value as a character string. All types except symbols
     * can be converted to a {@code String}.
     *
     * @return This {@link Atom Atom's} value as a character string.
     * @throws TypeConversionException if the type conversion is disallowed or if there is no known
     *         conversion for this {@code Atom's} type.
     */
    public final String toS() {
        if (this.type == Type.NIL) { return Grammar.NIL; }
        if (this.type == Type.Boolean) { return ((Boolean)value) ? Grammar.T : Grammar.F; }
        return String.valueOf(value);
    }

    /**
     * Returns the {@code String} representation of this {@link Atom Atom's} value.
     * @return The {@code String} representation of this {@link Atom Atom's} value.
     */
    @Override
    public final String toString() {
        if (this.type == Type.NIL) { return Grammar.NIL; }
        if (this.type == Type.Boolean) { return ((Boolean)value) ? Grammar.T : Grammar.F; }
        return String.valueOf(value);
    }

    /**
     * Indicates if this {@link Atom} is value-equal to the given {@code Atom}. Value-equal means
     * the two {@code Atoms} are the same {@code type} and have the same value. The {@code eql}
     * method does not dynamically cast types: e.g. comparing an {@code Atom} representing the
     * number zero (0) and Atom.F will return {@code false}, even though {@code toB()} treats them
     * as the same.
     *
     * @param rhs The {@code Atom} to compare to.
     * @return True if this {@code Atom} and the given {@code Atom} are value-equal, false otherwise.
     */
    public boolean eql(final Atom rhs) {
        return (this.type == rhs.type &&
                Objects.equals(this.value, rhs.value));
    }
}
