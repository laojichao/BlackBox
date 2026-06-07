package top.niunaijun.blackbox.fake.service.context;

import java.lang.reflect.Method;

import black.android.content.BRIContentServiceStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;

/**
 * Proxy for the Android {@code content} system service.
 * <p>
 * Intercepts content observer registration and change notification calls,
 * returning 0 (success) without actually registering observers or propagating
 * notifications to avoid side effects in the virtual environment.
 */
public class ContentServiceStub extends BinderInvocationStub {

    /**
     * Constructs a new proxy by acquiring the real content service binder.
     */
    public ContentServiceStub() {
        super(BRServiceManager.get().getService("content"));
    }

    /**
     * Returns the underlying content service interface.
     *
     * @return the real {@code IContentService} binder interface
     */
    @Override
    protected Object getWho() {
        return BRIContentServiceStub.get().asInterface(BRServiceManager.get().getService("content"));
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("content");
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
     * Hook that intercepts {@code registerContentObserver} and returns 0 (success)
     * without actually registering the observer.
     */
    @ProxyMethod("registerContentObserver")
    public static class RegisterContentObserver extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return always 0
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code notifyChange} and returns 0 (success)
     * without actually propagating the notification.
     */
    @ProxyMethod("notifyChange")
    public static class NotifyChange extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return always 0
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }
}
