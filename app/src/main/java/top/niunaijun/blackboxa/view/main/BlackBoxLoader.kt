package top.niunaijun.blackboxa.view.main

import android.app.Application
import android.content.Context
import android.util.Log
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.app.BActivityThread
import top.niunaijun.blackbox.app.configuration.AppLifecycleCallback
import top.niunaijun.blackbox.app.configuration.ClientConfiguration
import top.niunaijun.blackboxa.app.App
import top.niunaijun.blackboxa.biz.cache.AppSharedPreferenceDelegate
import java.io.File

/**
 * Manages the initialization and configuration of the BlackBox virtualization engine.
 *
 * This loader is responsible for:
 * - Attaching the base context and configuring the [BlackBoxCore] client settings
 * - Managing user preferences for root hiding, Xposed hiding, and daemon service
 * - Registering application lifecycle callbacks for virtual app processes
 * - Creating and providing the [BlackBoxCore] singleton instance
 *
 * Preferences are persisted using [AppSharedPreferenceDelegate] and applied during
 * the [attachBaseContext] and [doOnCreate] lifecycle phases.
 */
class BlackBoxLoader {


    private var mHideRoot by AppSharedPreferenceDelegate(App.getContext(), false)
    private var mHideXposed by AppSharedPreferenceDelegate(App.getContext(), false)
    private var mDaemonEnable by AppSharedPreferenceDelegate(App.getContext(), false)
    private var mShowShortcutPermissionDialog by AppSharedPreferenceDelegate(App.getContext(), true)


    /** Returns whether root detection hiding is enabled for virtual apps. */
    fun hideRoot(): Boolean {
        return mHideRoot
    }

    /**
     * Sets whether root detection hiding should be enabled for virtual apps.
     *
     * @param hideRoot true to hide root, false to disable hiding
     */
    fun invalidHideRoot(hideRoot: Boolean) {
        this.mHideRoot = hideRoot
    }

    /** Returns whether Xposed framework detection hiding is enabled for virtual apps. */
    fun hideXposed(): Boolean {
        return mHideXposed
    }

    /**
     * Sets whether Xposed framework detection hiding should be enabled for virtual apps.
     *
     * @param hideXposed true to hide Xposed, false to disable hiding
     */
    fun invalidHideXposed(hideXposed: Boolean) {
        this.mHideXposed = hideXposed
    }

    /** Returns whether the background daemon service is enabled for keeping virtual apps alive. */
    fun daemonEnable(): Boolean {
        return mDaemonEnable
    }

    /**
     * Sets whether the background daemon service should be enabled.
     *
     * @param enable true to enable the daemon service, false to disable
     */
    fun invalidDaemonEnable(enable: Boolean) {
        this.mDaemonEnable = enable
    }

    /** Returns whether the shortcut permission dialog should be shown to the user. */
    fun showShortcutPermissionDialog(): Boolean {
        return mShowShortcutPermissionDialog
    }

    /**
     * Sets whether the shortcut permission dialog should be displayed.
     *
     * @param show true to show the dialog, false to suppress it
     */
    fun invalidShortcutPermissionDialog(show: Boolean) {
        this.mShowShortcutPermissionDialog = show
    }

    /**
     * Returns the [BlackBoxCore] singleton instance, creating it if necessary.
     *
     * @return the BlackBoxCore virtualization engine instance
     */
    fun getBlackBoxCore(): BlackBoxCore {
        return BlackBoxCore.get()
    }

    /**
     * Registers an [AppLifecycleCallback] with [BlackBoxCore] to observe virtual app
     * lifecycle events such as application creation and initialization.
     */
    fun addLifecycleCallback() {
        BlackBoxCore.get().addAppLifecycleCallback(object : AppLifecycleCallback() {
            override fun beforeCreateApplication(
                packageName: String?,
                processName: String?,
                context: Context?,
                userId: Int
            ) {
                Log.d(
                    TAG,
                    "beforeCreateApplication: pkg $packageName, processName $processName,userID:${BActivityThread.getUserId()}"
                )
            }


            override fun beforeApplicationOnCreate(
                packageName: String?,
                processName: String?,
                application: Application?,
                userId: Int
            ) {
                Log.d(TAG, "beforeApplicationOnCreate: pkg $packageName, processName $processName")
            }

            override fun afterApplicationOnCreate(
                packageName: String?,
                processName: String?,
                application: Application?,
                userId: Int
            ) {
                Log.d(TAG, "afterApplicationOnCreate: pkg $packageName, processName $processName")
//                RockerManager.init(application,userId)
            }
        })
    }

    /**
     * Attaches the base context to [BlackBoxCore] and applies the current client configuration,
     * including root hiding, Xposed hiding, and daemon service settings.
     *
     * @param context the application base context
     */
    fun attachBaseContext(context: Context) {
        BlackBoxCore.get().doAttachBaseContext(context, object : ClientConfiguration() {
            override fun getHostPackageName(): String {
                return context.packageName
            }

            override fun isHideRoot(): Boolean {
                return mHideRoot
            }

            override fun isHideXposed(): Boolean {
                return mHideXposed
            }

            override fun isEnableDaemonService(): Boolean {
                return mDaemonEnable
            }

            override fun requestInstallPackage(file: File?, userId: Int): Boolean {
                val packageInfo =
                    context.packageManager.getPackageArchiveInfo(file!!.absolutePath, 0)
                return false
            }
        })
    }

    /**
     * Finalizes the BlackBoxCore initialization by calling [BlackBoxCore.doCreate].
     * This should be called during the application's [android.app.Application.onCreate] phase.
     *
     * @param context the application context
     */
    fun doOnCreate(context: Context) {
        BlackBoxCore.get().doCreate()

    }


    companion object {

        val TAG: String = BlackBoxLoader::class.java.simpleName

    }

}