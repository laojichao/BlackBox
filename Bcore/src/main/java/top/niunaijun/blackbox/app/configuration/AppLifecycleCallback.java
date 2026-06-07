package top.niunaijun.blackbox.app.configuration;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

/**
 * Callback interface for monitoring the lifecycle of virtual applications and their activities.
 * <p>
 * Extends Android's {@link Application.ActivityLifecycleCallbacks} with additional hooks
 * specific to the BlackBox virtual environment, allowing callers to observe application
 * creation, binding, and activity transitions for apps running inside the virtual container.
 * <p>
 * Register instances via {@link top.niunaijun.blackbox.BlackBoxCore#getAppLifecycleCallbacks()}.
 *
 * @author Milk
 */
public class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {
    /** A no-op singleton instance that performs no action on any callback. */
    public static AppLifecycleCallback EMPTY = new AppLifecycleCallback() {

    };

    /**
     * Called before the virtual application's {@link Application} object is created.
     * At this point the package context is available but {@code Application.onCreate()} has not run.
     *
     * @param packageName the package name of the virtual application
     * @param processName the process name the application is running in
     * @param context     the package-specific context created for the virtual app
     * @param userId      the virtual user ID under which the app is running
     */
    public void beforeCreateApplication(String packageName, String processName, Context context, int userId) {

    }

    /**
     * Called after the {@link Application} object is created but before its
     * {@code onCreate()} method is invoked.
     *
     * @param packageName the package name of the virtual application
     * @param processName the process name the application is running in
     * @param application the Application instance that was just created
     * @param userId      the virtual user ID under which the app is running
     */
    public void beforeApplicationOnCreate(String packageName, String processName, Application application, int userId) {

    }

    /**
     * Called after the virtual application's {@code Application.onCreate()} has completed.
     *
     * @param packageName the package name of the virtual application
     * @param processName the process name the application is running in
     * @param application the Application instance whose onCreate just finished
     * @param userId      the virtual user ID under which the app is running
     */
    public void afterApplicationOnCreate(String packageName, String processName, Application application, int userId) {

    }

    /**
     * Called when an activity is created inside the virtual environment.
     *
     * @param activity           the activity that was created
     * @param savedInstanceState the saved instance state bundle, or {@code null} if not restoring
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    /**
     * Called when an activity inside the virtual environment becomes visible.
     *
     * @param activity the activity that started
     */
    @Override
    public void onActivityStarted(Activity activity) {

    }

    /**
     * Called when an activity inside the virtual environment enters the resumed (foreground) state.
     *
     * @param activity the activity that resumed
     */
    @Override
    public void onActivityResumed(Activity activity) {

    }

    /**
     * Called when an activity inside the virtual environment is paused.
     *
     * @param activity the activity that was paused
     */
    @Override
    public void onActivityPaused(Activity activity) {

    }

    /**
     * Called when an activity inside the virtual environment is no longer visible.
     *
     * @param activity the activity that stopped
     */
    @Override
    public void onActivityStopped(Activity activity) {

    }

    /**
     * Called when an activity inside the virtual environment saves its instance state.
     *
     * @param activity  the activity saving state
     * @param outState  the bundle to write saved state into
     */
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    /**
     * Called when an activity inside the virtual environment is destroyed.
     *
     * @param activity the activity that was destroyed
     */
    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
