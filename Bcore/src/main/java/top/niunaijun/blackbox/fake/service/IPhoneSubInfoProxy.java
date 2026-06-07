package top.niunaijun.blackbox.fake.service;

import java.lang.reflect.Method;

import black.android.telephony.BRTelephonyManager;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Proxy for the Android {@code IPhoneSubInfo} telephony service.
 * <p>
 * Intercepts phone subscription information queries such as phone number retrieval,
 * replacing the application package name in requests and returning {@code null} for
 * sensitive subscriber data like line numbers.
 */
public class IPhoneSubInfoProxy extends ClassInvocationStub {
    /** Logging tag for this proxy class. */
    public static final String TAG = "IPhoneSubInfoProxy";

    /**
     * Constructs a new proxy, initializing the telephony service handle cache
     * and subscriber info service if available on the current API level.
     */
    public IPhoneSubInfoProxy() {
        if (BRTelephonyManager.get()._check_sServiceHandleCacheEnabled() != null) {
            BRTelephonyManager.get()._set_sServiceHandleCacheEnabled(true);
        }
        if (BRTelephonyManager.get()._check_getSubscriberInfoService() != null) {
            BRTelephonyManager.get().getSubscriberInfoService();
        }
    }

    /**
     * Returns the underlying {@code IPhoneSubInfo} service interface.
     *
     * @return the real phone sub info binder interface
     */
    @Override
    protected Object getWho() {
        return BRTelephonyManager.get().sIPhoneSubInfo();
    }

    /**
     * Injects this proxy by replacing the static phone sub info reference.
     *
     * @param baseInvocation  the original service object
     * @param proxyInvocation the proxy object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        BRTelephonyManager.get()._set_sIPhoneSubInfo(proxyInvocation);
    }

    /**
     * Intercepts all method calls, replacing the first package name argument
     * with the virtual application package before delegation.
     *
     * @param proxy  the proxy instance
     * @param method the method being invoked
     * @param args   the method arguments
     * @return the result of the delegated method call
     * @throws Throwable if the underlying method invocation fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceFirstAppPkg(args);
        return super.invoke(proxy, method, args);
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
     * Hook that intercepts {@code getLine1NumberForSubscriber} and returns {@code null}
     * to prevent leaking the real phone number in the virtual environment.
     */
    @ProxyMethod("getLine1NumberForSubscriber")
    public static class getLine1NumberForSubscriber extends MethodHook {
        /**
         * Hooks the method to return {@code null} instead of the real phone number.
         *
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return always {@code null}
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return null;
        }
    }
}
