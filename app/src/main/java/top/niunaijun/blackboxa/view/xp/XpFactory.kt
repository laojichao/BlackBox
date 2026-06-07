package top.niunaijun.blackboxa.view.xp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import top.niunaijun.blackboxa.data.XpRepository

/**
 * ViewModelProvider factory for creating [XpViewModel] instances with an injected [XpRepository].
 *
 * @param repo the Xposed module repository used by the created ViewModel
 */
@Suppress("UNCHECKED_CAST")
class XpFactory(private val repo:XpRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return XpViewModel(repo) as T
    }
}