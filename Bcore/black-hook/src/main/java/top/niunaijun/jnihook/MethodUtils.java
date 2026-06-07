package top.niunaijun.jnihook;

import androidx.annotation.Keep;

import java.lang.reflect.Method;

/**
 * Utility class for extracting JNI-compatible method descriptors from Java reflection objects.
 * <p>
 * Provides methods to obtain the declaring class name (in JNI internal format), method name,
 * and full JNI method descriptor (e.g., "(ILjava/lang/String;V") for a given {@link Method}.
 * These descriptors are used by the native JNI hooking code to locate and replace method entries.
 * </p>
 *
 * @author Milk
 */
@Keep
public class MethodUtils {

    /**
     * Returns the JNI internal name of the declaring class of the given method.
     * Dots are replaced with slashes (e.g., "java/lang/String").
     *
     * @param method the method whose declaring class name to retrieve.
     * @return the declaring class name in JNI internal format.
     */
    // native call
    public static String getDeclaringClass(final Method method) {
        return method.getDeclaringClass().getName().replace(".", "/");
    }

    /**
     * Returns the name of the given method.
     *
     * @param method the method whose name to retrieve.
     * @return the method name.
     */
    // native call
    public static String getMethodName(final Method method) {
        return method.getName();
    }

    /**
     * Returns the full JNI method descriptor for the given method.
     * The descriptor encodes parameter types and return type in JNI format
     * (e.g., "(ILjava/lang/String;)V" for a method taking int and String, returning void).
     *
     * @param method the method whose descriptor to compute.
     * @return the JNI method descriptor string.
     */
    // native call
    public static String getDesc(final Method method) {
        final StringBuffer buf = new StringBuffer();
        buf.append("(");
        final Class<?>[] types = method.getParameterTypes();
        for (int i = 0; i < types.length; ++i) {
            buf.append(getDesc(types[i]));
        }
        buf.append(")");
        buf.append(getDesc(method.getReturnType()));
        return buf.toString();
    }

    private static String getDesc(final Class<?> returnType) {
        if (returnType.isPrimitive()) {
            return getPrimitiveLetter(returnType);
        }
        if (returnType.isArray()) {
            return "[" + getDesc(returnType.getComponentType());
        }
        return "L" + getType(returnType) + ";";
    }

    private static String getType(final Class<?> parameterType) {
        if (parameterType.isArray()) {
            return "[" + getDesc(parameterType.getComponentType());
        }
        if (!parameterType.isPrimitive()) {
            final String clsName = parameterType.getName();
            return clsName.replaceAll("\\.", "/");
        }
        return getPrimitiveLetter(parameterType);
    }

    private static String getPrimitiveLetter(final Class<?> type) {
        if (Integer.TYPE.equals(type)) {
            return "I";
        }
        if (Void.TYPE.equals(type)) {
            return "V";
        }
        if (Boolean.TYPE.equals(type)) {
            return "Z";
        }
        if (Character.TYPE.equals(type)) {
            return "C";
        }
        if (Byte.TYPE.equals(type)) {
            return "B";
        }
        if (Short.TYPE.equals(type)) {
            return "S";
        }
        if (Float.TYPE.equals(type)) {
            return "F";
        }
        if (Long.TYPE.equals(type)) {
            return "J";
        }
        if (Double.TYPE.equals(type)) {
            return "D";
        }
        throw new IllegalStateException("Type: " + type.getCanonicalName() + " is not a primitive type");
    }
}