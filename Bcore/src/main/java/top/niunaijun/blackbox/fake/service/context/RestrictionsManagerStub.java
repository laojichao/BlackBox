package top.niunaijun.blackbox.fake.service.context;

import android.content.Context;

import java.lang.reflect.Method;

import black.android.content.BRIRestrictionsManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;

/**
 * Proxy for the Android {@code restrictions} system service.
 * <p>
 * Intercepts application restrictions queries, replacing the package name
 * with the host package to ensure restrictions are retrieved correctly
 * within the virtual environment.
 */
public class RestrictionsManagerStub extends BinderInvocationStub {

    /**
     * Constructs a new proxy by acquiring the real restrictions manager binder service.
     */
    public RestrictionsManagerStub() {
        super(BRServiceManager.get().getService(Context.RESTRICTIONS_SERVICE));
    }

    /**
     * Returns the underlying restrictions manager service interface.
     *
     * @return the real {@code IRestrictionsManager} binder interface
     */
    @Override
    protected Object getWho() {
        return BRIRestrictionsManagerStub.get().asInterface(BRServiceManager.get().getService(Context.RESTRICTIONS_SERVICE));
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.RESTRICTIONS_SERVICE);
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
     * Hook that intercepts {@code getApplicationRestrictions} and replaces
     * the package name argument with the host package before delegation.
     */
    @ProxyMethod("getApplicationRestrictions")
    public static class GetApplicationRestrictions extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments (first element is package name)
         * @return the application restrictions bundle for the host package
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            args[0] = BlackBoxCore.getHostPkg();
            return method.invoke(who, args);
        }
    }
}
