package black.android.app;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.usage.IUsageStatsManager}.
 * Provides access to the Usage Stats Manager AIDL service stub.
 */
@BClassName("android.app.usage.IUsageStatsManager")
public interface IUsageStatsManager {
    /**
     * Reflection mirror for {@code android.app.usage.IUsageStatsManager.Stub}.
     * Converts an IBinder to the IUsageStatsManager interface.
     */
    @BClassName("android.app.usage.IUsageStatsManager$Stub")
    interface Stub {
        /** Converts a raw IBinder to the IUsageStatsManager proxy interface. */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
