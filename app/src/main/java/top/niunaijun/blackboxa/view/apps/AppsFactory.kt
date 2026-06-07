package top.niunaijun.blackboxa.view.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import top.niunaijun.blackboxa.data.AppsRepository

/**
 * ViewModelProvider.Factory for creating [AppsViewModel] instances.
 *
 * Supplies an [AppsRepository] dependency to the ViewModel during construction.
 *
 * @property appsRepository the repository providing app data operations.
 */
@Suppress("UNCHECKED_CAST")
class AppsFactory(private val appsRepository: AppsRepository) : ViewModelProvider.NewInstanceFactory() {

    /**
     * Creates a new [AppsViewModel] with the injected [AppsRepository].
     *
     * @param modelClass the ViewModel class to instantiate.
     * @return a new [AppsViewModel] instance.
     */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AppsViewModel(appsRepository) as T
    }
}