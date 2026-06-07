package black.android.os;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.INetworkManagementService.
 * AIDL interface for the network management system service.
 */
@BClassName("android.os.INetworkManagementService")
public interface INetworkManagementService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.os.INetworkManagementService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the INetworkManagementService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
