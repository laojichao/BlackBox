package top.niunaijun.blackbox.fake.service;

import android.content.Context;

import black.android.os.BRIPowerManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.service.base.ValueMethodProxy;

/**
 * Proxy for the Android {@code power} system service.
 * <p>
 * Intercepts power management operations such as wake lock acquisition and release,
 * returning safe default values to prevent virtual environment apps from affecting
 * real device power state.
 */
public class IPowerManagerProxy extends BinderInvocationStub {
    /**
     * Constructs a new proxy by acquiring the real power manager binder service.
     */
    public IPowerManagerProxy() {
        super(BRServiceManager.get().getService(Context.POWER_SERVICE));
    }

    /**
     * Returns the underlying power manager service interface.
     *
     * @return the real {@code IPowerManager} binder interface
     */
    @Override
    protected Object getWho() {
        return BRIPowerManagerStub.get().asInterface(BRServiceManager.get().getService(Context.POWER_SERVICE));
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.POWER_SERVICE);
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
     * Binds method hooks that return safe default values for wake lock operations.
     */
    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("acquireWakeLock", 0));
        addMethodHook(new ValueMethodProxy("acquireWakeLockWithUid", 0));
        addMethodHook(new ValueMethodProxy("releaseWakeLock", 0));
        addMethodHook(new ValueMethodProxy("updateWakeLockWorkSource", 0));
        addMethodHook(new ValueMethodProxy("isWakeLockLevelSupported", true));
    }
}
