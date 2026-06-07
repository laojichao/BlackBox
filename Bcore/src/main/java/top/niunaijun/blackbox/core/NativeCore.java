package top.niunaijun.blackbox.core;


import android.os.Process;

import androidx.annotation.Keep;

import java.io.File;
import java.util.List;

import dalvik.system.DexFile;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.utils.compat.DexFileCompat;

import static top.niunaijun.blackbox.core.env.BEnvironment.EMPTY_JAR;

/**
 * JNI bridge to the native {@code libblackbox} library.
 *
 * <p>Provides the low-level primitives used by the virtual engine:</p>
 * <ul>
 *   <li>Native library initialization and I/O rule management</li>
 *   <li>Uid remapping for cross-process calls</li>
 *   <li>Transparent filesystem path redirection at the native layer</li>
 *   <li>Xposed framework concealment</li>
 *   <li>Dex file inspection utilities</li>
 * </ul>
 *
 * <p>Methods annotated with {@code @Keep} are called from native code
 * via JNI callbacks and must not be renamed or removed by ProGuard.</p>
 */
public class NativeCore {
    public static final String TAG = "NativeCore";

    static {
        new File("");
        System.loadLibrary("blackbox");
    }

    /**
     * Initializes the native virtual engine with the given API level.
     *
     * @param apiLevel the Android SDK version (e.g. 30 for Android 11)
     */
    public static native void init(int apiLevel);

    /**
     * Enables the native I/O interception layer so that registered
     * redirect rules take effect for all subsequent file operations.
     */
    public static native void enableIO();

    /**
     * Registers a native I/O redirect rule.
     *
     * @param targetPath   the original filesystem prefix
     * @param relocatePath the replacement filesystem prefix
     */
    public static native void addIORule(String targetPath, String relocatePath);

    /**
     * Activates native-level Xposed framework concealment, preventing
     * apps from detecting Xposed-related artifacts in memory or on disk.
     */
    public static native void hideXposed();

    /**
     * Dumps all dex files loaded by the given class loader.
     *
     * <p>Currently a no-op (dump code is commented out) but retained
     * for future debugging use.</p>
     *
     * @param classLoader the class loader whose dex files to dump
     * @param packageName the package name, used as a subdirectory name
     */
    public static void dumpDex(ClassLoader classLoader, String packageName) {
        List<Long> cookies = DexFileCompat.getCookies(classLoader);
        for (Long cookie : cookies) {
            if (cookie == 0)
                continue;
//            File file = new File(BlackBoxCore.get().getDexDumpDir(), packageName);
//            FileUtils.mkdirs(file);
//            dumpDex(cookie, file.getAbsolutePath());
        }
    }

    /**
     * JNI callback that translates a caller's real UID into the virtual
     * UID used inside the BlackBox environment.
     *
     * <p>System UIDs (below {@code FIRST_APPLICATION_UID}) and UIDs
     * above {@code LAST_APPLICATION_UID} are returned unchanged.
     * If the UID belongs to the host process itself, the virtual
     * calling UID from {@link BActivityThread} is returned instead.</p>
     *
     * @param origCallingUid the original UID observed by the framework
     * @return the remapped UID for the virtual environment
     */
    @Keep
    public static int getCallingUid(int origCallingUid) {
        // 系统uid
        if (origCallingUid > 0 && origCallingUid < Process.FIRST_APPLICATION_UID)
            return origCallingUid;
        // 非用户应用
        if (origCallingUid > Process.LAST_APPLICATION_UID)
            return origCallingUid;

        if (origCallingUid == BlackBoxCore.getHostUid()) {
//            Log.d(TAG, "origCallingUid: " + origCallingUid + " => " + BActivityThread.getCallingBUid());
            return BActivityThread.getCallingBUid();
        }
        return origCallingUid;
    }

    /**
     * JNI callback that redirects a filesystem path string through the
     * {@link IOCore} redirect engine.
     *
     * @param path the original path
     * @return the redirected path
     */
    @Keep
    public static String redirectPath(String path) {
        return IOCore.get().redirectPath(path);
    }

    /**
     * JNI callback that redirects a {@link File} through the
     * {@link IOCore} redirect engine.
     *
     * @param path the original file
     * @return the redirected file
     */
    @Keep
    public static File redirectPath(File path) {
        return IOCore.get().redirectPath(path);
    }

    /**
     * Loads the {@code empty.apk} dex file and returns its native cookie
     * handles as an array of longs.
     *
     * <p>Used by native code to obtain a valid but empty dex when no real
     * application code should be loaded yet.</p>
     *
     * @return array of native dex cookie values, or an empty array on failure
     */
    @Keep
    public static long[] loadEmptyDex() {
        try {
            DexFile dexFile = new DexFile(EMPTY_JAR);
            List<Long> cookies = DexFileCompat.getCookies(dexFile);
            long[] longs = new long[cookies.size()];
            for (int i = 0; i < cookies.size(); i++) {
                longs[i] = cookies.get(i);
            }
            return longs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new long[]{};
    }
}
