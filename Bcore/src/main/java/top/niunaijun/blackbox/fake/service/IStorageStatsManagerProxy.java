package top.niunaijun.blackbox.fake.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import java.lang.reflect.Method;

import black.android.app.usage.BRIStorageStatsManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Proxy for the Android {@code storagestats} system service (API 26+).
 * <p>
 * Intercepts storage statistics queries and replaces the application package name
 * in requests to ensure proper routing within the virtual environment.
 */
@TargetApi(Build.VERSION_CODES.O)
public class IStorageStatsManagerProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by acquiring the real storage stats manager binder service.
     */
    public IStorageStatsManagerProxy() {
        super(BRServiceManager.get().getService(Context.STORAGE_STATS_SERVICE));
    }

    /**
     * Returns the underlying storage stats manager service interface.
     *
     * @return the real {@code IStorageStatsManager} binder interface
     */
    @Override
    protected Object getWho() {
        return BRIStorageStatsManagerStub.get().asInterface(BRServiceManager.get().getService(Context.STORAGE_STATS_SERVICE));
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.STORAGE_STATS_SERVICE);
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
     * Intercepts all method calls, replacing the first application package name
     * argument before delegation.
     *
     * @param proxy  the proxy instance
     * @param method the method being invoked
     * @param args   the method arguments
     * @return the result of the delegated method call
     * @throws Throwable if the underlying method invocation fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceFirstAppPkg(args);
        return super.invoke(proxy, method, args);
    }
}
