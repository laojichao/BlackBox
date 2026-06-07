package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.util.Log;

import java.lang.reflect.Method;

import black.android.net.wifi.BRIWifiManagerStub;
import black.android.net.wifi.BRWifiInfo;
import black.android.net.wifi.BRWifiSsid;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;

/**
 * Proxy for the Android {@code wifi} system service.
 * <p>
 * Intercepts Wi-Fi connection info queries, replacing the BSSID, MAC address,
 * and SSID with virtual values to prevent the virtual environment from
 * revealing the real Wi-Fi network information.
 */
public class IWifiManagerProxy extends BinderInvocationStub {
    /** Logging tag for this proxy class. */
    public static final String TAG = "IWifiManagerProxy";

    /**
     * Constructs a new proxy by acquiring the real Wi-Fi manager binder service.
     */
    public IWifiManagerProxy() {
        super(BRServiceManager.get().getService(Context.WIFI_SERVICE));
    }

    /**
     * Returns the underlying Wi-Fi manager service interface.
     *
     * @return the real {@code IWifiManager} binder interface
     */
    @Override
    protected Object getWho() {
        return BRIWifiManagerStub.get().asInterface(BRServiceManager.get().getService(Context.WIFI_SERVICE));
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.WIFI_SERVICE);
    }

    /**
     * Checks whether the current environment is invalid for this proxy.
     *
     * @return always {@code false}, indicating the environment is always valid
     */
    @Override
    public boolean isBadEnv() {
        return false;
    }

    /**
     * Hook that intercepts {@code getConnectionInfo} and replaces real Wi-Fi
     * identifiers (BSSID, MAC, SSID) with virtual values.
     */
    @ProxyMethod("getConnectionInfo")
    public static class GetConnectionInfo extends MethodHook {
        /**
         * Replaces the real Wi-Fi connection info fields with virtual values.
         * Uses reflection because {@link WifiInfo} has no public setter for BSSID/SSID.
         *
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return a modified {@link WifiInfo} with virtual network identifiers
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            WifiInfo wifiInfo = (WifiInfo) method.invoke(who, args);
            BRWifiInfo.get(wifiInfo)._set_mBSSID("ac:62:5a:82:65:c4");
            BRWifiInfo.get(wifiInfo)._set_mMacAddress("ac:62:5a:82:65:c4");
            BRWifiInfo.get(wifiInfo)._set_mWifiSsid(BRWifiSsid.get().createFromAsciiEncoded("BlackBox_Wifi"));
            return wifiInfo;
        }

        /**
         * Converts an integer IP address to a dotted-decimal string.
         *
         * @param ip the IP address as an integer
         * @return the IP address in "x.x.x.x" format
         */
        public static String intIP2StringIP(int ip) {
            return (ip & 0xFF) + "." +
                    ((ip >> 8) & 0xFF) + "." +
                    ((ip >> 16) & 0xFF) + "." +
                    (ip >> 24 & 0xFF);
        }

        /**
         * Converts a dotted-decimal IP address string to an integer.
         *
         * @param ipString the IP address in "x.x.x.x" format
         * @return the IP address as an integer
         */
        public static int ip2Int(String ipString) {
            // 取 ip 的各段
            String[] ipSlices = ipString.split("\\.");
            int rs = 0;
            for (int i = 0; i < ipSlices.length; i++) {
                // 将 ip 的每一段解析为 int，并根据位置左移 8 位
                int intSlice = Integer.parseInt(ipSlices[i]) << 8 * i;
                // 或运算
                rs = rs | intSlice;
            }
            return rs;
        }
    }
}
