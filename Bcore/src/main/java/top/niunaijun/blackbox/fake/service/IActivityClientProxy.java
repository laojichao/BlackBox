package top.niunaijun.blackbox.fake.service;

import android.app.ActivityManager;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.app.BRActivityClient;
import black.android.util.BRSingleton;
import top.niunaijun.blackbox.fake.frameworks.BActivityManager;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.compat.TaskDescriptionCompat;

/**
 * Proxy for the Android ActivityClient (IActivityClientController) interface.
 * Intercepts client-side activity lifecycle callbacks such as finish, resume,
 * and destroy to notify the virtual activity manager. Also fixes task descriptions
 * on Android 12+ to display correct information in the recent apps list.
 *
 * @author BlackBox
 */
public class IActivityClientProxy extends ClassInvocationStub {
    /** Tag for logging. */
    public static final String TAG = "IActivityClientProxy";
    /** The optional pre-existing IActivityClientController instance to wrap. */
    private final Object who;

    /**
     * Constructs a new proxy with an optional existing controller instance.
     *
     * @param who the existing IActivityClientController to wrap, or null to auto-detect
     */
    public IActivityClientProxy(Object who) {
        this.who = who;
    }

    /**
     * Returns the IActivityClientController instance. Uses the provided instance
     * if available, otherwise retrieves the singleton from ActivityClient.
     *
     * @return the IActivityClientController instance
     */
    @Override
    protected Object getWho() {
        if (who != null) {
            return who;
        }
        Object instance = BRActivityClient.get().getInstance();
        Object singleton = BRActivityClient.get(instance).INTERFACE_SINGLETON();
        return BRSingleton.get(singleton).get();
    }

    /**
     * Injects this proxy into the ActivityClient singleton, replacing the
     * existing IActivityClientController instance.
     *
     * @param baseInvocation the original invocation object (unused)
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        Object instance = BRActivityClient.get().getInstance();
        Object singleton = BRActivityClient.get(instance).INTERFACE_SINGLETON();
        BRSingleton.get(singleton)._set_mInstance(proxyInvocation);
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
     * Returns the proxy invocation object for external injection.
     *
     * @return the proxy invocation object
     */
    @Override
    public Object getProxyInvocation() {
        return super.getProxyInvocation();
    }

    /**
     * Sets the proxy-only mode, allowing this proxy to wrap an existing instance
     * without fully replacing the singleton.
     *
     * @param o true to enable proxy-only mode
     */
    @Override
    public void onlyProxy(boolean o) {
        super.onlyProxy(o);
    }

    /**
     * Hook that intercepts {@code finishActivity} to notify the virtual
     * activity manager when an activity is finished.
     */
    @ProxyMethod("finishActivity")
    public static class FinishActivity extends MethodHook {
        /**
         * Notifies the virtual activity manager that an activity is being finished.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the activity IBinder token
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IBinder token = (IBinder) args[0];
            BActivityManager.get().onFinishActivity(token);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code activityResumed} to notify the virtual
     * activity manager when an activity is resumed.
     */
    @ProxyMethod("activityResumed")
    public static class ActivityResumed extends MethodHook {
        /**
         * Notifies the virtual activity manager that an activity has been resumed.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the activity IBinder token
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IBinder token = (IBinder) args[0];
            BActivityManager.get().onActivityResumed(token);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code activityDestroyed} to notify the virtual
     * activity manager when an activity is destroyed.
     */
    @ProxyMethod("activityDestroyed")
    public static class ActivityDestroyed extends MethodHook {
        /**
         * Notifies the virtual activity manager that an activity has been destroyed.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the activity IBinder token
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IBinder token = (IBinder) args[0];
            BActivityManager.get().onActivityDestroyed(token);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code setTaskDescription} on Android 12+ to fix
     * task description display in the recent apps list.
     */
    // for >= Android 12
    @ProxyMethod("setTaskDescription")
    public static class SetTaskDescription extends MethodHook {
        /**
         * Fixes the TaskDescription to show correct app info in the recent apps list.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the IBinder token, args[1] is the ActivityManager.TaskDescription
         * @return the result of the original method invocation with the fixed TaskDescription
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ActivityManager.TaskDescription td = (ActivityManager.TaskDescription) args[1];
            args[1] = TaskDescriptionCompat.fix(td);
            return method.invoke(who, args);
        }
    }
}
