package top.niunaijun.blackboxa.view.fake

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.entity.location.BLocation
import top.niunaijun.blackboxa.bean.FakeLocationBean
import top.niunaijun.blackboxa.data.FakeLocationRepository
import top.niunaijun.blackboxa.view.base.BaseViewModel

/**
 * ViewModel for managing fake location configurations per installed application.
 *
 * Handles loading the list of installed apps with their fake location settings,
 * updating the location mode pattern, and setting custom GPS coordinates.
 * Delegates all work to [FakeLocationRepository] via coroutine launches on the IO dispatcher.
 *
 * @property mRepo the repository providing fake location data operations.
 */
class FakeLocationViewModel(private val mRepo: FakeLocationRepository) : BaseViewModel() {

    /** LiveData holding the list of installed apps with their [FakeLocationBean] settings. */
    val appsLiveData = MutableLiveData<List<FakeLocationBean>>()

    /**
     * Loads the list of installed applications with their fake location configurations.
     *
     * @param userID the virtual user ID to query installed apps for.
     */
    fun getInstallAppList(userID: Int) {
        launchOnUI {
            mRepo.getInstalledAppList(userID, appsLiveData)
        }
    }

    /**
     * Updates the fake location mode pattern for the specified app.
     *
     * @param userId the virtual user ID.
     * @param pkg the package name of the target application.
     * @param pattern the location mode pattern constant (e.g., [BLocationManager.CLOSE_MODE] or [BLocationManager.OWN_MODE]).
     */
    fun setPattern(userId: Int, pkg: String, pattern: Int) {
        launchOnUI {
            mRepo.setPattern(userId, pkg, pattern)
        }
    }

    /**
     * Sets the custom fake GPS location for the specified app.
     *
     * @param userId the virtual user ID.
     * @param pkg the package name of the target application.
     * @param location the [BLocation] containing the latitude and longitude to fake.
     */
    fun setLocation(userId: Int, pkg: String, location: BLocation) {
        launchOnUI {
            mRepo.setLocation(userId, pkg, location)
        }
    }

}