package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.os.IBinder;

import black.android.net.wifi.BRIWifiManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;

/**
 * Proxy for the Android {@code wifiscanner} system service.
 * <p>
 * Intercepts Wi-Fi scanner service calls to prevent virtual environment apps
 * from accessing real Wi-Fi scanning functionality on the host device.
 */
public class IWifiScannerProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by acquiring the real Wi-Fi scanner binder service.
     */
    public IWifiScannerProxy() {
        super(BRServiceManager.get().getService("wifiscanner"));
    }

    /**
     * Returns the underlying Wi-Fi scanner service interface.
     *
     * @return the real Wi-Fi scanner binder interface
     */
    @Override
    protected Object getWho() {
        return BRIWifiManagerStub.get().asInterface(BRServiceManager.get().getService("wifiscanner"));
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("wifiscanner");
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
}
