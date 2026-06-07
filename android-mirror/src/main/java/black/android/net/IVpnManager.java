package black.android.net;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.net.IVpnManager.
 * AIDL interface for the VPN management system service.
 */
@BClassName("android.net.IVpnManager")
public interface IVpnManager {

    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.net.IVpnManager$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IVpnManager proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
