package top.niunaijun.blackbox.fake.service;

import android.content.Context;

import java.lang.reflect.Method;

import black.android.content.pm.BRILauncherAppsStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Proxy for the Android Launcher Apps system service (ILauncherApps).
 * Intercepts launcher-related API calls to replace package names in method
 * arguments, ensuring that launcher operations resolve correctly within the
 * virtual environment. All method invocations are forwarded after package
 * name substitution.
 *
 * @author Milk
 */
public class ILauncherAppsProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by obtaining the Launcher Apps binder service.
     */
    public ILauncherAppsProxy() {
        super(BRServiceManager.get().getService(Context.LAUNCHER_APPS_SERVICE));
    }

    /**
     * Returns the ILauncherApps interface instance from the system service.
     *
     * @return the original ILauncherApps binder interface
     */
    @Override
    protected Object getWho() {
        return BRILauncherAppsStub.get().asInterface(BRServiceManager.get().getService(Context.LAUNCHER_APPS_SERVICE));
    }

    /**
     * Replaces the system Launcher Apps service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.LAUNCHER_APPS_SERVICE);
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
     * Binds method hooks for launcher-related API calls.
     */
    @Override
    protected void onBindMethod() {
        super.onBindMethod();
    }

    /**
     * Intercepts all method invocations to replace the first package name
     * argument with the virtual environment's host package before delegation.
     *
     * @param proxy the proxy object the method was invoked on
     * @param method the method being invoked
     * @param args the method arguments; the first package name is replaced
     * @return the result of the original method invocation
     * @throws Throwable if the underlying method call fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceFirstAppPkg(args);
        // todo shouldHideFromSuggestions
        return super.invoke(proxy, method, args);
    }

}
