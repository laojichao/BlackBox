package black.android.net.wifi;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.net.wifi.WifiManager fields.
 * Provides access to the internal IWifiManager service binder.
 */
@BClassName("android.net.wifi.WifiManager")
public interface WifiManager {
    /** Static reference to the IWifiManager service. */
    @BStaticField
    IInterface sService();

    /** Instance reference to the IWifiManager service. */
    @BField
    IInterface mService();
}
