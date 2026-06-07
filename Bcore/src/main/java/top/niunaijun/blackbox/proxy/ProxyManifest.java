package top.niunaijun.blackbox.proxy;

import java.util.Locale;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Utility class that generates component class names and authorities for proxy stubs
 * declared in the host app's manifest. Provides methods to look up proxy Activity,
 * Service, ContentProvider, BroadcastReceiver, and process names by index. The proxy
 * component pool ({@link #FREE_COUNT} slots) enables concurrent virtual app instances
 * to use distinct manifest-registered components.
 *
 * @author Milk
 */
public class ProxyManifest {
    /** Total number of proxy component slots available (P0 through P49). */
    public static final int FREE_COUNT = 50;

    /**
     * Checks whether the given authority string belongs to a proxy content provider.
     *
     * @param msg the authority string to check
     * @return {@code true} if the string matches the bind provider or a proxy content provider
     */
    public static boolean isProxy(String msg) {
        return getBindProvider().equals(msg) || msg.contains("proxy_content_provider_");
    }

    /**
     * Returns the authority of the system call provider used for host-to-server IPC.
     *
     * @return the fully qualified authority string for the bind provider
     */
    public static String getBindProvider() {
        return BlackBoxCore.getHostPkg() + ".blackbox.SystemCallProvider";
    }

    /**
     * Returns the content provider authority for a given proxy slot index.
     *
     * @param index the proxy slot index (0-49)
     * @return the authority string for this proxy content provider
     */
    public static String getProxyAuthorities(int index) {
        return String.format(Locale.CHINA, "%s.proxy_content_provider_%d", BlackBoxCore.getHostPkg(), index);
    }

    /**
     * Returns the fully qualified class name for a pending activity proxy slot.
     *
     * @param index the proxy slot index (0-49)
     * @return the class name of the proxy pending activity
     */
    public static String getProxyPendingActivity(int index) {
        return String.format(Locale.CHINA, "top.niunaijun.blackbox.proxy.ProxyPendingActivity$P%d", index);
    }

    /**
     * Returns the fully qualified class name for an activity proxy slot.
     *
     * @param index the proxy slot index (0-49)
     * @return the class name of the proxy activity
     */
    public static String getProxyActivity(int index) {
        return String.format(Locale.CHINA, "top.niunaijun.blackbox.proxy.ProxyActivity$P%d", index);
    }

    /**
     * Returns the fully qualified class name for a transparent proxy activity slot.
     *
     * @param index the proxy slot index (0-49)
     * @return the class name of the transparent proxy activity
     */
    public static String TransparentProxyActivity(int index) {
        return String.format(Locale.CHINA, "top.niunaijun.blackbox.proxy.TransparentProxyActivity$P%d", index);
    }

    /**
     * Returns the fully qualified class name for a service proxy slot.
     *
     * @param index the proxy slot index (0-49)
     * @return the class name of the proxy service
     */
    public static String getProxyService(int index) {
        return String.format(Locale.CHINA, "top.niunaijun.blackbox.proxy.ProxyService$P%d", index);
    }

    /**
     * Returns the fully qualified class name for a job service proxy slot.
     *
     * @param index the proxy slot index (0-49)
     * @return the class name of the proxy job service
     */
    public static String getProxyJobService(int index) {
        return String.format(Locale.CHINA, "top.niunaijun.blackbox.proxy.ProxyJobService$P%d", index);
    }

    /**
     * Returns the authority for the proxy FileProvider.
     *
     * @return the fully qualified FileProvider authority string
     */
    public static String getProxyFileProvider() {
        return BlackBoxCore.getHostPkg() + ".blackbox.FileProvider";
    }

    /**
     * Returns the fully qualified class name of the proxy stub receiver.
     *
     * @return the stub receiver class name
     */
    public static String getProxyReceiver() {
        return BlackBoxCore.getHostPkg() + ".stub_receiver";
    }

    /**
     * Returns the process name for a virtual app with the given black PID.
     *
     * @param bPid the virtual process ID
     * @return the process name string in the format {@code <hostPkg>:p<bPid>}
     */
    public static String getProcessName(int bPid) {
        return BlackBoxCore.getHostPkg() + ":p" + bPid;
    }
}
