package top.niunaijun.blackbox.fake.service;

import android.content.Context;

import java.lang.reflect.Method;

import black.android.media.BRIMediaRouterServiceStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Proxy for the Android Media Router system service (IMediaRouterService).
 * Intercepts media router client registration and Router2 registration calls
 * to replace package names in method arguments. This ensures that media
 * routing operations resolve correctly within the virtual environment.
 *
 * @author BlackBox
 */
public class IMediaRouterServiceProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by obtaining the Media Router Service binder.
     */
    public IMediaRouterServiceProxy() {
        super(BRServiceManager.get().getService(Context.MEDIA_ROUTER_SERVICE));
    }

    /**
     * Returns the IMediaRouterService interface instance from the system service.
     *
     * @return the original IMediaRouterService binder interface
     */
    @Override
    protected Object getWho() {
        return BRIMediaRouterServiceStub.get().asInterface(BRServiceManager.get().getService(Context.MEDIA_ROUTER_SERVICE));
    }

    /**
     * Replaces the system Media Router Service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.MEDIA_ROUTER_SERVICE);
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
     * Hook that intercepts {@code registerClientAsUser} to replace the
     * package name with the virtual environment's equivalent.
     */
    @ProxyMethod("registerClientAsUser")
    public static class registerClientAsUser extends MethodHook {

        /**
         * Replaces the first app package name argument and delegates to the original method.
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
     * Hook that intercepts {@code registerRouter2} to replace the
     * package name with the virtual environment's equivalent.
     */
    @ProxyMethod("registerRouter2")
    public static class registerRouter2 extends MethodHook {

        /**
         * Replaces the first app package name argument and delegates to the original method.
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
}
