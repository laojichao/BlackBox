package top.niunaijun.blackbox.fake.service;

import android.os.IInterface;
import android.view.WindowManager;

import java.lang.reflect.Method;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;


/**
 * Proxy for the Android window session interface.
 * <p>
 * Intercepts window display operations ({@code addToDisplay}, {@code addToDisplayAsUser})
 * to replace the package name in {@link WindowManager.LayoutParams} with the host package,
 * ensuring virtual environment windows are properly attributed.
 */
public class IWindowSessionProxy extends BinderInvocationStub {
    /** Logging tag for this proxy class. */
    public static final String TAG = "WindowSessionStub";

    private IInterface mSession;

    /**
     * Constructs a new proxy wrapping the given window session.
     *
     * @param session the real window session interface to wrap
     */
    public IWindowSessionProxy(IInterface session) {
        super(session.asBinder());
        mSession = session;
    }

    /**
     * Returns the underlying window session interface.
     *
     * @return the real window session interface
     */
    @Override
    protected Object getWho() {
        return mSession;
    }

    /**
     * No-op injection; the session proxy does not replace a system service.
     *
     * @param baseInvocation  the original service object
     * @param proxyInvocation the proxy object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {

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
     * Returns the proxy invocation object for this session.
     *
     * @return the proxy invocation wrapper
     */
    @Override
    public Object getProxyInvocation() {
        return super.getProxyInvocation();
    }

    /**
     * Hook that intercepts {@code addToDisplay} and replaces the package name
     * in {@link WindowManager.LayoutParams} with the host package.
     */
    @ProxyMethod("addToDisplay")
    public static class AddToDisplay extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments containing LayoutParams
         * @return the result of the delegated method call
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            for (Object arg : args) {
                if (arg == null) {
                    continue;
                }
                if (arg instanceof WindowManager.LayoutParams) {
                    ((WindowManager.LayoutParams) arg).packageName = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that inherits from {@link AddToDisplay} for user-specific display operations.
     * Uses the same package name replacement logic.
     */
    @ProxyMethod("addToDisplayAsUser")
    public static class AddToDisplayAsUser extends AddToDisplay {
    }
}
