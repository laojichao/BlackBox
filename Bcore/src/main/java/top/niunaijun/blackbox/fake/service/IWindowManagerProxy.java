package top.niunaijun.blackbox.fake.service;

import android.os.IInterface;

import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.android.view.BRIWindowManagerStub;
import black.android.view.BRWindowManagerGlobal;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;

/**
 * Proxy for the Android {@code window} system service.
 * <p>
 * Intercepts window manager operations, particularly session creation via
 * {@code openSession}, which returns a wrapped {@link IWindowSessionProxy}
 * to inject virtual environment package names into display requests.
 */
public class IWindowManagerProxy extends BinderInvocationStub {
    /** Logging tag for this proxy class. */
    public static final String TAG = "WindowManagerStub";

    /**
     * Constructs a new proxy by acquiring the real window manager binder service.
     */
    public IWindowManagerProxy() {
        super(BRServiceManager.get().getService("window"));
    }

    /**
     * Returns the underlying window manager service interface.
     *
     * @return the real {@code IWindowManager} binder interface
     */
    @Override
    protected Object getWho() {
        return BRIWindowManagerStub.get().asInterface(BRServiceManager.get().getService("window"));
    }

    /**
     * Injects this proxy into the system service registry and clears the cached
     * window manager service reference.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("window");
        BRWindowManagerGlobal.get()._set_sWindowManagerService(null);
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
     * Hook that intercepts {@code openSession} and wraps the returned session
     * in an {@link IWindowSessionProxy} to inject virtual package names.
     */
    @ProxyMethod("openSession")
    public static class OpenSession extends MethodHook {
        /**
         * Creates a proxy wrapper around the real window session.
         *
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return a proxied {@link IWindowSessionProxy} instance
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IInterface session = (IInterface) method.invoke(who, args);
            IWindowSessionProxy IWindowSessionProxy = new IWindowSessionProxy(session);
            IWindowSessionProxy.injectHook();
            return IWindowSessionProxy.getProxyInvocation();
        }
    }
}
