package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;

import black.android.content.pm.BRIShortcutServiceStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.fake.service.base.PkgMethodProxy;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.compat.ParceledListSliceCompat;

/**
 * Proxy for the Android {@code shortcut} system service.
 * <p>
 * Intercepts all shortcut management operations including creation, removal,
 * and query methods. Most operations are either intercepted with package name
 * replacement or return safe default/stub values to avoid side effects in
 * the virtual environment.
 */
public class IShortcutManagerProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by acquiring the real shortcut manager binder service.
     */
    public IShortcutManagerProxy() {
        super(BRServiceManager.get().getService(Context.SHORTCUT_SERVICE));
    }

    /**
     * Returns the underlying shortcut manager service interface.
     *
     * @return the real {@code IShortcutService} binder interface
     */
    @Override
    protected Object getWho() {
        return BRIShortcutServiceStub.get().asInterface(BRServiceManager.get().getService(Context.SHORTCUT_SERVICE));
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.SHORTCUT_SERVICE);
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
     * Binds package-aware method hooks for shortcut query and management operations.
     */
    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new PkgMethodProxy("getShortcuts"));//修复whtasApp启动黑屏问题
        addMethodHook(new PkgMethodProxy("disableShortcuts"));
        addMethodHook(new PkgMethodProxy("enableShortcuts"));
        addMethodHook(new PkgMethodProxy("getRemainingCallCount"));
        addMethodHook(new PkgMethodProxy("getRateLimitResetTime"));
        addMethodHook(new PkgMethodProxy("getIconMaxDimensions"));
        addMethodHook(new PkgMethodProxy("getMaxShortcutCountPerActivity"));
        addMethodHook(new PkgMethodProxy("reportShortcutUsed"));
        addMethodHook(new PkgMethodProxy("onApplicationActive"));
        addMethodHook(new PkgMethodProxy("hasShortcutHostPermission"));
        addMethodHook(new PkgMethodProxy("removeAllDynamicShortcuts"));
        addMethodHook(new PkgMethodProxy("removeDynamicShortcuts"));
        addMethodHook(new PkgMethodProxy("removeLongLivedShortcuts"));
        addMethodHook(new PkgMethodProxy("getManifestShortcuts"){
            @Override
            protected Object hook(Object who, Method method, Object[] args) throws Throwable {
                return ParceledListSliceCompat.create(new ArrayList<ShortcutInfo>());
            }
        });
    }

    /**
     * Hook that intercepts {@code requestPinShortcut} and always returns {@code true}.
     */
    @ProxyMethod("requestPinShortcut")
    public static class RequestPinShortcut extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return always {@code true}
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }

    /**
     * Hook that intercepts {@code setDynamicShortcuts} and always returns {@code true}.
     */
    @ProxyMethod("setDynamicShortcuts")
    public static class SetDynamicShortcuts extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return always {@code true}
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }

    /**
     * Hook that intercepts {@code addDynamicShortcuts} and always returns {@code true}.
     */
    @ProxyMethod("addDynamicShortcuts")
    public static class AddDynamicShortcuts extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return always {@code true}
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }

    /**
     * Hook that intercepts {@code createShortcutResultIntent} and returns an empty {@link Intent}.
     */
    @ProxyMethod("createShortcutResultIntent")
    public static class CreateShortcutResultIntent extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return a new empty {@link Intent}
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return new Intent();
        }
    }

    /**
     * Hook that intercepts {@code pushDynamicShortcut} and returns 0 (success).
     */
    @ProxyMethod("pushDynamicShortcut")
    public static class pushDynamicShortcut extends MethodHook {
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
     * Intercepts all method calls, replacing all application package names
     * in arguments before delegation.
     *
     * @param proxy  the proxy instance
     * @param method the method being invoked
     * @param args   the method arguments
     * @return the result of the delegated method call
     * @throws Throwable if the underlying method invocation fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceAllAppPkg(args);
        return super.invoke(proxy, method, args);
    }
}
