package top.canyie.pine;

import android.os.Build;

/**
 * Configuration holder for the Pine method hooking framework.
 * <p>
 * All fields are static and should be set before calling {@link Pine#ensureInitialized()}.
 * After initialization, some fields (e.g. {@link #debuggable}) can be changed at runtime
 * via Pine's API methods.
 * </p>
 *
 * @author canyie
 */
@SuppressWarnings("WeakerAccess") public final class PineConfig {
    /** The Android SDK level detected at startup. Automatically adjusted for preview releases. */
    public static int sdkLevel;
    /**
     * Whether we need to print more detailed logs.
     */
    public static boolean debug = true;

    /**
     * Whether the current process is debuggable.
     */
    public static boolean debuggable;

    /**
     * Whether all Pine hooks are disabled globally. When {@code true}, hooked methods
     * bypass all callbacks and execute their original implementation directly.
     */
    public static boolean disableHooks;

    /**
     * Internal API. Whether we should use fast-native to speedup JNI method calling.
     * Only effective on Android Lollipop (API 21) and above.
     */
    public static boolean useFastNative;
    /** Set to true to hide certain Pine features. Some debug information may be erased. */
    public static boolean antiChecks;
    /** Set to true to disable the hidden API policy for application domain (non-SDK interface restrictions). */
    public static boolean disableHiddenApiPolicy = true;
    /** Set to true to disable the hidden API policy for platform domain (system app restrictions). */
    public static boolean disableHiddenApiPolicyForPlatformDomain = true;

    /**
     * The function responsible for loading the native library (libpine.so).
     * Defaults to {@code System.loadLibrary("pine")}.
     *
     * @see Pine.LibLoader
     */
    public static Pine.LibLoader libLoader = new Pine.LibLoader() {
        @Override public void loadLib() {
            System.loadLibrary("pine");
        }
    };

    static {
        sdkLevel = Build.VERSION.SDK_INT;
        if (sdkLevel == 30 && Build.VERSION.PREVIEW_SDK_INT > 0) {
            // Android S Preview
            sdkLevel = 31;
        }
    }

    private PineConfig() {
        throw new RuntimeException();
    }
}
