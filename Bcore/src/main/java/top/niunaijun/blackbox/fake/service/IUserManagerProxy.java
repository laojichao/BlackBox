package top.niunaijun.blackbox.fake.service;

import android.content.Context;

import java.lang.reflect.Method;
import java.util.ArrayList;

import black.android.content.pm.BRUserInfo;
import black.android.os.BRIUserManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;

/**
 * Proxy for the Android {@code user} system service.
 * <p>
 * Intercepts user management operations including application restrictions queries,
 * profile parent lookups, and user enumeration, returning virtual-environment-aware
 * values to isolate the virtual space from the real device user system.
 */
public class IUserManagerProxy extends BinderInvocationStub {
    /**
     * Constructs a new proxy by acquiring the real user manager binder service.
     */
    public IUserManagerProxy() {
        super(BRServiceManager.get().getService(Context.USER_SERVICE));
    }

    /**
     * Returns the underlying user manager service interface.
     *
     * @return the real {@code IUserManager} binder interface
     */
    @Override
    protected Object getWho() {
        return BRIUserManagerStub.get().asInterface(BRServiceManager.get().getService(Context.USER_SERVICE));
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.USER_SERVICE);
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
     * Hook that replaces the package name with the host package when querying
     * application restrictions.
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

    /**
     * Hook that returns a synthetic primary user profile representing the virtual environment.
     */
    @ProxyMethod("getProfileParent")
    public static class GetProfileParent extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return a synthetic {@code UserInfo} object representing the BlackBox primary user
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object blackBox = BRUserInfo.get()._new(BActivityThread.getUserId(), "BlackBox", BRUserInfo.get().FLAG_PRIMARY());
            return blackBox;
        }
    }

    /**
     * Hook that returns an empty user list to hide real device users.
     */
    @ProxyMethod("getUsers")
    public static class getUsers extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return an empty {@link ArrayList}
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return new ArrayList<>();
        }
    }
}
