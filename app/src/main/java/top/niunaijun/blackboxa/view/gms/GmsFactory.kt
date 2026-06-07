package top.niunaijun.blackboxa.view.gms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import top.niunaijun.blackboxa.data.GmsRepository

/**
 * ViewModelProvider.Factory for creating [GmsViewModel] instances.
 *
 * Supplies a [GmsRepository] dependency to the ViewModel during construction.
 *
 * @property repo the repository providing GMS data operations.
 */
class GmsFactory(private val repo:GmsRepository): ViewModelProvider.NewInstanceFactory() {

    /**
     * Creates a new [GmsViewModel] with the injected [GmsRepository].
     *
     * @param modelClass the ViewModel class to instantiate.
     * @return a new [GmsViewModel] instance.
     */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GmsViewModel(repo) as T
    }
}