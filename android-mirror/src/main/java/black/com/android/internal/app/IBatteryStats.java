package black.com.android.internal.app;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.app.IBatteryStats.
 * AIDL interface for the battery statistics system service.
 */
@BClassName("com.android.internal.app.IBatteryStats")
public interface IBatteryStats {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("com.android.internal.app.IBatteryStats$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IBatteryStats proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
