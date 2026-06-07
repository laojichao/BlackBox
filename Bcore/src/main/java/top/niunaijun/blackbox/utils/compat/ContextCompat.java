package top.niunaijun.blackbox.utils.compat;

import android.content.Context;
import android.content.ContextWrapper;

import black.android.app.BRContextImpl;
import black.android.app.BRContextImplKitkat;
import black.android.content.AttributionSourceStateContext;
import black.android.content.BRAttributionSource;
import black.android.content.BRAttributionSourceState;
import black.android.content.BRContentResolver;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;

/**
 * Compatibility utility for fixing {@link android.content.Context} internals within the virtual environment.
 * <p>
 * Patches the base context's package name, content resolver package name, and (on Android 12+)
 * the {@code AttributionSourceState} to match the host application, ensuring that system
 * services attribute calls to the correct package. Handles deep {@link ContextWrapper} chains
 * up to 10 levels to reach the underlying {@code ContextImpl}.
 */
public class ContextCompat {
    public static final String TAG = "ContextCompat";

    /**
     * Fixes the {@code AttributionSourceState} chain on an {@code AttributionSource} object.
     * <p>
     * Sets the package name to the host package and the UID to the specified virtual UID
     * on each node in the linked AttributionSource chain. Used on Android 12 (S) and above
     * where the attribution source is checked by the system.
     *
     * @param obj the root AttributionSource object to fix (may be null)
     * @param uid the virtual user ID to set in the attribution source
     */
    public static void fixAttributionSourceState(Object obj, int uid) {
        Object mAttributionSourceState;
        if (obj != null && BRAttributionSource.get(obj)._check_mAttributionSourceState() != null) {
            mAttributionSourceState = BRAttributionSource.get(obj).mAttributionSourceState();

            AttributionSourceStateContext attributionSourceStateContext = BRAttributionSourceState.get(mAttributionSourceState);
            attributionSourceStateContext._set_packageName(BlackBoxCore.getHostPkg());
            attributionSourceStateContext._set_uid(uid);
            fixAttributionSourceState(BRAttributionSource.get(obj).getNext(), uid);
        }
    }

    /**
     * Fixes the given context for the virtual environment by patching its internal fields.
     * <p>
     * Unwraps any {@link ContextWrapper} layers, resets the PackageManager to force
     * re-initialization, sets the base package name and operation package name to the
     * host package, and patches the content resolver. On Android 12+, also fixes the
     * {@code AttributionSourceState}.
     *
     * @param context the context instance to fix
     */
    public static void fix(Context context) {
        try {
            int deep = 0;
            while (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
                deep++;
                if (deep >= 10) {
                    return;
                }
            }
            BRContextImpl.get(context)._set_mPackageManager(null);
            try {
                context.getPackageManager();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            BRContextImpl.get(context)._set_mBasePackageName(BlackBoxCore.getHostPkg());
            BRContextImplKitkat.get(context)._set_mOpPackageName(BlackBoxCore.getHostPkg());
            BRContentResolver.get(context.getContentResolver())._set_mPackageName(BlackBoxCore.getHostPkg());

            if (BuildCompat.isS()) {
                fixAttributionSourceState(BRContextImpl.get(context).getAttributionSource(), BActivityThread.getBUid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
