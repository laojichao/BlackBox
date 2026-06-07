package top.niunaijun.blackboxa.bean

import android.graphics.drawable.Drawable
import top.niunaijun.blackbox.entity.location.BLocation

/**
 * Represents an application's fake location configuration within a specific virtual user environment.
 *
 * Each app can have three location patterns: Global (use global fake location),
 * Self (use its own configured location), or Close (use real location).
 *
 * @property userID The virtual user ID this configuration belongs to.
 * @property name The display name of the application.
 * @property icon The application icon drawable.
 * @property packageName The unique Android package name of the application.
 * @property fakeLocationPattern The location pattern mode for this app (Global, Self, or Close).
 * @property fakeLocation The custom fake location configured for this app, or null if not set.
 */
data class FakeLocationBean(
    val userID: Int,
    val name: String,
    val icon: Drawable,
    val packageName: String,
    var fakeLocationPattern: Int,
    var fakeLocation: BLocation?
)

/**
 * Represents the result of a fake location installation operation for a specific user.
 *
 * @property userID The virtual user ID associated with the operation.
 * @property success Whether the installation operation succeeded.
 * @property msg A human-readable message describing the result of the operation.
 */
data class FakeLocationBeanInstallBean(val userID: Int, val success: Boolean, val msg: String)