package top.niunaijun.blackbox.fake.service;

import android.app.ActivityManager;

import java.lang.reflect.Method;

import black.android.app.BRActivityTaskManager;
import black.android.app.BRIActivityTaskManagerStub;
import black.android.os.BRServiceManager;
import black.android.util.BRSingleton;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.fake.hook.ScanClass;
import top.niunaijun.blackbox.utils.compat.TaskDescriptionCompat;

/**
 * Proxy for the Android Activity Task Manager system service (IActivityTaskManager).
 * Intercepts activity task management operations on Android 10+ where the
 * activity task manager was separated from the activity manager. Currently
 * handles task description fixes for proper display in the recent apps list
 * on Android 10 through 11.
 *
 * @author Milk
 */
@ScanClass(ActivityManagerCommonProxy.class)
public class IActivityTaskManagerProxy extends BinderInvocationStub {
    /** Tag for logging. */
    public static final String TAG = "ActivityTaskManager";

    /**
     * Constructs a new proxy by obtaining the activity_task binder service.
     */
    public IActivityTaskManagerProxy() {
        super(BRServiceManager.get().getService("activity_task"));
    }

    /**
     * Returns the IActivityTaskManager interface instance from the system service.
     *
     * @return the original IActivityTaskManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRIActivityTaskManagerStub.get().asInterface(BRServiceManager.get().getService("activity_task"));
    }

    /**
     * Replaces the system Activity Task Manager service with this proxy instance.
     * Also replaces the singleton reference in ActivityTaskManager.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("activity_task");
        BRActivityTaskManager.get().getService();
        Object o = BRActivityTaskManager.get().IActivityTaskManagerSingleton();
        BRSingleton.get(o)._set_mInstance(BRIActivityTaskManagerStub.get().asInterface(this));
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
     * Hook that intercepts {@code setTaskDescription} on Android 10 through 11
     * to fix task description display in the recent apps list.
     */
    // for >= Android 10 && < Android 12
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
