package top.niunaijun.blackboxa.util

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.app.App
import top.niunaijun.blackboxa.app.AppManager
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.util.ContextUtil.openAppSystemSettings
import top.niunaijun.blackboxa.view.main.ShortcutActivity

/**
 * Utility object for creating and managing home screen shortcuts for virtual environment apps.
 *
 * Handles shortcut creation via [ShortcutManagerCompat], including user prompt dialogs
 * for custom shortcut names and permission guidance when shortcuts fail to pin.
 */
object ShortcutUtil {


    /**
     * Creates a home screen shortcut for an application within the virtual environment.
     *
     * If pin shortcuts are supported, displays a dialog allowing the user to customize
     * the shortcut name. After successful creation, optionally shows a permission
     * guidance dialog if needed.
     *
     * @param context The context used to display dialogs and interact with the shortcut manager.
     * @param userID The virtual user ID the shortcut should launch for.
     * @param info The [AppInfo] of the application to create a shortcut for.
     */
    fun createShortcut(context: Context, userID: Int, info: AppInfo) {

        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            val labelName = info.name + userID
            val intent = Intent(context, ShortcutActivity::class.java)
                .setAction(Intent.ACTION_MAIN)
                .putExtra("pkg", info.packageName)
                .putExtra("userId", userID)
            MaterialDialog(context).show {
                title(res = R.string.app_shortcut)
                input(
                    hintRes = R.string.shortcut_name,
                    prefill = labelName
                ) { _, input ->

                    val shortcutInfo: ShortcutInfoCompat =
                        ShortcutInfoCompat.Builder(context, info.packageName + userID)
                            .setIntent(intent)
                            .setShortLabel(input)
                            .setLongLabel(input)
                            .setIcon(IconCompat.createWithBitmap(info.icon.toBitmap()))
                            .build()

                    ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null)
                    showAllowPermissionDialog(context)
                }
                positiveButton(R.string.done)
                negativeButton(R.string.cancel)
            }

        } else {
            toast(R.string.cannot_create_shortcut)
        }
    }

    /**
     * Shows a dialog guiding the user to grant shortcut pinning permission
     * if the permission dialog has not been permanently dismissed.
     *
     * @param context The context used to display the dialog.
     */
    private fun showAllowPermissionDialog(context: Context){
        if (!AppManager.mBlackBoxLoader.showShortcutPermissionDialog()){
            return
        }

        MaterialDialog(context).show {
            title(R.string.try_add_shortcut)
            message(R.string.add_shortcut_fail_msg)
            positiveButton(R.string.done)
            negativeButton(R.string.permission_setting){
                App.getContext().openAppSystemSettings()
            }

            neutralButton(R.string.no_reminders){
                AppManager.mBlackBoxLoader.invalidShortcutPermissionDialog(false)
            }
        }

    }
}