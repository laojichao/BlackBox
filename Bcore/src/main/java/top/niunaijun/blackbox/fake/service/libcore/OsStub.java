package top.niunaijun.blackbox.fake.service.libcore;

import android.os.Process;

import java.lang.reflect.Method;

import black.libcore.io.BRLibcore;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.IOCore;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.Reflector;

/**
 * Proxy for the {@code libcore.io.Os} low-level I/O class.
 * <p>
 * Intercepts all OS-level I/O operations to redirect file paths through the
 * virtual environment's I/O core, and provides fake UID values for the
 * virtual application. This stub handles path redirection transparently
 * for any method argument that starts with "/".
 */
public class OsStub extends ClassInvocationStub {
    /** Logging tag for this stub class. */
    public static final String TAG = "OsStub";
    private Object mBase;

    /**
     * Constructs a new proxy by obtaining the current {@code libcore.io.Os} instance.
     */
    public OsStub() {
        mBase = BRLibcore.get().os();
    }

    /**
     * Returns the underlying {@code libcore.io.Os} instance.
     *
     * @return the real Os instance
     */
    @Override
    protected Object getWho() {
        return mBase;
    }

    /**
     * Injects this proxy by replacing the static {@code libcore.io.Os} reference.
     *
     * @param baseInvocation  the original Os instance
     * @param proxyInvocation the proxy Os instance to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        BRLibcore.get()._set_os(proxyInvocation);
    }

    /**
     * No-op method binding; path redirection is handled via {@link #invoke}.
     */
    @Override
    protected void onBindMethod() {
    }

    /**
     * Checks whether the current environment is invalid by verifying
     * the Os reference still points to this proxy.
     *
     * @return {@code true} if the Os reference has been replaced externally
     */
    @Override
    public boolean isBadEnv() {
        return BRLibcore.get().os() != getProxyInvocation();
    }

    /**
     * Intercepts all Os method calls, redirecting any string argument that
     * starts with "/" through the virtual environment's I/O core path redirection.
     *
     * @param proxy  the proxy instance
     * @param method the method being invoked
     * @param args   the method arguments (path strings will be redirected)
     * @return the result of the delegated method call
     * @throws Throwable if the underlying invocation fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null)
                    continue;
                if (args[i] instanceof String && ((String) args[i]).startsWith("/")) {
                    String orig = (String) args[i];
                    args[i] = IOCore.get().redirectPath(orig);
//                    if (!ObjectsCompat.equals(orig, args[i])) {
//                        Log.d(TAG, "redirectPath: " + orig + "  => " + args[i]);
//                    }
                }
            }
        }
        return super.invoke(proxy, method, args);
    }

    /**
     * Hook that intercepts {@code getuid} and returns a fake UID for the virtual app.
     */
    @ProxyMethod("getuid")
    public static class getuid extends MethodHook {

        /**
         * Returns the virtual app's UID if the caller is a virtual application,
         * otherwise returns the real UID.
         *
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return the fake or real UID
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int callUid = (int) method.invoke(who, args);
            return getFakeUid(callUid);
        }
    }

    /**
     * Hook that intercepts {@code stat} and replaces the {@code st_uid} field
     * in the result with a fake UID for the virtual application.
     */
    @ProxyMethod("stat")
    public static class stat extends MethodHook {

        /**
         * Invokes the real stat method and replaces the UID in the result struct.
         *
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return the stat result with a replaced UID
         * @throws Throwable (unwrapped from InvocationTargetException) if the underlying invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object invoke = null;
            try {
                invoke = method.invoke(who, args);
            } catch (Throwable e) {
                throw e.getCause();
            }
            Reflector.with(invoke).field("st_uid").set(getFakeUid(-1));
            return invoke;
        }
    }

    /**
     * Computes the fake UID for the virtual environment.
     * <p>
     * System UIDs (below {@code FIRST_APPLICATION_UID}) are returned as-is.
     * Application UIDs are replaced with the virtual app's BAppId if the
     * thread is initialized, or the host UID otherwise.
     *
     * @param callUid the real UID returned by the Os call
     * @return the appropriate fake UID
     */
    private static int getFakeUid(int callUid) {
        if (callUid > 0 && callUid <= Process.FIRST_APPLICATION_UID)
            return callUid;
//            Log.d(TAG, "getuid: " + BActivityThread.getAppPackageName() + ", " + BActivityThread.getAppUid());
        if (BActivityThread.isThreadInit() && BActivityThread.currentActivityThread().isInit()) {
            return BActivityThread.getBAppId();
        } else {
            return BlackBoxCore.getHostUid();
        }
    }
}
