package top.niunaijun.blackboxa.util

import androidx.annotation.StringRes
import top.niunaijun.blackboxa.app.App


/**
 * Retrieves a localized string resource from the application context.
 *
 * @param id The string resource ID.
 * @param arg Optional format arguments to substitute into the string resource.
 * @return The formatted string resource value.
 */
fun getString(@StringRes id: Int, vararg arg: String): String {
    if(arg.isEmpty()){
        return App.getContext().getString(id)
    }
    return App.getContext().getString(id,*arg)
}

