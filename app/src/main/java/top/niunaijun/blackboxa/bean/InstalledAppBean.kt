package top.niunaijun.blackboxa.bean

import android.graphics.drawable.Drawable

/**
 * Represents an application with its installation status within the virtual environment.
 *
 * Extends [AppInfo] with an installation flag indicating whether the app is currently
 * installed inside the virtual machine for a specific user.
 *
 * @property name The display name of the application.
 * @property icon The application icon drawable.
 * @property packageName The unique Android package name of the application.
 * @property sourceDir The file path to the application's APK on the device.
 * @property isInstall Whether the application is installed in the virtual environment.
 */
data class InstalledAppBean(val name: String, val icon: Drawable, val packageName: String, val sourceDir: String, val isInstall: Boolean)
