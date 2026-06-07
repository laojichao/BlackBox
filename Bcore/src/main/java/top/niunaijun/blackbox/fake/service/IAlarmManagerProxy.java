package top.niunaijun.blackbox.fake.service;

import android.content.Context;

import java.lang.reflect.Method;

import black.android.app.BRIAlarmManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;

/**
 * Proxy for the Android Alarm Manager system service (IAlarmManager).
 * Intercepts alarm scheduling operations to prevent virtual apps from
 * setting real alarms on the host device. The {@code set} method is
 * intercepted and silenced to avoid unintended alarm behavior.
 *
 * @author Milk
 */
public class IAlarmManagerProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by obtaining the Alarm Manager binder service.
     */
    public IAlarmManagerProxy() {
        super(BRServiceManager.get().getService(Context.ALARM_SERVICE));
    }

    /**
     * Returns the IAlarmManager interface instance from the system service.
     *
     * @return the original IAlarmManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRIAlarmManagerStub.get().asInterface(BRServiceManager.get().getService(Context.ALARM_SERVICE));
    }

    /**
     * Replaces the system Alarm Manager service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Hook that intercepts the {@code set} method to silently block alarm
     * scheduling from virtual apps.
     */
    @ProxyMethod("set")
    public static class Set extends MethodHook {
        /**
         * Silently drops the alarm set request.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
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
}
