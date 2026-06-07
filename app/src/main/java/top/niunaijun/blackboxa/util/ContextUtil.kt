package top.niunaijun.blackboxa.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Utility object providing context-related extension functions.
 */
object ContextUtil {

    /**
     * Opens the system application details settings screen for the current application.
     * Launches with [Intent.FLAG_ACTIVITY_NEW_TASK] so it can be called from non-Activity contexts.
     */
    fun Context.openAppSystemSettings() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", packageName, null)
        })
    }
}