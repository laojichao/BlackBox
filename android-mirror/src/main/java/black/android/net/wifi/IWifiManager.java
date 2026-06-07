package black.android.net.wifi;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.net.wifi.IWifiManager.
 * AIDL interface for the Wi-Fi management system service.
 */
@BClassName("android.net.wifi.IWifiManager")
public interface IWifiManager {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.net.wifi.IWifiManager$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IWifiManager proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
