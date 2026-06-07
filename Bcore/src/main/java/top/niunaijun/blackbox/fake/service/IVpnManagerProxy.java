package top.niunaijun.blackbox.fake.service;

import black.android.net.BRIVpnManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.ScanClass;

/**
 * Proxy for the Android {@code vpn_management} system service.
 * <p>
 * Intercepts VPN management operations by replacing the system service
 * and scanning {@link VpnCommonProxy} for additional VPN-related method hooks
 * such as VPN preparation and establishment.
 */
@ScanClass(VpnCommonProxy.class)
public class IVpnManagerProxy extends BinderInvocationStub {
    /** Logging tag for this proxy class. */
    public static final String TAG = "IVpnManagerProxy";
    /** The system service name used to look up the VPN management service. */
    public static final String VPN_MANAGEMENT_SERVICE = "vpn_management";

    /**
     * Constructs a new proxy by acquiring the real VPN manager binder service.
     */
    public IVpnManagerProxy() {
        super(BRServiceManager.get().getService(VPN_MANAGEMENT_SERVICE));
    }

    /**
     * Returns the underlying VPN manager service interface.
     *
     * @return the real {@code IVpnManager} binder interface
     */
    @Override
    protected Object getWho() {
        return BRIVpnManagerStub.get().asInterface(BRServiceManager.get().getService(VPN_MANAGEMENT_SERVICE));
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(VPN_MANAGEMENT_SERVICE);
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
