package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.android.view.accessibility.BRIAccessibilityManagerStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.system.user.BUserHandle;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethods;

/**
 * Proxy for the Android Accessibility Manager system service (IAccessibilityManager).
 * Intercepts accessibility service calls and replaces user IDs with the virtual
 * environment's user ID to ensure proper isolation between virtual app instances.
 * Contains a static inner hook class that handles user ID replacement for various
 * accessibility operations such as sending events and querying installed services.
 *
 * @author Milk
 */
public class IAccessibilityManagerProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by obtaining the Accessibility Manager binder service.
     */
    public IAccessibilityManagerProxy() {
        super(BRServiceManager.get().getService(Context.ACCESSIBILITY_SERVICE));
    }

    /**
     * Returns the IAccessibilityManager interface instance from the system service.
     *
     * @return the original IAccessibilityManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRIAccessibilityManagerStub.get().asInterface(BRServiceManager.get().getService(Context.ACCESSIBILITY_SERVICE));
    }

    /**
     * Replaces the system Accessibility Manager service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    /**
     * Checks if the environment has been corrupted by another proxy.
     *
     * @return always returns false, indicating the environment is always valid
     */
    @Override
    public boolean isBadEnv() {
        return false;
    }

    /**
     * Hook that replaces the last integer argument (user ID) in accessibility manager
     * method calls with the virtual environment's user ID. This ensures that
     * accessibility operations are scoped to the correct virtual user.
     * Covers methods: interrupt, sendAccessibilityEvent, addClient,
     * getInstalledAccessibilityServiceList, getEnabledAccessibilityServiceList,
     * addAccessibilityInteractionConnection, getWindowToken.
     */
    @ProxyMethods({"interrupt", "sendAccessibilityEvent", "addClient",
            "getInstalledAccessibilityServiceList", "getEnabledAccessibilityServiceList",
            "addAccessibilityInteractionConnection", "getWindowToken"})
    public static class ReplaceUserId extends MethodHook {
        /**
         * Replaces the last argument with the virtual user's ID if it is an Integer.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; the last integer argument is replaced with the virtual user ID
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int index = args.length - 1;
                Object arg = args[index];
                if (arg instanceof Integer) {
                    ApplicationInfo applicationInfo = BlackBoxCore.getContext().getApplicationInfo();
                    args[index] = BUserHandle.getUserId(applicationInfo.uid);
                }
            }
            return method.invoke(who, args);
        }
    }
}
