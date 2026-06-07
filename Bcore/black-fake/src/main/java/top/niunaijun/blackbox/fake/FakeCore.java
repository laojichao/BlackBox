package top.niunaijun.blackbox.fake;

import top.niunaijun.jnihook.ReflectCore;

/**
 * Initialization class for the black-fake module that prepares core Android framework classes
 * for hooking within the BlackBox virtual environment.
 * <p>
 * This class uses the JNI hooking mechanism ({@link ReflectCore}) to make key framework
 * classes and their members accessible, enabling interception of Android system calls
 * at the ART level.
 * </p>
 *
 * @author Milk
 */
public class FakeCore {
    /**
     * Initializes the fake environment by making {@link android.app.ActivityThread}
     * and its members accessible for hooking. This is a prerequisite for intercepting
     * Android application lifecycle events within the virtual environment.
     */
    public static void init() {
        ReflectCore.set(android.app.ActivityThread.class);
    }
}
