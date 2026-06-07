package top.niunaijun.blackbox.utils.compat;

import android.text.TextUtils;

import top.niunaijun.blackbox.utils.Reflector;


/**
 * Compatibility wrapper for reading {@code android.os.SystemProperties}.
 * <p>
 * {@code SystemProperties} is a hidden Android system API that provides access to
 * the system property store. This class uses reflection to invoke its methods so that
 * the virtual environment can read device properties (e.g. {@code ro.build.version.sdk})
 * without requiring compile-time access to the hidden API.
 */
public class SystemPropertiesCompat {

    /**
     * Retrieves the string value of a system property, returning a default if not set.
     *
     * @param key the property name (e.g. {@code "ro.build.display.id"})
     * @param def the default value to return if the property is not set or the call fails
     * @return the property value, or {@code def} if the property is absent or an error occurs
     */
    public static String get(String key, String def) {
        try {
            return (String) Reflector.on("android.os.SystemProperties")
                    .method("get", String.class, String.class)
                    .call(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    /**
     * Retrieves the string value of a system property without a default.
     *
     * @param key the property name (e.g. {@code "ro.product.model"})
     * @return the property value, or {@code null} if the property is not set or an error occurs
     */
    public static String get(String key) {
        try {
            return (String) Reflector.on("android.os.SystemProperties")
                    .method("get", String.class)
                    .call(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks whether a system property is set (non-empty).
     *
     * @param key the property name to check
     * @return {@code true} if the property exists and its value is non-empty, {@code false} otherwise
     */
    public static boolean isExist(String key) {
        return !TextUtils.isEmpty(get(key));
    }

    /**
     * Retrieves the integer value of a system property, returning a default if not set.
     *
     * @param key the property name (e.g. {@code "ro.build.version.sdk"})
     * @param def the default value to return if the property is not set or the call fails
     * @return the integer property value, or {@code def} if the property is absent or an error occurs
     */
    public static int getInt(String key, int def) {
        try {
            return (int) Reflector.on("android.os.SystemProperties")
                    .method("getInt", String.class, int.class)
                    .call(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

}
