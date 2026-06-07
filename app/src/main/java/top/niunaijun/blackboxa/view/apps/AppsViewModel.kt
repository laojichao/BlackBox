package top.niunaijun.blackboxa.view.apps

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.data.AppsRepository
import top.niunaijun.blackboxa.view.base.BaseViewModel

/**
 * ViewModel for managing installed applications within a virtual user environment.
 *
 * Handles operations such as loading the installed app list, installing/uninstalling APKs,
 * clearing app data, launching apps, and updating app sort order. Delegates all work
 * to [AppsRepository] via coroutine launches on the IO dispatcher.
 *
 * @property repo the repository providing app management data operations.
 */
class AppsViewModel(private val repo: AppsRepository) : BaseViewModel() {

    /** LiveData holding the list of installed [AppInfo] items for the current user. */
    val appsLiveData = MutableLiveData<List<AppInfo>>()

    /** LiveData holding the result message string from install/uninstall/clear operations. */
    val resultLiveData = MutableLiveData<String>()

    /** LiveData holding whether the app launch succeeded (true) or failed (false). */
    val launchLiveData = MutableLiveData<Boolean>()

    /** LiveData used as a trigger to save updated app sort order; leverages LiveData's single-update semantics. */
    val updateSortLiveData = MutableLiveData<Boolean>()

    /**
     * Loads the list of installed applications for the given virtual user.
     *
     * @param userId the virtual user ID to query installed apps for.
     */
    fun getInstalledApps(userId: Int) {
        launchOnUI {
            repo.getVmInstallList(userId, appsLiveData)
        }
    }

    /**
     * Installs an APK from the given source into the specified virtual user environment.
     *
     * @param source the file path or content URI of the APK to install.
     * @param userID the virtual user ID to install the app into.
     */
    fun install(source: String, userID: Int) {
        launchOnUI {
            repo.installApk(source, userID, resultLiveData)
        }
    }

    /**
     * Uninstalls the specified package from the given virtual user environment.
     *
     * @param packageName the package name of the app to uninstall.
     * @param userID the virtual user ID to uninstall the app from.
     */
    fun unInstall(packageName: String, userID: Int) {
        launchOnUI {
            repo.unInstall(packageName, userID, resultLiveData)
        }
    }

    /**
     * Clears all application data for the specified package in the given virtual user environment.
     *
     * @param packageName the package name of the app whose data will be cleared.
     * @param userID the virtual user ID where the app is installed.
     */
    fun clearApkData(packageName: String,userID: Int){
        launchOnUI {
            repo.clearApkData(packageName,userID,resultLiveData)
        }
    }

    /**
     * Launches the specified application within the given virtual user environment.
     *
     * @param packageName the package name of the app to launch.
     * @param userID the virtual user ID to launch the app in.
     */
    fun launchApk(packageName: String, userID: Int) {
        launchOnUI {
            repo.launchApk(packageName, userID, launchLiveData)
        }
    }

    /**
     * Persists the updated app sort order for the given virtual user.
     *
     * @param userID the virtual user ID whose app order is being updated.
     * @param dataList the reordered list of [AppInfo] items reflecting the new order.
     */
    fun updateApkOrder(userID: Int,dataList:List<AppInfo>){
        launchOnUI {
            repo.updateApkOrder(userID,dataList)
        }
    }
}