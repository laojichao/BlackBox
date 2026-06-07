package top.niunaijun.blackbox.fake.service;

import android.content.Context;

import java.lang.reflect.Method;

import black.android.media.session.BRISessionManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;

/**
 * Proxy for the Android Media Session Manager system service (IMediaSessionManager).
 * Intercepts media session creation to replace the caller package name with the
 * host package name, ensuring media sessions are correctly attributed within the
 * virtual environment.
 *
 * @author Milk
 */
public class IMediaSessionManagerProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by obtaining the Media Session Manager binder service.
     */
    public IMediaSessionManagerProxy() {
        super(BRServiceManager.get().getService(Context.MEDIA_SESSION_SERVICE));
    }

    /**
     * Returns the ISessionManager interface instance from the system service.
     *
     * @return the original ISessionManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRISessionManagerStub.get().asInterface(BRServiceManager.get().getService(Context.MEDIA_SESSION_SERVICE));
    }

    /**
     * Replaces the system Media Session Manager service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.MEDIA_SESSION_SERVICE);
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
     * Hook that intercepts {@code createSession} to replace the caller
     * package name with the host package name.
     */
    @ProxyMethod("createSession")
    public static class CreateSession extends MethodHook {

        /**
         * Replaces the first string argument (package name) with the host package name
         * and delegates to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is replaced if it is a String
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null && args.length > 0 && args[0] instanceof String) {
                args[0] = BlackBoxCore.getHostPkg();
            }
            return method.invoke(who, args);
        }
    }
}
