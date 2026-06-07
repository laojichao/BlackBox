package top.niunaijun.blackboxa.view.fake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import top.niunaijun.blackboxa.data.FakeLocationRepository

/**
 * ViewModelProvider.Factory for creating [FakeLocationViewModel] instances.
 *
 * Supplies a [FakeLocationRepository] dependency to the ViewModel during construction.
 *
 * @property repo the repository providing fake location data operations.
 */
class FakeLocationFactory(private val repo: FakeLocationRepository) :
    ViewModelProvider.NewInstanceFactory() {

    /**
     * Creates a new [FakeLocationViewModel] with the injected [FakeLocationRepository].
     *
     * @param modelClass the ViewModel class to instantiate.
     * @return a new [FakeLocationViewModel] instance.
     */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FakeLocationViewModel(repo) as T
    }
}