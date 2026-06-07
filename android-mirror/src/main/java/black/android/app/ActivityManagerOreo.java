package black.android.app;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.ActivityManager} (Oreo+ / API 26+).
 * Provides access to the IActivityManager service singleton introduced
 * in Android Oreo, replacing the legacy ActivityManagerNative path.
 */
@BClassName("android.app.ActivityManager")
public interface ActivityManagerOreo {
    /** Returns the hidden static {@code IActivityManagerSingleton} field. */
    @BStaticField
    Object IActivityManagerSingleton();

    /** Returns the IActivityManager service interface via the singleton. */
    @BStaticMethod
    IInterface getService();
}
