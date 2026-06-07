package top.niunaijun.blackboxa.app.rocker

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Convenience interface implementing [Application.ActivityLifecycleCallbacks] with empty defaults.
 *
 * Implementors can override only the lifecycle methods they need without providing
 * stub implementations for every callback.
 */
interface BaseActivityLifecycleCallback : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }
    override fun onActivityStarted(activity: Activity) {

    }
    override fun onActivityResumed(activity: Activity) {

    }
    override fun onActivityPaused(activity: Activity) {

    }
    override fun onActivityStopped(activity: Activity) {

    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }
    override fun onActivityDestroyed(activity: Activity) {

    }
}