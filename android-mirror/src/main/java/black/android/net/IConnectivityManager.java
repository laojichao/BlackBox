package black.android.net;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.net.IConnectivityManager.
 * AIDL interface for the connectivity management system service.
 */
@BClassName("android.net.IConnectivityManager")
public interface IConnectivityManager {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.net.IConnectivityManager$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IConnectivityManager proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
