package top.niunaijun.blackboxa.data

import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.entity.location.BLocation
import top.niunaijun.blackbox.fake.frameworks.BLocationManager
import top.niunaijun.blackboxa.bean.FakeLocationBean

/**
 * Repository for managing per-app fake location configurations in the virtual environment.
 *
 * Retrieves installed applications and queries the fake location pattern and coordinates
 * for each one. Location mode and location configuration are independent concepts:
 * the mode (pattern) determines whether fake location is active, while the location
 * config stores the actual coordinates. Each application can use one of three patterns:
 * Global (use global fake location), Self (use its own config), or Close (use real location).
 */
class FakeLocationRepository {
    val TAG: String = "FakeLocationRepository"

    /**
     * Sets the location pattern for an application in the virtual environment.
     *
     * @param userId The virtual user ID.
     * @param pkg The package name of the application.
     * @param pattern The location pattern to set (Global, Self, or Close).
     */
    fun setPattern(userId: Int, pkg: String, pattern: Int) {
        BLocationManager.get().setPattern(userId, pkg, pattern)
    }

    /**
     * Gets the location pattern for an application.
     *
     * @param userId The virtual user ID.
     * @param pkg The package name of the application.
     * @return The current location pattern integer value.
     */
    private fun getPattern(userId: Int, pkg: String): Int {
        return BLocationManager.get().getPattern(userId, pkg)
    }

    /**
     * Gets the configured fake location for an application.
     *
     * @param userId The virtual user ID.
     * @param pkg The package name of the application.
     * @return The configured [BLocation], or null if no custom location is set.
     */
    private fun getLocation(userId: Int, pkg: String): BLocation? {
        return BLocationManager.get().getLocation(userId, pkg)
    }

    /**
     * Sets a custom fake location for an application in the virtual environment.
     *
     * @param userId The virtual user ID.
     * @param pkg The package name of the application.
     * @param location The [BLocation] coordinates to use as the fake location.
     */
    fun setLocation(userId: Int, pkg: String, location: BLocation) {
        BLocationManager.get().setLocation(userId, pkg, location)
    }

    /**
     * Retrieves all installed applications for a virtual user along with their
     * fake location configurations and posts the result to [appsFakeLiveData].
     *
     * @param userID The virtual user ID to query.
     * @param appsFakeLiveData LiveData to receive the list of [FakeLocationBean] results.
     */
    fun getInstalledAppList(
        userID: Int,
        appsFakeLiveData: MutableLiveData<List<FakeLocationBean>>
    ) {
        val installedList = mutableListOf<FakeLocationBean>()
        val installedApplications: List<ApplicationInfo> =
            BlackBoxCore.get().getInstalledApplications(0, userID)
        // List<ApplicationInfo> -> List<FakeLocationBean>
        for (installedApplication in installedApplications) {
//            val file = File(installedApplication.sourceDir)
//
//            if ((installedApplication.flags and ApplicationInfo.FLAG_SYSTEM) != 0) continue
//
//            if (!AbiUtils.isSupport(file)) continue
//
//            val isXpModule = BlackBoxCore.get().isXposedModule(file)

            val info = FakeLocationBean(
                userID,
                installedApplication.loadLabel(BlackBoxCore.getPackageManager()).toString(),
                installedApplication.loadIcon(BlackBoxCore.getPackageManager()),
                installedApplication.packageName,
                getPattern(userID, installedApplication.packageName),
                getLocation(userID, installedApplication.packageName)
            )
            installedList.add(info)
        }

        Log.d(TAG, installedList.joinToString(","))
        appsFakeLiveData.postValue(installedList)
    }
}