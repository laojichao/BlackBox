package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.os.BRIVibratorManagerServiceStub;
import black.android.os.BRServiceManager;
import black.com.android.internal.os.BRIVibratorServiceStub;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Proxy for the Android vibrator system service.
 * <p>
 * Intercepts vibrator service calls, replacing the UID and package name
 * arguments to ensure proper attribution within the virtual environment.
 * Handles API differences between Android S+ (vibrator_manager) and
 * older versions (vibrator).
 */
public class IVibratorServiceProxy extends BinderInvocationStub {
    private static String NAME;
    static {
        if (BuildCompat.isS()) {
            NAME = "vibrator_manager";
        } else {
            NAME = Context.VIBRATOR_SERVICE;
        }
    }

    /**
     * Constructs a new proxy by acquiring the real vibrator binder service.
     */
    public IVibratorServiceProxy() {
        super(BRServiceManager.get().getService(NAME));
    }

    /**
     * Returns the underlying vibrator service interface, selecting the
     * appropriate implementation based on the Android API level.
     *
     * @return the real vibrator service binder interface
     */
    @Override
    protected Object getWho() {
        IBinder service = BRServiceManager.get().getService(NAME);
        if (BuildCompat.isS()) {
            return BRIVibratorManagerServiceStub.get().asInterface(service);
        }
        return BRIVibratorServiceStub.get().asInterface(service);
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(NAME);
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
     * Intercepts all method calls, replacing both UID and package name
     * arguments before delegation.
     *
     * @param proxy  the proxy instance
     * @param method the method being invoked
     * @param args   the method arguments
     * @return the result of the delegated method call
     * @throws Throwable if the underlying method invocation fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceFirstUid(args);
        MethodParameterUtils.replaceFirstAppPkg(args);
        return super.invoke(proxy, method, args);
    }
}
