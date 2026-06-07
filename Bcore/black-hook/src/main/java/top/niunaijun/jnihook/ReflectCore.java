package top.niunaijun.jnihook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import top.niunaijun.jnihook.jni.JniHook;

/**
 * Utility class for making classes and their members fully accessible via reflection.
 * <p>
 * Used by the black-hook framework to bypass Java access restrictions on classes that need
 * to be hooked. Recursively makes all declared methods, fields, and inner classes of a given
 * class publicly accessible, and sets the class's access flags to public.
 * </p>
 *
 * @author Milk
 */
public class ReflectCore {

    /**
     * Makes the given class and all its members (methods, fields, inner classes) accessible.
     * <p>
     * Sets the class's {@code accessFlags} to include the {@code PUBLIC} modifier,
     * then recursively sets all declared methods, fields, and inner classes to be accessible.
     * </p>
     *
     * @param clazz the class to make fully accessible.
     */
    public static void set(Class<?> clazz) {
        try {
            Field accessFlags = Class.class.getDeclaredField("accessFlags");
            accessFlags.setAccessible(true);
            int o = (int) accessFlags.get(clazz);
            accessFlags.set(clazz, o | 0x0001);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            JniHook.setAccessible(clazz, declaredMethod);
        }
        for (Field declaredField : clazz.getDeclaredFields()) {
            JniHook.setAccessible(clazz, declaredField);
        }
        for (Class<?> declaredClass : clazz.getDeclaredClasses()) {
            set(declaredClass);
        }
    }
}
