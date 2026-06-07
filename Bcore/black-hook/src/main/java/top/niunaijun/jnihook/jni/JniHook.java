package top.niunaijun.jnihook.jni;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * JNI-level native hooking interface for manipulating ART method and field access at runtime.
 * <p>
 * This class provides native methods to:
 * <ul>
 *   <li>Calculate native memory offsets used by the hooking mechanism ({@link #NATIVE_OFFSET}, {@link #NATIVE_OFFSET_2})</li>
 *   <li>Force accessibility of methods and fields by modifying their ART-level access flags</li>
 * </ul>
 * The native methods interact directly with ART internal structures to bypass Java-level
 * access restrictions that standard reflection cannot overcome.
 * </p>
 *
 * @author Milk
 */
public final class JniHook {
    /** The native memory offset calculated by {@link #nativeOffset()}, used for ART method manipulation. */
    public static final int NATIVE_OFFSET = 0;

    /** A secondary native memory offset calculated by {@link #nativeOffset2()}. */
    public static final int NATIVE_OFFSET_2 = 0;

    /**
     * Native method that calculates and stores the primary native memory offset.
     * Called during initialization to determine ART internal layout.
     */
    public static final native void nativeOffset();

    /**
     * Native method that calculates and stores the secondary native memory offset.
     * Called during initialization for additional ART structure information.
     */
    public static final native void nativeOffset2();

    /**
     * Forces the given method to be accessible by modifying its ART-level access flags.
     *
     * @param clazz  the declaring class of the method.
     * @param method the method to make accessible.
     */
    public static native void setAccessible(Class<?> clazz, Method method);

    /**
     * Forces the given field to be accessible by modifying its ART-level access flags.
     *
     * @param clazz the declaring class of the field.
     * @param field the field to make accessible.
     */
    public static native void setAccessible(Class<?> clazz, Field field);
}
