package top.niunaijun.blackboxa.data

import android.content.pm.ApplicationInfo
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.BlackBoxCore.getPackageManager
import top.niunaijun.blackbox.utils.AbiUtils
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.app.AppManager
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.bean.InstalledAppBean
import top.niunaijun.blackboxa.util.getString
import java.io.File


/**
 * Repository responsible for managing application lifecycle within the virtual environment.
 *
 * Handles operations such as listing installed apps (both host and virtual),
 * installing/uninstalling APKs, launching apps, clearing app data, and
 * maintaining per-user app sort order. Delegates to [BlackBoxCore] for
 * all virtual environment operations.
 */
class AppsRepository {
    val TAG: String = "AppsRepository"
    private var mInstalledList = mutableListOf<AppInfo>()

    /**
     * Scans the host device for all non-system, architecture-compatible applications
     * and caches them in [mInstalledList]. Each app is checked for Xposed module compatibility.
     */
    fun previewInstallList() {
        synchronized(mInstalledList) {
            val installedApplications: List<ApplicationInfo> =
                getPackageManager().getInstalledApplications(0)
            val installedList = mutableListOf<AppInfo>()

            for (installedApplication in installedApplications) {
                val file = File(installedApplication.sourceDir)

                if ((installedApplication.flags and ApplicationInfo.FLAG_SYSTEM) != 0) continue

                if (!AbiUtils.isSupport(file)) continue

                val isXpModule = BlackBoxCore.get().isXposedModule(file)

                val info = AppInfo(
                    installedApplication.loadLabel(getPackageManager()).toString(),
                    installedApplication.loadIcon(getPackageManager()),
                    installedApplication.packageName,
                    installedApplication.sourceDir,
                    isXpModule
                )
                installedList.add(info)
            }
            this.mInstalledList.clear()
            this.mInstalledList.addAll(installedList)
        }


    }

    /**
     * Retrieves the list of installed apps for a specific virtual user and posts the result
     * to [appsLiveData]. Sets [loadingLiveData] to true during the operation.
     *
     * @param userID The virtual user ID to query installed applications for.
     * @param loadingLiveData LiveData to observe loading state changes.
     * @param appsLiveData LiveData to receive the list of [InstalledAppBean] results.
     */
    fun getInstalledAppList(
        userID: Int,
        loadingLiveData: MutableLiveData<Boolean>,
        appsLiveData: MutableLiveData<List<InstalledAppBean>>
    ) {
        loadingLiveData.postValue(true)
        synchronized(mInstalledList) {
            val blackBoxCore = BlackBoxCore.get()
            Log.d(TAG, mInstalledList.joinToString(","))
            val newInstalledList = mInstalledList.map {
                InstalledAppBean(
                    it.name,
                    it.icon,
                    it.packageName,
                    it.sourceDir,
                    blackBoxCore.isInstalled(it.packageName, userID)
                )
            }
            appsLiveData.postValue(newInstalledList)
            loadingLiveData.postValue(false)


        }

    }

    /**
     * Retrieves the list of installed Xposed modules and posts the result to [appsLiveData].
     * Only apps that are Xposed modules (from the cached install list) are included.
     * Sets [loadingLiveData] to true during the operation.
     *
     * @param loadingLiveData LiveData to observe loading state changes.
     * @param appsLiveData LiveData to receive the list of [InstalledAppBean] Xposed module results.
     */
    fun getInstalledModuleList(
        loadingLiveData: MutableLiveData<Boolean>,
        appsLiveData: MutableLiveData<List<InstalledAppBean>>
    ) {

        loadingLiveData.postValue(true)
        synchronized(mInstalledList) {
            val blackBoxCore = BlackBoxCore.get()
            val moduleList = mInstalledList.filter {
                it.isXpModule
            }.map {
                InstalledAppBean(
                    it.name,
                    it.icon,
                    it.packageName,
                    it.sourceDir,
                    blackBoxCore.isInstalledXposedModule(it.packageName)
                )
            }
            appsLiveData.postValue(moduleList)
            loadingLiveData.postValue(false)
        }

    }


    /**
     * Retrieves the list of applications installed within a virtual user's environment,
     * sorted according to the user's saved sort order. Posts the result to [appsLiveData].
     *
     * @param userId The virtual user ID to query installed applications for.
     * @param appsLiveData LiveData to receive the list of [AppInfo] results.
     */
    fun getVmInstallList(userId: Int, appsLiveData: MutableLiveData<List<AppInfo>>) {
        val sortListData =
            AppManager.mRemarkSharedPreferences.getString("AppList$userId", "")
        val sortList = sortListData?.split(",")

        val applicationList = BlackBoxCore.get().getInstalledApplications(0, userId)

        val appInfoList = mutableListOf<AppInfo>()
        applicationList.also {
            if (sortList.isNullOrEmpty()) {
                return@also
            }
            it.sortWith(AppsSortComparator(sortList))

        }.forEach {
            val info = AppInfo(
                it.loadLabel(getPackageManager()).toString(),
                it.loadIcon(getPackageManager()),
                it.packageName,
                it.sourceDir,
                isInstalledXpModule(it.packageName)
            )

            appInfoList.add(info)
        }


        appsLiveData.postValue(appInfoList)
    }

