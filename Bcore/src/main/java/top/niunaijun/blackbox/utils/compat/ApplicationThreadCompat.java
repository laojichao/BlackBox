package top.niunaijun.blackbox.utils.compat;

import android.os.IBinder;
import android.os.IInterface;

import black.android.app.BRApplicationThreadNative;
import black.android.app.BRIApplicationThreadOreoStub;

/**
 * Compatibility wrapper for {@link android.app.ApplicationThreadNative} / IApplicationThread.
 * <p>
 * On Android 8.0 (Oreo) and above, {@code ApplicationThreadNative.asInterface()} was removed
 * and replaced with a stub-based approach via {@code ApplicationThreadOreoStub}. This class
 * abstracts that difference so callers can obtain an IApplicationThread proxy on any API level.
 */
public class ApplicationThreadCompat {

    /**
     * Converts an {@link IBinder} to an IApplicationThread interface proxy.
     * <p>
     * Uses the Oreo+ stub-based implementation when running on Android 8.0 or above,
     * and falls back to the legacy {@code ApplicationThreadNative.asInterface()} on older versions.
     *
     * @param binder the raw IBinder to convert
     * @return the IApplicationThread interface, or null if conversion fails
     */
    public static IInterface asInterface(IBinder binder) {
        if (BuildCompat.isOreo()) {
            return BRIApplicationThreadOreoStub.get().asInterface(binder);
        }
        return BRApplicationThreadNative.get().asInterface(binder);
    }
}
