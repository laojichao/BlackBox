package top.canyie.pine.utils;

import android.os.Build;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import top.canyie.pine.PineConfig;

/**
 * Utility class providing enhanced reflection capabilities for the Pine framework.
 * <p>
 * Includes methods to forcefully bypass accessibility checks, and to find fields, methods,
 * and constructors by traversing the class hierarchy. These utilities are essential for
 * accessing hidden or private members of Android framework classes.
 * </p>
 *
 * @author canyie
 */
public final class ReflectionHelper {
    private static Field override;

    private ReflectionHelper() {
    }

    /**
     * Forces the given reflection member to be accessible, bypassing standard Java access checks.
     * If {@link AccessibleObject#setAccessible(boolean)} fails, directly modifies the internal
     * {@code override} (or {@code flag} on older Android) field.
     *
     * @param member the accessible object (field, method, or constructor) to make accessible.
     * @throws SecurityException if the override field cannot be set.
     */
    public static void forceAccessible(AccessibleObject member) {
        try {
            member.setAccessible(true);
            if (member.isAccessible()) return;
        } catch (SecurityException ignored) {
        }

        if (override == null) {
            override = getField(AccessibleObject.class, PineConfig.sdkLevel >= Build.VERSION_CODES.N ? "override" : "flag");
        }

        try {
            override.setBoolean(member, true);
        } catch (IllegalAccessException e) {
            throw new SecurityException("Cannot set AccessibleObject.override", e);
        }
    }

    /**
     * Finds a declared field by name, searching the given class and its superclasses.
     *
     * @param c    the class to start searching from.
     * @param name the field name to find.
     * @return the accessible {@link Field}.
     * @throws IllegalArgumentException if the field is not found in the class hierarchy.
     */
    public static Field getField(Class<?> c, String name) {
        Field field = findField(c, name);
        if (field == null) throw new IllegalArgumentException("No field " + name + " found in " + c);
        return field;
    }

    /**
     * Searches for a declared field by name in the given class and its superclasses.
     * Unlike {@link #getField(Class, String)}, returns {@code null} instead of throwing
     * if the field is not found.
     *
     * @param c    the class to start searching from.
     * @param name the field name to find.
     * @return the accessible {@link Field}, or {@code null} if not found.
     */
    public static Field findField(Class<?> c, String name) {
        for (;c != null;c = c.getSuperclass()) {
            try {
                Field field = c.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

    /**
     * Finds a declared method by name and parameter types, searching the given class and its superclasses.
     *
     * @param c          the class to start searching from.
     * @param name       the method name to find.
     * @param paramTypes the parameter types of the method.
     * @return the accessible {@link Method}.
     * @throws IllegalArgumentException if the method is not found in the class hierarchy.
     */
    public static Method getMethod(Class<?> c, String name, Class<?>... paramTypes) {
        Method method = findMethod(c, name, paramTypes);
        if (method == null)
            throw new IllegalArgumentException("No method " + name + " with params " + Arrays.toString(paramTypes) + " found in " + c);
        return method;
    }

    /**
     * Searches for a declared method by name and parameter types in the given class and its superclasses.
     * Unlike {@link #getMethod(Class, String, Class...)}, returns {@code null} instead of throwing
     * if the method is not found.
     *
     * @param c          the class to start searching from.
     * @param name       the method name to find.
     * @param paramTypes the parameter types of the method.
     * @return the accessible {@link Method}, or {@code null} if not found.
     */
    public static Method findMethod(Class<?> c, String name, Class<?>... paramTypes) {
        for (;c != null;c = c.getSuperclass()) {
            try {
                Method method = c.getDeclaredMethod(name, paramTypes);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }

    /**
     * Finds a declared constructor with the given parameter types and makes it accessible.
     *
     * @param <T>        the type of the declaring class.
     * @param c          the class to search.
     * @param paramTypes the parameter types of the constructor.
     * @return the accessible {@link Constructor}.
     * @throws IllegalArgumentException if no constructor with the given parameter types is found.
     */
    public static <T> Constructor<T> getConstructor(Class<T> c, Class<?>... paramTypes) {
        try {
            Constructor<T> constructor = c.getDeclaredConstructor(paramTypes);
            forceAccessible(constructor);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No constructor found with params " + Arrays.toString(paramTypes), e);
        }
    }

    /**
     * Searches for a declared constructor with the given parameter types and makes it accessible.
     * Unlike {@link #getConstructor(Class, Class...)}, returns {@code null} instead of throwing
     * if the constructor is not found.
     *
     * @param <T>        the type of the declaring class.
     * @param c          the class to search.
     * @param paramTypes the parameter types of the constructor.
     * @return the accessible {@link Constructor}, or {@code null} if not found.
     */
    public static <T> Constructor<T> findConstructor(Class<T> c, Class<?>... paramTypes) {
        try {
            Constructor<T> constructor = c.getDeclaredConstructor(paramTypes);
            forceAccessible(constructor);
            return constructor;
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}
