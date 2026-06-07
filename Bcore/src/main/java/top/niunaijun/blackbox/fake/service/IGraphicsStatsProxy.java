package top.niunaijun.blackbox.fake.service;

import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.android.view.BRIGraphicsStatsStub;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Proxy for the Android Graphics Stats system service (IGraphicsStats).
 * Intercepts graphics statistics requests to replace package names in method
 * arguments, ensuring graphics profiling works correctly within the virtual
 * environment. Delegates {@code requestBufferForProcess} calls to the real
 * system service after parameter substitution.
 *
 * @author Milk
 */
public class IGraphicsStatsProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by obtaining the Graphics Stats binder service.
     */
    public IGraphicsStatsProxy() {
        super(BRServiceManager.get().getService("graphicsstats"));
    }

    /**
     * Returns the IGraphicsStats interface instance from the system service.
     *
     * @return the original IGraphicsStats binder interface
     */
    @Override
    protected Object getWho() {
        return BRIGraphicsStatsStub.get().asInterface(BRServiceManager.get().getService("graphicsstats"));
    }

    /**
     * Replaces the system Graphics Stats service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("graphicsstats");
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
     * Hook that intercepts {@code requestBufferForProcess} to replace
     * the package name with the virtual environment's equivalent.
     */
    @ProxyMethod("requestBufferForProcess")
    public static class RequestBufferForProcess extends MethodHook {

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
