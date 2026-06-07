package black.android.app;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.ActivityManagerNative} (pre-Oreo).
 * Provides access to the global default IActivityManager binder proxy
 * used for interacting with the Activity Manager Service.
 */
@BClassName("android.app.ActivityManagerNative")
public interface ActivityManagerNative {
    /** Returns the hidden static {@code gDefault} field holding the default IActivityManager singleton. */
    @BStaticField
    Object gDefault();

    /** Returns the default IActivityManager interface instance. */
    @BStaticMethod
    IInterface getDefault();
}
