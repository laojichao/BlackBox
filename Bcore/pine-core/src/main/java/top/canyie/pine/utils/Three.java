package top.canyie.pine.utils;

import java.util.Objects;

/**
 * A simple generic triple (3-tuple) container holding three values of potentially different types.
 * <p>
 * Used internally by the Pine framework to return multiple values from argument extraction
 * methods in the architecture-specific entry classes (e.g., core registers, stack, and
 * floating-point registers).
 * </p>
 *
 * @param <A> the type of the first element.
 * @param <B> the type of the second element.
 * @param <C> the type of the third element.
 * @author canyie
 */
public final class Three<A, B, C> {
    /** The first element. */
    public A a;
    /** The second element. */
    public B b;
    /** The third element. */
    public C c;

    /**
     * Creates an empty triple with all elements set to {@code null}.
     */
    public Three() {
    }

    /**
     * Creates a triple with the given values.
     *
     * @param a the first element.
     * @param b the second element.
     * @param c the third element.
     */
    public Three(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Three)) return false;
        Three<?, ?, ?> that = (Three) obj;
        return Objects.equals(a, that.a) && Objects.equals(b, that.b) && Objects.equals(c, that.c);
    }

    @Override public int hashCode() {
        return Objects.hashCode(a) ^ Objects.hashCode(b) ^ Objects.hashCode(c);
    }

    @Override public String toString() {
        return "Three{A: " + a + "; b: " + b + "; c: " + c + "}";
    }
}
