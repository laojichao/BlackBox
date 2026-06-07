package black.com.android.internal.app;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.app.ISmtOpsService.
 * AIDL interface for the Smart Ops system service (device-specific).
 */
@BClassName("com.android.internal.app.ISmtOpsService")
public interface ISmtOpsService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("com.android.internal.app.ISmtOpsService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the ISmtOpsService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
