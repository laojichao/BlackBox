package black.android.net.wifi;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.net.wifi.WifiScanner.
 * Provides access to hidden Wi-Fi scanner constants.
 */
@BClassName("android.net.wifi.WifiScanner")
public interface WifiScanner {
    /** Extra key for available Wi-Fi channels in scan results. */
    @BStaticField
    String GET_AVAILABLE_CHANNELS_EXTRA();
}
