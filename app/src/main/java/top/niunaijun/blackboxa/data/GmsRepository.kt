package top.niunaijun.blackboxa.data

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.app.AppManager
import top.niunaijun.blackboxa.bean.GmsBean
import top.niunaijun.blackboxa.bean.GmsInstallBean
import top.niunaijun.blackboxa.util.getString

/**
 * Repository for managing Google Mobile Services (GMS) installation status
 * across virtual users in the BlackBox environment.
 *
 * Provides operations to query, install, and uninstall GMS for individual virtual users.
 */
class GmsRepository {

    /**
     * Retrieves the GMS installation status for all virtual users and posts
     * the result to [mInstalledLiveData].
     *
     * @param mInstalledLiveData LiveData to receive the list of [GmsBean] results.
     */
    fun getGmsInstalledList(mInstalledLiveData: MutableLiveData<List<GmsBean>>) {
        val userList = arrayListOf<GmsBean>()

        BlackBoxCore.get().users.forEach {
            val userId = it.id
            val userName =
                AppManager.mRemarkSharedPreferences.getString("Remark$userId", "User $userId") ?: ""
            val isInstalled = BlackBoxCore.get().isInstallGms(userId)
            val bean = GmsBean(userId, userName, isInstalled)
            userList.add(bean)
        }

        mInstalledLiveData.postValue(userList)
    }

    /**
     * Installs Google Mobile Services for the specified virtual user.
     * Posts a [GmsInstallBean] with the operation result to [mUpdateInstalledLiveData].
     *
     * @param userID The virtual user ID to install GMS for.
     * @param mUpdateInstalledLiveData LiveData to receive the [GmsInstallBean] result.
     */
    fun installGms(
        userID: Int,
        mUpdateInstalledLiveData: MutableLiveData<GmsInstallBean>
    ) {
        val installResult = BlackBoxCore.get().installGms(userID)

        val result = if (installResult.success) {
            getString(R.string.install_success)
        } else {
            getString(R.string.install_fail, installResult.msg)
        }

        val bean = GmsInstallBean(userID,installResult.success,result)
        mUpdateInstalledLiveData.postValue(bean)
    }

    /**
     * Uninstalls Google Mobile Services from the specified virtual user.
     * Only attempts uninstall if GMS is currently installed for the user.
     * Posts a [GmsInstallBean] with the operation result to [mUpdateInstalledLiveData].
     *
     * @param userID The virtual user ID to uninstall GMS from.
     * @param mUpdateInstalledLiveData LiveData to receive the [GmsInstallBean] result.
     */
    fun uninstallGms(
        userID: Int,
        mUpdateInstalledLiveData: MutableLiveData<GmsInstallBean>
    ) {
        var isSuccess = false
        if (BlackBoxCore.get().isInstallGms(userID)) {
            isSuccess = BlackBoxCore.get().uninstallGms(userID)
        }

        val result = if (isSuccess) {
            getString(R.string.uninstall_success)
        } else {
            getString(R.string.uninstall_fail)
        }

        val bean = GmsInstallBean(userID,isSuccess,result)

        mUpdateInstalledLiveData.postValue(bean)
    }
}