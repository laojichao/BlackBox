package top.niunaijun.blackbox.fake.service;

import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.com.android.internal.telephony.BRITelephonyRegistryStub;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Proxy for the Android {@code telephony.registry} system service.
 * <p>
 * Intercepts telephony listener registration calls, replacing the application
 * package name in requests to ensure proper routing within the virtual environment.
 */
public class ITelephonyRegistryProxy extends BinderInvocationStub {
    /**
     * Constructs a new proxy by acquiring the real telephony registry binder service.
     */
    public ITelephonyRegistryProxy() {
        super(BRServiceManager.get().getService("telephony.registry"));
    }

    /**
     * Returns the underlying telephony registry service interface.
     *
     * @return the real {@code ITelephonyRegistry} binder interface
     */
    @Override
    protected Object getWho() {
        return BRITelephonyRegistryStub.get().asInterface(BRServiceManager.get().getService("telephony.registry"));
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("telephony.registry");
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
     * Hook that replaces the package name in subscriber-specific listener registrations.
     */
    @ProxyMethod("listenForSubscriber")
    public static class ListenForSubscriber extends MethodHook {

        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return the result of the delegated method call with replaced package name
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that replaces the package name in standard listener registrations.
     */
    @ProxyMethod("listen")
    public static class Listen extends MethodHook {

        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return the result of the delegated method call with replaced package name
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }
}
