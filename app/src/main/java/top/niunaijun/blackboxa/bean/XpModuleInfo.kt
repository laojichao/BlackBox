package top.niunaijun.blackboxa.bean

import android.graphics.drawable.Drawable

/**
 * Represents metadata about an installed Xposed module.
 *
 * Used to display Xposed module information in the module management UI,
 * including its name, description, version, and enabled state.
 *
 * @property name The display name of the Xposed module.
 * @property desc A short description of what the module does.
 * @property packageName The unique Android package name of the module.
 * @property version The version string of the module (e.g., "1.0.0").
 * @property enable Whether the module is currently enabled in the Xposed framework.
 * @property icon The module's icon drawable.
 */
data class XpModuleInfo(
        val name: String,
        val desc: String,
        val packageName: String,
        val version: String,
        var enable: Boolean,
        val icon: Drawable
)
