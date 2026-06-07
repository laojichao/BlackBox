package top.niunaijun.blackboxa.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import top.niunaijun.blackboxa.data.AppsRepository

/**
 * ViewModelProvider.Factory for creating [ListViewModel] instances.
 *
 * Supplies an [AppsRepository] dependency to the ViewModel during construction.
 *
 * @property appsRepository the repository providing app listing data operations.
 */
@Suppress("UNCHECKED_CAST")
class ListFactory(private val appsRepository: AppsRepository) : ViewModelProvider.NewInstanceFactory() {

    /**
     * Creates a new [ListViewModel] with the injected [AppsRepository].
     *
     * @param modelClass the ViewModel class to instantiate.
     * @return a new [ListViewModel] instance.
     */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListViewModel(appsRepository) as T
    }
}