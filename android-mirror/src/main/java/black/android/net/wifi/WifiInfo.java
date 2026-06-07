package black.android.net.wifi;

import android.net.wifi.SupplicantState;

import java.net.InetAddress;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.net.wifi.WifiInfo constructors and fields.
 * Allows constructing and manipulating Wi-Fi connection details.
 */
@BClassName("android.net.wifi.WifiInfo")
public interface WifiInfo {
    /** Construct a new empty WifiInfo instance. */
    @BConstructor
    WifiInfo _new();

    /** The BSSID (AP MAC address) of the connected access point. */
    @BField
    String mBSSID();

    /** The frequency (MHz) of the connected channel. */
    @BField
    int mFrequency();

    /** The device IP address on the Wi-Fi network. */
    @BField
    InetAddress mIpAddress();

    /** The link speed (Mbps) to the access point. */
    @BField
    int mLinkSpeed();

    /** The device MAC address. */
    @BField
    String mMacAddress();

    /** The Wi-Fi network ID. */
    @BField
    int mNetworkId();

    /** The received signal strength indicator (dBm). */
    @BField
    int mRssi();

    /** The SSID (network name) of the connected access point. */
    @BField
    String mSSID();

    /** The current supplicant (WPA) state. */
    @BField
    SupplicantState mSupplicantState();

    /** The WifiSsid object for the connected network. */
    @BField
    Object mWifiSsid();
}
