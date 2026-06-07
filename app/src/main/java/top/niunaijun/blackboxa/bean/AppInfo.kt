package top.niunaijun.blackboxa.bean

import android.graphics.drawable.Drawable

/**
 * Represents basic information about an application installed on the device.
 *
 * Used to display app details in the installed applications list and manage
 * app operations within the virtual environment.
 *
 * @property name The display name of the application.
 * @property icon The application icon drawable.
 * @property packageName The unique Android package name of the application.
 * @property sourceDir The file path to the application's APK on the device.
 * @property isXpModule Whether the application is an Xposed module.
 */
data class AppInfo(val name: String, val icon: Drawable, val packageName: String, val sourceDir: String, val isXpModule: Boolean)