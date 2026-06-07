package top.niunaijun.blackbox.fake.service.context.providers;

import android.os.IInterface;

import java.lang.reflect.Method;

import black.android.content.BRAttributionSource;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.utils.compat.ContextCompat;

/**
 * Content provider wrapper for system-level content providers in the virtual environment.
 * <p>
 * Intercepts content provider method calls by replacing the first package name
 * argument with the host package name, or by fixing the {@code AttributionSourceState}
 * with the host UID. Unlike {@link ContentProviderStub}, this stub uses the host
 * identity rather than the virtual app identity for system-level providers.
 */
public class SystemProviderStub extends ClassInvocationStub implements BContentProvider {
    private IInterface mBase;

    /**
     * Wraps the given content provider proxy with host environment interception.
     *
     * @param contentProviderProxy the real content provider proxy interface
     * @param appPkg               the application package name (unused, host package is used instead)
     * @return the wrapped content provider proxy
     */
    @Override
    public IInterface wrapper(IInterface contentProviderProxy, String appPkg) {
        mBase = contentProviderProxy;
        injectHook();
        return (IInterface) getProxyInvocation();
    }

    /**
     * Returns the underlying content provider interface.
     *
     * @return the real content provider interface
     */
    @Override
    protected Object getWho() {
        return mBase;
    }

    /**
     * No-op injection; the stub does not replace a system service.
     *
     * @param baseInvocation  the original service object
     * @param proxyInvocation the proxy object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {

    }

    /**
     * No-op method binding; all interception is handled via {@link #invoke}.
     */
    @Override
    protected void onBindMethod() {

    }

    /**
     * Checks whether the current environment is invalid for this stub.
     *
     * @return always {@code false}, indicating the environment is always valid
     */
    @Override
    public boolean isBadEnv() {
        return false;
    }

    /**
     * Intercepts content provider method calls, replacing the first argument
     * with the host package name or fixing attribution source state with host UID.
     *
     * @param proxy  the proxy instance
     * @param method the method being invoked
     * @param args   the method arguments
     * @return the result of the delegated method call
     * @throws Throwable if the underlying invocation fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("asBinder".equals(method.getName())) {
            return method.invoke(mBase, args);
        }
        if (args != null && args.length > 0) {
            Object arg = args[0];
            if (arg instanceof String) {
                args[0] = BlackBoxCore.getHostPkg();
            } else if (arg.getClass().getName().equals(BRAttributionSource.getRealClass().getName())) {
                ContextCompat.fixAttributionSourceState(arg, BlackBoxCore.getHostUid());
            }
        }
        return method.invoke(mBase, args);
    }
}
