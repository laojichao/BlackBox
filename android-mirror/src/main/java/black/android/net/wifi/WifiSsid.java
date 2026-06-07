package black.android.net.wifi;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.net.wifi.WifiSsid.
 * Provides access to the hidden WifiSsid factory method.
 */
@BClassName("android.net.wifi.WifiSsid")
public interface WifiSsid {
    /**
     * Create a WifiSsid from an ASCII-encoded SSID string.
     */
    @BStaticMethod
    Object createFromAsciiEncoded(String asciiEncoded);
}
