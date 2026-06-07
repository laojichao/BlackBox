package black.android.service.persistentdata;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.service.persistentdata.IPersistentDataBlockService.
 * AIDL interface for the persistent data block system service.
 */
@BClassName("android.service.persistentdata.IPersistentDataBlockService")
public interface IPersistentDataBlockService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.service.persistentdata.IPersistentDataBlockService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IPersistentDataBlockService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
