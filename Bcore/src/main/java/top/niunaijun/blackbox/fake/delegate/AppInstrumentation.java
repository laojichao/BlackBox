package top.niunaijun.blackbox.fake.delegate;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import java.lang.reflect.Field;

import black.android.app.BRActivity;
import black.android.app.BRActivityThread;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.HookManager;
import top.niunaijun.blackbox.fake.hook.IInjectHook;
import top.niunaijun.blackbox.fake.service.HCallbackProxy;
import top.niunaijun.blackbox.fake.service.IActivityClientProxy;
import top.niunaijun.blackbox.utils.HackAppUtils;
import top.niunaijun.blackbox.utils.compat.ActivityCompat;
import top.niunaijun.blackbox.utils.compat.ActivityManagerCompat;
import top.niunaijun.blackbox.utils.compat.ContextCompat;

/**
 * Singleton instrumentation delegate that intercepts the app's instrumentation callbacks
 * for the virtual environment. Wraps the host {@link Instrumentation} and hooks activity
 * creation, application creation, and other lifecycle events to ensure proper context
 * and configuration within BlackBox.
 */
public final class AppInstrumentation extends BaseInstrumentationDelegate implements IInjectHook {

    private static final String TAG = AppInstrumentation.class.getSimpleName();

    private static AppInstrumentation sAppInstrumentation;

    /**
     * Returns the singleton instance of {@link AppInstrumentation}, creating it lazily
     * on first access using double-checked locking.
     *
     * @return the singleton AppInstrumentation instance
     */
    public static AppInstrumentation get() {
        if (sAppInstrumentation == null) {
            synchronized (AppInstrumentation.class) {
                if (sAppInstrumentation == null) {
                    sAppInstrumentation = new AppInstrumentation();
                }
            }
        }
        return sAppInstrumentation;
    }

    /**
     * Constructs a new AppInstrumentation instance.
     */
    public AppInstrumentation() {
    }

    /**
     * Injects this instrumentation into the current ActivityThread by replacing
     * the host's instrumentation with this delegate. Skips injection if the
     * instrumentation is already properly set up.
     */
    @Override
    public void injectHook() {
        try {
            Instrumentation mInstrumentation = getCurrInstrumentation();
            if (mInstrumentation == this || checkInstrumentation(mInstrumentation))
                return;
            mBaseInstrumentation = (Instrumentation) mInstrumentation;
            BRActivityThread.get(BlackBoxCore.mainThread())._set_mInstrumentation(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Instrumentation getCurrInstrumentation() {
        Object currentActivityThread = BlackBoxCore.mainThread();
        return BRActivityThread.get(currentActivityThread).mInstrumentation();
    }

    /**
     * Checks whether the instrumentation environment has been tampered with.
     *
     * @return true if the current instrumentation is not the expected delegate, false otherwise
     */
    @Override
    public boolean isBadEnv() {
        return !checkInstrumentation(getCurrInstrumentation());
    }

    private boolean checkInstrumentation(Instrumentation instrumentation) {
        if (instrumentation instanceof AppInstrumentation) {
            return true;
        }
        Class<?> clazz = instrumentation.getClass();
        if (Instrumentation.class.equals(clazz)) {
            return false;
        }
        do {
            assert clazz != null;
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (Instrumentation.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    try {
                        Object obj = field.get(instrumentation);
                        if ((obj instanceof AppInstrumentation)) {
                            return true;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        } while (!Instrumentation.class.equals(clazz));
        return false;
    }

    private void checkHCallback() {
        HookManager.get().checkEnv(HCallbackProxy.class);
    }

    private void checkActivity(Activity activity) {
        Log.d(TAG, "callActivityOnCreate: " + activity.getClass().getName());
        HackAppUtils.enableQQLogOutput(activity.getPackageName(), activity.getClassLoader());
        checkHCallback();
        HookManager.get().checkEnv(IActivityClientProxy.class);
        ActivityInfo info = BRActivity.get(activity).mActivityInfo();
        ContextCompat.fix(activity);
        ActivityCompat.fix(activity);
        if (info.theme != 0) {
            activity.getTheme().applyStyle(info.theme, true);
        }
        ActivityManagerCompat.setActivityOrientation(activity, info.screenOrientation);
    }

    /**
     * Creates a new Application instance, fixing the context and loading Xposed modules.
     *
     * @param cl        the ClassLoader with which to instantiate the object
     * @param className the class name of the Application to instantiate
     * @param context   the context to use for initialization
     * @return the newly created Application
     * @throws InstantiationException if the class cannot be instantiated
     * @throws IllegalAccessException if the class or its nullary constructor is not accessible
     * @throws ClassNotFoundException if the class cannot be found
     */
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ContextCompat.fix(context);
        BActivityThread.currentActivityThread().loadXposed(context);
        return super.newApplication(cl, className, context);
    }

    /**
     * Called when an activity is being created with a persistent state bundle.
     * Applies context fixes and activity configuration before delegating to the base.
     *
     * @param activity         the activity being created
     * @param icicle           the saved instance state bundle, or null
     * @param persistentState  the persistent saved instance state, or null
     */
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        checkActivity(activity);
        super.callActivityOnCreate(activity, icicle, persistentState);
    }

    /**
     * Called when an activity is being created. Applies context fixes and activity
     * configuration before delegating to the base.
     *
     * @param activity  the activity being created
     * @param icicle    the saved instance state bundle, or null
     */
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        checkActivity(activity);
        super.callActivityOnCreate(activity, icicle);
    }

    /**
     * Called when the application is being created. Checks the HCallback environment
     * before delegating to the base.
     *
     * @param app the Application being created
     */
    @Override
    public void callApplicationOnCreate(Application app) {
        checkHCallback();
        super.callApplicationOnCreate(app);
    }

    /**
     * Creates a new Activity instance from a class name. Falls back to the base
     * instrumentation if the class is not found via the delegate's class loader.
     *
     * @param cl        the ClassLoader to use
     * @param className the fully qualified class name of the Activity
     * @param intent    the Intent that started the Activity
     * @return the newly created Activity
     * @throws InstantiationException if the class cannot be instantiated
     * @throws IllegalAccessException if the class or its nullary constructor is not accessible
     * @throws ClassNotFoundException if the class cannot be found
     */
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return super.newActivity(cl, className, intent);
        } catch (ClassNotFoundException e) {
            return mBaseInstrumentation.newActivity(cl, className, intent);
        }
    }
}
