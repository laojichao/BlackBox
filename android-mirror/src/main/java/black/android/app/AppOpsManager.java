package black.android.app;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Reflection mirror for {@code android.app.AppOpsManager}.
 * Provides access to the hidden {@code mService} field, which is
 * the IAppOpsService binder proxy for interacting with the app ops system.
 */
@BClassName("android.app.AppOpsManager")
public interface AppOpsManager {
    /** Returns the hidden {@code mService} field referencing the IAppOpsService binder. */
    @BField
    IInterface mService();
}
