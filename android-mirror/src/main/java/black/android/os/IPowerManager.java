package black.android.os;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.IPowerManager.
 * AIDL interface for the power management system service.
 */
@BClassName("android.os.IPowerManager")
public interface IPowerManager {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.os.IPowerManager$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IPowerManager proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
