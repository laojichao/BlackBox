package top.niunaijun.blackboxa.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import top.niunaijun.blackboxa.app.App

/**
 * Holds a reference to the currently displayed [Toast] to allow cancellation
 * before showing a new one, preventing toast queue buildup.
 */
var toastImpl: Toast? = null

/**
 * Extension function on [Context] that shows a short-duration [Toast] message.
 * Cancels any previously displayed toast before showing the new one.
 *
 * @param msg The message text to display in the toast.
 */
fun Context.toast(msg: String) {
    toastImpl?.cancel()
    toastImpl = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    toastImpl?.show()
}

/**
 * Shows a short-duration [Toast] message using the application context.
 *
 * @param msg The message text to display in the toast.
 */
fun toast(msg: String) {
    App.getContext().toast(msg)
}

/**
 * Shows a short-duration [Toast] message by resolving a string resource ID.
 *
 * @param msgID The string resource ID to display.
 */
fun toast(@StringRes msgID: Int) {
    toast(getString(msgID))
}