    /**
     * Checks whether the given package is registered as an Xposed module.
     *
     * @param packageName The package name to check.
     * @return true if the package is an installed Xposed module, false otherwise.
     */
    private fun isInstalledXpModule(packageName: String): Boolean {
        BlackBoxCore.get().installedXPModules.forEach {
            if (packageName == it.packageName) {
                return@isInstalledXpModule true
            }
        }

        return false
    }


    /**
     * Installs an APK into the virtual environment for the specified user.
     * Supports both file paths and URLs. Posts a success or failure message to [resultLiveData].
     * After installation, the app sort list is updated and empty users are cleaned up.
     *
     * @param source The APK source, either a local file path or a URL.
     * @param userId The virtual user ID to install the APK for.
     * @param resultLiveData LiveData to receive a human-readable result message.
     */
    fun installApk(source: String, userId: Int, resultLiveData: MutableLiveData<String>) {
        val blackBoxCore = BlackBoxCore.get()
        val installResult = if (URLUtil.isValidUrl(source)) {
            val uri = Uri.parse(source)
            blackBoxCore.installPackageAsUser(uri, userId)
        } else {
            blackBoxCore.installPackageAsUser(source, userId)
        }

        if (installResult.success) {
            updateAppSortList(userId, installResult.packageName, true)
            resultLiveData.postValue(getString(R.string.install_success))
        } else {
            resultLiveData.postValue(getString(R.string.install_fail, installResult.msg))
        }
        scanUser()
    }

    /**
     * Uninstalls an application from the virtual environment for the specified user.
     * Updates the app sort list and cleans up empty users afterward.
     *
     * @param packageName The package name of the application to uninstall.
     * @param userID The virtual user ID to uninstall from.
     * @param resultLiveData LiveData to receive a human-readable result message.
     */
    fun unInstall(packageName: String, userID: Int, resultLiveData: MutableLiveData<String>) {
        BlackBoxCore.get().uninstallPackageAsUser(packageName, userID)
        updateAppSortList(userID, packageName, false)
        scanUser()
        resultLiveData.postValue(getString(R.string.uninstall_success))
    }


    /**
     * Launches an application within the virtual environment.
     *
     * @param packageName The package name of the application to launch.
     * @param userId The virtual user ID to launch the app for.
     * @param launchLiveData LiveData to receive whether the launch was successful.
     */
    fun launchApk(packageName: String, userId: Int, launchLiveData: MutableLiveData<Boolean>) {
        val result = BlackBoxCore.get().launchApk(packageName, userId)
        launchLiveData.postValue(result)
    }


    /**
     * Clears all data for an application within the virtual environment.
     *
     * @param packageName The package name of the application whose data should be cleared.
     * @param userID The virtual user ID.
     * @param resultLiveData LiveData to receive a human-readable result message.
     */
    fun clearApkData(packageName: String, userID: Int, resultLiveData: MutableLiveData<String>) {
        BlackBoxCore.get().clearPackage(packageName, userID)
        resultLiveData.postValue(getString(R.string.clear_success))
    }

    /**
     * Recursively scans virtual users in reverse order and removes any users that have
     * no installed applications. When a user is deleted, their remark and app sort list
     * are also cleaned up from SharedPreferences.
     */
    private fun scanUser() {
        val blackBoxCore = BlackBoxCore.get()
        val userList = blackBoxCore.users

        if (userList.isEmpty()) {
            return
        }

        val id = userList.last().id

        if (blackBoxCore.getInstalledApplications(0, id).isEmpty()) {
            blackBoxCore.deleteUser(id)
            AppManager.mRemarkSharedPreferences.edit {
                remove("Remark$id")
                remove("AppList$id")
            }
            scanUser()
        }
    }


    /**
     * Updates the persisted app sort order list for a user by adding or removing a package.
     *
     * @param userID The virtual user ID whose sort list should be updated.
     * @param pkg The package name to add or remove.
     * @param isAdd true to add the package to the list, false to remove it.
     */
    private fun updateAppSortList(userID: Int, pkg: String, isAdd: Boolean) {

        val savedSortList =
            AppManager.mRemarkSharedPreferences.getString("AppList$userID", "")

        val sortList = linkedSetOf<String>()
        if (savedSortList != null) {
            sortList.addAll(savedSortList.split(","))
        }

        if (isAdd) {
            sortList.add(pkg)
        } else {
            sortList.remove(pkg)
        }

        AppManager.mRemarkSharedPreferences.edit {
            putString("AppList$userID", sortList.joinToString(","))
        }

    }

    /**
     * Persists the current app display order for a virtual user by saving
     * the ordered list of package names to SharedPreferences.
     *
     * @param userID The virtual user ID whose app order should be saved.
     * @param dataList The list of [AppInfo] in the desired display order.
     */
    fun updateApkOrder(userID: Int, dataList: List<AppInfo>) {
        AppManager.mRemarkSharedPreferences.edit {
            putString("AppList$userID",
                dataList.joinToString(",") { it.packageName })
        }

    }

}
