package top.niunaijun.blackbox.fake.service;

import android.os.IInterface;

import java.lang.reflect.Method;

import black.android.hardware.display.BRDisplayManagerGlobal;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.fake.service.base.PkgMethodProxy;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Proxy for the Android Display Manager service (IDisplayManager).
 * Intercepts display management operations and replaces package names in
 * virtual display creation requests. Injects into the DisplayManagerGlobal
 * singleton rather than a system service binder.
 *
 * @author Milk
 */
public class IDisplayManagerProxy extends ClassInvocationStub {

    /**
     * Constructs a new proxy for the Display Manager service.
     */
    public IDisplayManagerProxy() {
    }

    /**
     * Returns the IDisplayManager interface instance from DisplayManagerGlobal.
     *
     * @return the original IDisplayManager interface
     */
    @Override
    protected Object getWho() {
        return BRDisplayManagerGlobal.get(BRDisplayManagerGlobal.get().getInstance()).mDm();
    }

    /**
     * Injects this proxy into the DisplayManagerGlobal singleton's mDm field.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        Object dmg = BRDisplayManagerGlobal.get().getInstance();
        BRDisplayManagerGlobal.get(dmg)._set_mDm(getProxyInvocation());
    }

    /**
     * Checks if the environment has been corrupted by comparing the current
     * mDm reference with this proxy's invocation.
     *
     * @return true if the mDm reference no longer matches this proxy
     */
    @Override
    public boolean isBadEnv() {
        Object dmg = BRDisplayManagerGlobal.get().getInstance();
        IInterface mDm = BRDisplayManagerGlobal.get(dmg).mDm();
        return mDm != getProxyInvocation();
    }


    /**
     * Hook that intercepts {@code createVirtualDisplay} to replace the
     * package name argument with the virtual environment's host package.
     */
    @ProxyMethod("createVirtualDisplay")
    public static class CreateVirtualDisplay extends MethodHook {

        /**
         * Returns the method name for this hook.
         *
         * @return "createVirtualDisplay"
         */
        @Override
        protected String getMethodName() {
            return "createVirtualDisplay";
        }

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
