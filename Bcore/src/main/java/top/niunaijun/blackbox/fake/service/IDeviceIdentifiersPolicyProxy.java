package top.niunaijun.blackbox.fake.service;


import java.lang.reflect.Method;

import black.android.os.BRIDeviceIdentifiersPolicyServiceStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.Md5Utils;

/**
 * Proxy for the Android Device Identifiers Policy system service (IDeviceIdentifiersPolicyService).
 * Intercepts device identifier queries (such as serial number) to return
 * virtualized values derived from the host package name. This prevents
 * virtual apps from obtaining the real device serial number.
 *
 * @author Milk
 */
public class IDeviceIdentifiersPolicyProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by obtaining the device_identifiers binder service.
     */
    public IDeviceIdentifiersPolicyProxy() {
        super(BRServiceManager.get().getService("device_identifiers"));
    }

    /**
     * Returns the IDeviceIdentifiersPolicyService interface instance from the system service.
     *
     * @return the original IDeviceIdentifiersPolicyService binder interface
     */
    @Override
    protected Object getWho() {
        return BRIDeviceIdentifiersPolicyServiceStub.get().asInterface(BRServiceManager.get().getService("device_identifiers"));
    }

    /**
     * Replaces the system Device Identifiers Policy service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("device_identifiers");
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
     * Hook that intercepts {@code getSerialForPackage} to return an MD5 hash
     * of the host package name instead of the real device serial number.
     */
    @ProxyMethod("getSerialForPackage")
    public static class x extends MethodHook {
        /**
         * Returns an MD5 hash of the host package name as a fake serial number.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return an MD5 hash string derived from the host package name
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//                args[0] = BlackBoxCore.getHostPkg();
//                return method.invoke(who, args);
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }
}
