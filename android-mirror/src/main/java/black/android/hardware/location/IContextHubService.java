package black.android.hardware.location;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.hardware.location.IContextHubService.
 * AIDL interface for the Context Hub (sensor hub) system service.
 */
@BClassName("android.hardware.location.IContextHubService")
public interface IContextHubService {

    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.hardware.location.IContextHubService$Stub")
    interface Stub {
        /** Converts a raw IBinder to an IContextHubService proxy interface. */
        @BStaticMethod
        IInterface asInterface(IBinder iBinder);
    }
}
