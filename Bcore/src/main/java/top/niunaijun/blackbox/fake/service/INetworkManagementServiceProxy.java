package top.niunaijun.blackbox.fake.service;

import static top.niunaijun.blackbox.app.BActivityThread.getUid;

import java.lang.reflect.Method;

import black.android.os.BRINetworkManagementServiceStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.fake.service.base.UidMethodProxy;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Proxy for the Android Network Management system service (INetworkManagementService).
 * Intercepts network management operations to translate UIDs and package names
 * for the virtual environment. Ensures that cleartext network policies,
 * metered network blacklists/whitelists, and per-UID network statistics are
 * correctly resolved for apps running inside the virtual container.
 *
 * @author BlackBox
 */
public class INetworkManagementServiceProxy extends BinderInvocationStub {
    /** The system service name for the network management service. */
    public static final String NAME = "network_management";

    /**
     * Constructs a new proxy by obtaining the Network Management Service binder.
     */
    public INetworkManagementServiceProxy() {
        super(BRServiceManager.get().getService(NAME));
    }

    /**
     * Returns the INetworkManagementService interface instance from the system service.
     *
     * @return the original INetworkManagementService binder interface
     */
    @Override
    protected Object getWho() {
        return BRINetworkManagementServiceStub.get().asInterface(BRServiceManager.get().getService(NAME));
    }

    /**
     * Replaces the system Network Management Service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(NAME);
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

    /**
     * Binds UID-aware method hooks for network policy operations.
     * Registers proxies for cleartext network policy, metered network
     * blacklist, and metered network whitelist methods.
     */
    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new UidMethodProxy("setUidCleartextNetworkPolicy", 0));
        addMethodHook(new UidMethodProxy("setUidMeteredNetworkBlacklist", 0));
        addMethodHook(new UidMethodProxy("setUidMeteredNetworkWhitelist", 0));
    }

    /**
     * Hook that intercepts {@code getNetworkStatsUidDetail} to replace
     * both the UID and package name arguments with the virtual environment's equivalents.
     */
    @ProxyMethod("getNetworkStatsUidDetail")
    public static class getNetworkStatsUidDetail extends MethodHook {

        /**
         * Replaces the first UID and first package name arguments, then delegates
         * to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; UID and package name are replaced
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstUid(args);
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }
}
