package top.niunaijun.blackboxa.app

import android.content.Context
import android.content.SharedPreferences
import top.niunaijun.blackboxa.view.main.BlackBoxLoader

/**
 * Singleton manager that coordinates application-level initialization for the BlackBox host.
 *
 * Holds lazy-initialized references to [BlackBoxLoader] and [BlackBoxCore], manages
 * SharedPreferences for user remarks, and orchestrates the attachBaseContext and onCreate
 * lifecycle phases.
 */
object AppManager {
    /** The [BlackBoxLoader] instance managing BlackBox engine configuration. */
    @JvmStatic
    val mBlackBoxLoader by lazy {
        BlackBoxLoader()
    }

    /** The [BlackBoxCore] virtualization engine singleton. */
    @JvmStatic
    val mBlackBoxCore by lazy {
        mBlackBoxLoader.getBlackBoxCore()
    }

    /** SharedPreferences for storing user profile remark names. */
    @JvmStatic
    val mRemarkSharedPreferences: SharedPreferences by lazy {
        App.getContext().getSharedPreferences("UserRemark",Context.MODE_PRIVATE)
    }

    /**
     * Attaches the base context to the BlackBox engine and registers lifecycle callbacks.
     *
     * @param context the application base context
     */
    fun doAttachBaseContext(context: Context) {
        try {
            mBlackBoxLoader.attachBaseContext(context)
            mBlackBoxLoader.addLifecycleCallback()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Finalizes BlackBox engine initialization and starts third-party services.
     *
     * @param context the application context
     */
    fun doOnCreate(context: Context) {
        mBlackBoxLoader.doOnCreate(context)
        initThirdService(context)
    }

    private fun initThirdService(context: Context) {}
}
