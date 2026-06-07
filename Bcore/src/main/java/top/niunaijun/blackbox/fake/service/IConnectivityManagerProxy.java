package top.niunaijun.blackbox.fake.service;

import android.content.Context;

import black.android.net.BRIConnectivityManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.ScanClass;

/**
 * Proxy for the Android Connectivity Manager system service (IConnectivityManager).
 * Intercepts network connectivity operations and works in conjunction with
 * {@link VpnCommonProxy} to handle VPN-related network requests within the
 * virtual environment. Delegates most operations directly to the real service.
 *
 * @author Milk
 */
@ScanClass(VpnCommonProxy.class)
public class IConnectivityManagerProxy extends BinderInvocationStub {
    /** Tag for logging. */
    public static final String TAG = "IConnectivityManagerProxy";

    /**
     * Constructs a new proxy by obtaining the Connectivity Manager binder service.
     */
    public IConnectivityManagerProxy() {
        super(BRServiceManager.get().getService(Context.CONNECTIVITY_SERVICE));
    }

    /**
     * Returns the IConnectivityManager interface instance from the system service.
     *
     * @return the original IConnectivityManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRIConnectivityManagerStub.get().asInterface(BRServiceManager.get().getService(Context.CONNECTIVITY_SERVICE));
    }

    /**
     * Replaces the system Connectivity Manager service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Checks if the environment has been corrupted by another proxy.
     *
     * @return always returns false
     */
    @Override
    public boolean isBadEnv() {
        return false;
    }
}
