package top.niunaijun.blackbox.fake.service;

import android.content.ComponentName;
import android.content.Context;

import java.lang.reflect.Method;

import black.android.app.admin.BRIDevicePolicyManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Proxy for the Android Device Policy Manager system service (IDevicePolicyManager).
 * Intercepts device policy queries to return safe default values within the
 * virtual environment. Reports no device owner, always indicates the device
 * is provisioned, and replaces package names in encryption status queries.
 *
 * @author Milk
 */
public class IDevicePolicyManagerProxy extends BinderInvocationStub {
    /**
     * Constructs a new proxy by obtaining the Device Policy Manager binder service.
     */
    public IDevicePolicyManagerProxy() {
        super(BRServiceManager.get().getService(Context.DEVICE_POLICY_SERVICE));
    }

    /**
     * Returns the IDevicePolicyManager interface instance from the system service.
     *
     * @return the original IDevicePolicyManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRIDevicePolicyManagerStub.get().asInterface(BRServiceManager.get().getService(Context.DEVICE_POLICY_SERVICE));
    }

    /**
     * Replaces the system Device Policy Manager service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.DEVICE_POLICY_SERVICE);
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
     * Hook that intercepts {@code getStorageEncryptionStatus} to replace
     * the package name with the virtual environment's equivalent.
     */
    @ProxyMethod("getStorageEncryptionStatus")
    public static class GetStorageEncryptionStatus extends MethodHook {

        /**
         * Replaces the first package name argument and delegates to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; the first package name is replaced
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code getDeviceOwnerComponent} to return an
     * empty ComponentName, indicating no device owner is configured.
     */
    @ProxyMethod("getDeviceOwnerComponent")
    public static class GetDeviceOwnerComponent extends MethodHook {

        /**
         * Returns an empty ComponentName indicating no device owner.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return an empty ComponentName
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return new ComponentName("", "");
        }
    }

    /**
     * Hook that intercepts {@code getDeviceOwnerName} to return "BlackBox"
     * as the device owner name.
     */
    @ProxyMethod("getDeviceOwnerName")
    public static class getDeviceOwnerName extends MethodHook {

        /**
         * Returns "BlackBox" as the device owner name.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns "BlackBox"
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return "BlackBox";
        }
    }

    /**
     * Hook that intercepts {@code getProfileOwnerName} to return "BlackBox"
     * as the profile owner name.
     */
    @ProxyMethod("getProfileOwnerName")
    public static class getProfileOwnerName extends MethodHook {

        /**
         * Returns "BlackBox" as the profile owner name.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns "BlackBox"
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return "BlackBox";
        }
    }

    /**
     * Hook that intercepts {@code isDeviceProvisioned} to always indicate
     * that the device is provisioned.
     */
    @ProxyMethod("isDeviceProvisioned")
    public static class isDeviceProvisioned extends MethodHook {
        /**
         * Always returns true indicating the device is provisioned.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns true
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }
}
