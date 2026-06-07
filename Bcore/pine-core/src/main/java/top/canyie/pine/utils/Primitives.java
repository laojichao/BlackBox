package top.canyie.pine.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import top.canyie.pine.Pine;

/**
 * Utility class providing low-level operations for the Pine hooking framework.
 * <p>
 * Includes methods for manipulating object class pointers, superclass references,
 * field offsets, class access flags, and byte/int/long/double conversions assuming
 * little-endian byte order (as used by Android/ARM).
 * </p>
 *
 * @author canyie
 */
@SuppressWarnings("JavaReflectionMemberAccess") @SuppressLint("PrivateApi") public final class Primitives {
    private static final String TAG = "Primitives";
    private static Class<?> unsafeClass;
    private static Object unsafe;
    private static Method putObject;
    private static boolean triedGetShadowKlassField;
    private static Field shadowKlassField;
    private static Field superClassField;
    private static Field classAccessFlagsField;

    /**
     * Returns the native ART thread pointer for the current thread.
     * Ensures the Pine library is initialized before retrieving the thread pointer.
     *
     * @return the native ART thread pointer as a {@code long}.
     */
    public static long currentArtThread() {
        Pine.ensureInitialized();
        return Pine.currentArtThread0();
    }

    /**
     * Sets the class pointer of an object to a different class at the native level.
     * <p>
     * Tries {@code Object.shadow$_klass_} field first; falls back to {@code sun.misc.Unsafe.putObject}
     * if the shadow field is not available.
     * </p>
     *
     * @param target   the object whose class pointer should be changed.
     * @param newClass the new class to assign to the object.
     */
    public static void setObjectClass(Object target, Class<?> newClass) {
        if (target.getClass() == newClass) return;
        if (!triedGetShadowKlassField) {
            triedGetShadowKlassField = true;
            try {
                shadowKlassField = Object.class.getDeclaredField("shadow$_klass_");
                shadowKlassField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.w("Primitives", "Object.shadow$_klass_ not found, use Unsafe.", e);
            }
        }
        try {
            if (shadowKlassField != null) {
                shadowKlassField.set(target, newClass);
            } else {
                ensureUnsafeReady();
                if (putObject == null) {
                    putObject = unsafeClass.getDeclaredMethod("putObject", Object.class, long.class, Object.class);
                    putObject.setAccessible(true);
                }
                putObject.invoke(unsafe, target, 0L, newClass); // offset 0 is first field
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the superclass pointer of a class to a different class at the native level.
     *
     * @param target       the class whose superclass should be changed.
     * @param newSuperClass the new superclass to assign.
     * @throws RuntimeException if the {@code Class.superClass} field is not found or inaccessible.
     */
    public static void setSuperClass(Class<?> target, Class<?> newSuperClass) {
        if (target.getSuperclass() == newSuperClass) return;
        if (superClassField == null) {
            try {
                // noinspection JavaReflectionMemberAccess
                superClassField = Class.class.getDeclaredField("superClass");
                superClassField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Class.superClass not found", e);
            }
        }
        try {
            superClassField.set(target, newSuperClass);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the memory offset of the given field within its declaring object.
     * <p>
     * Tries multiple approaches: Android-specific {@code Field.offset} field,
     * Android-specific {@code Field.getOffset()} method, and {@code Unsafe.objectFieldOffset()}.
     * </p>
     *
     * @param field the field to get the offset for.
     * @return the field offset in bytes.
     * @throws Exception if none of the offset retrieval methods are available.
     */
    public static int getFieldOffset(Field field) throws Exception {
        // 1. try Android-specific field
        try {
            Field offset = Field.class.getDeclaredField("offset");
            offset.setAccessible(true);
            return offset.getInt(field);
        } catch (Exception ignored) {
        }

        // 2. try Android-specific method
        try {
            @SuppressLint("DiscouragedPrivateApi") Method getOffset = Field.class.getDeclaredMethod("getOffset");
            getOffset.setAccessible(true);
            return (int) getOffset.invoke(field);
        } catch (Exception ignored) {
        }

        // 3. try Java traditional method
        // We assume that the field is non-static.
        ensureUnsafeReady();
        Method objectFieldOffset = unsafeClass.getDeclaredMethod("objectFieldOffset", Field.class);
        objectFieldOffset.setAccessible(true);
        return (int) objectFieldOffset.invoke(unsafe, field);
    }

    /**
     * Removes the {@code final} modifier from the given class by modifying its access flags
     * via the hidden {@code Class.accessFlags} field.
     *
     * @param target the class to remove the {@code final} flag from.
     * @throws RuntimeException if the {@code Class.accessFlags} field is not found or inaccessible.
     */
    public static void removeClassFinalFlag(Class<?> target) {
        if (!Modifier.isFinal(target.getModifiers())) return;
        if (classAccessFlagsField == null) {
            try {
                // noinspection JavaReflectionMemberAccess
                classAccessFlagsField = Class.class.getDeclaredField("accessFlags");
                classAccessFlagsField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Class.accessFlags not found", e);
            }
        }
        try {
            classAccessFlagsField.setInt(target, classAccessFlagsField.getInt(target) & ~Modifier.FINAL);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts an {@code int} value to a 4-byte array in little-endian order.
     *
     * @param value the integer value to convert.
     * @return a 4-element byte array in little-endian order.
     */
    public static byte[] int2Bytes(int value) {
        // Android only use little-endian.
        return new byte[] {
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 24) & 0xFF)
        };
    }

    /**
     * Converts a 4-byte array (little-endian) to an {@code int} value.
     *
     * @param src a 4-element byte array in little-endian order.
     * @return the reconstructed integer value.
     */
    public static int bytes2Int(byte[] src) {
        // Android only use little-endian.
        return (src[0] & 0xFF)
                | ((src[1] & 0xFF) << 8)
                | ((src[2] & 0xFF) << 16)
                | ((src[3] & 0xFF) << 24);
    }

    /**
     * Combines two {@code int} values into a {@code long} in little-endian order.
     *
     * @param l the low 32 bits.
     * @param h the high 32 bits.
     * @return the combined 64-bit long value.
     */
    public static long ints2Long(int l, int h) {
        // Android only use little-endian.
        return (((long) h) << 32) | (l & 0xffffffffL);
    }

    /**
     * Combines two {@code int} values into a {@code double} via bit reinterpretation
     * in little-endian order.
     *
     * @param a the low 32 bits.
     * @param b the high 32 bits.
     * @return the reconstructed double value.
     */
    public static double ints2Double(int a, int b) {
        return Double.longBitsToDouble(ints2Long(a, b));
    }

    /**
     * Combines two {@code float} values into a {@code double} by reinterpreting each float
     * as an int and combining them as a long in little-endian order.
     *
     * @param l the low float value.
     * @param h the high float value.
     * @return the reconstructed double value.
     */
    public static double floats2Double(float l, float h) {
        return Double.longBitsToDouble(ints2Long(Float.floatToIntBits(l), Float.floatToIntBits(h)));
    }

    /**
     * Rounds up an integer to the nearest even number.
     *
     * @param n the input value.
     * @return the value rounded up to the nearest even number (unchanged if already even).
     */
    public static int evenUp(int n) {
        if ((n & 1) == 1) {
            n++;
        }
        return n;
    }

    private static Object getUnsafe() throws Exception {
        try {
            // try Unsafe.getUnsafe()
            Method getUnsafe = unsafeClass.getDeclaredMethod("getUnsafe");
            getUnsafe.setAccessible(true);
            return getUnsafe.invoke(null);
        } catch (Exception ignored) {
        }

        Field theUnsafe;
        try {
            // try Unsafe.theUnsafe (art and hotspot vm)
            theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException ignored) {
            // try Unsafe.THE_ONE (art and dalvik vm)
            theUnsafe = unsafeClass.getDeclaredField("THE_ONE");
        }
        theUnsafe.setAccessible(true);
        return theUnsafe.get(null);
    }

    private static void ensureUnsafeReady() {
        if (unsafe != null) return;
        try {
            unsafeClass = Class.forName("sun.misc.Unsafe");
            unsafe = getUnsafe();
        } catch (Exception e) {
            throw new RuntimeException("Unsafe API is unavailable", e);
        }
    }
}
