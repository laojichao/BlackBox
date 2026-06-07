package top.niunaijun.blackbox.fake.service.context.providers;

import android.os.IInterface;

import java.lang.reflect.Method;

import black.android.content.BRAttributionSource;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.utils.compat.ContextCompat;

/**
 * Content provider wrapper for virtual environment app-specific content providers.
 * <p>
 * Intercepts content provider method calls by replacing the first package name
 * argument with the virtual app's package name, or by fixing the
 * {@code AttributionSourceState} with the virtual app's UID. This ensures
 * content provider operations are properly attributed to the virtual app.
 */
public class ContentProviderStub extends ClassInvocationStub implements BContentProvider {
    /** Logging tag for this stub class. */
    public static final String TAG = "ContentProviderStub";
    private IInterface mBase;
    private String mAppPkg;

    /**
     * Wraps the given content provider proxy with virtual environment interception.
     *
     * @param contentProviderProxy the real content provider proxy interface
     * @param appPkg               the application package name to inject into calls
     * @return the wrapped content provider proxy
     */
    public IInterface wrapper(final IInterface contentProviderProxy, final String appPkg) {
        mBase = contentProviderProxy;
        mAppPkg = appPkg;
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
     * Intercepts content provider method calls, replacing the first argument
     * with the virtual app package name or fixing attribution source state.
     *
     * @param proxy  the proxy instance
     * @param method the method being invoked
     * @param args   the method arguments
     * @return the result of the delegated method call
     * @throws Throwable if the underlying invocation fails (unwrapped from InvocationTargetException)
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("asBinder".equals(method.getName())) {
            return method.invoke(mBase, args);
        }
        if (args != null && args.length > 0) {
            Object arg = args[0];
            if (arg instanceof String) {
                args[0] = mAppPkg;
            } else if (arg.getClass().getName().equals(BRAttributionSource.getRealClass().getName())) {
                ContextCompat.fixAttributionSourceState(arg, BActivityThread.getBUid());
            }
        }
        try {
            return method.invoke(mBase, args);
        } catch (Throwable e) {
            throw e.getCause();
        }
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
}
