package black.android.os.storage;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.storage.IStorageManager.
 * AIDL interface for the storage management system service.
 */
@BClassName("android.os.storage.IStorageManager")
public interface IStorageManager {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.os.storage.IStorageManager$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IStorageManager proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
