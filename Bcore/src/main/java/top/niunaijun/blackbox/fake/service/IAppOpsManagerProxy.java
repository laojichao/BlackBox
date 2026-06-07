package top.niunaijun.blackbox.fake.service;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.app.BRAppOpsManager;
import black.android.os.BRServiceManager;
import black.com.android.internal.app.BRIAppOpsServiceStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Proxy for the Android App Operations Manager system service (IAppOpsService).
 * Intercepts app operation permission checks and replaces package names and UIDs
 * to ensure proper operation within the virtual environment. Allows certain
 * proxy operations and package checks to always pass to prevent apps from
 * detecting the virtual sandbox.
 *
 * @author Milk
 */
public class IAppOpsManagerProxy extends BinderInvocationStub {
    /**
     * Constructs a new proxy by obtaining the App Ops Manager binder service.
     */
    public IAppOpsManagerProxy() {
        super(BRServiceManager.get().getService(Context.APP_OPS_SERVICE));
    }

    /**
     * Returns the IAppOpsService interface instance from the system service.
     *
     * @return the original IAppOpsService binder interface
     */
    @Override
    protected Object getWho() {
        IBinder call = BRServiceManager.get().getService(Context.APP_OPS_SERVICE);
        return BRIAppOpsServiceStub.get().asInterface(call);
    }

    /**
     * Replaces the system App Ops Manager service with this proxy instance.
     * Also replaces the mService field in the AppOpsManager if available.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        if (BRAppOpsManager.get(null)._check_mService() != null) {
            AppOpsManager appOpsManager = (AppOpsManager) BlackBoxCore.getContext().getSystemService(Context.APP_OPS_SERVICE);
            try {
                BRAppOpsManager.get(appOpsManager)._set_mService(getProxyInvocation());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        replaceSystemService(Context.APP_OPS_SERVICE);
    }

    /**
     * Intercepts all method calls to replace the first app package name and
     * last UID arguments with virtual environment equivalents.
     *
     * @param proxy the proxy object
     * @param method the method being invoked
     * @param args the method arguments
     * @return the result of the delegated method call
     * @throws Throwable if the underlying method call fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceFirstAppPkg(args);
        MethodParameterUtils.replaceLastUid(args);
        return super.invoke(proxy, method, args);
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
     * Hook that intercepts {@code noteProxyOperation} to always allow
     * proxy operations within the virtual environment.
     */
    @ProxyMethod("noteProxyOperation")
    public static class NoteProxyOperation extends MethodHook {
        /**
         * Returns MODE_ALLOWED for all proxy operations.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns {@link AppOpsManager#MODE_ALLOWED}
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return AppOpsManager.MODE_ALLOWED;
        }
    }

    /**
     * Hook that intercepts {@code checkPackage} to always allow
     * package access within the virtual environment.
     */
    @ProxyMethod("checkPackage")
    public static class CheckPackage extends MethodHook {
        /**
         * Returns MODE_ALLOWED for all package checks.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns {@link AppOpsManager#MODE_ALLOWED}
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            // todo
            return AppOpsManager.MODE_ALLOWED;
        }
    }

    /**
     * Hook that intercepts {@code checkOperation} to replace the UID
     * with the virtual environment's UID before checking.
     */
    @ProxyMethod("checkOperation")
    public static class CheckOperation extends MethodHook {
        /**
         * Replaces the last UID argument and delegates to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; last UID is replaced
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUid(args);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code noteOperation} to pass through
     * to the original method directly.
     */
    @ProxyMethod("noteOperation")
    public static class NoteOperation extends MethodHook {
        /**
         * Delegates directly to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }
}
