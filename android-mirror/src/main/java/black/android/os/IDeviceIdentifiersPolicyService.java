package black.android.os;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.IDeviceIdentifiersPolicyService.
 * AIDL interface for accessing device identifiers with permission checks.
 */
@BClassName("android.os.IDeviceIdentifiersPolicyService")
public interface IDeviceIdentifiersPolicyService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.os.IDeviceIdentifiersPolicyService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IDeviceIdentifiersPolicyService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
