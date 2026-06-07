package black.android.os.mount;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.storage.IMountService (legacy mount interface).
 * AIDL interface for the storage mount system service.
 */
@BClassName("android.os.storage.IMountService")
public interface IMountService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.os.storage.IMountService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IMountService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
