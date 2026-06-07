package top.niunaijun.blackboxa.view.gms

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackboxa.bean.GmsBean
import top.niunaijun.blackboxa.bean.GmsInstallBean
import top.niunaijun.blackboxa.data.GmsRepository
import top.niunaijun.blackboxa.view.base.BaseViewModel

/**
 * ViewModel for managing Google Mobile Services (GMS) installation across virtual users.
 *
 * Handles loading the list of virtual users with their GMS status, and performing
 * install/uninstall operations. Delegates all work to [GmsRepository] via coroutine
 * launches on the IO dispatcher.
 *
 * @property mRepo the repository providing GMS data operations.
 */
class GmsViewModel(private val mRepo: GmsRepository) : BaseViewModel() {

    /** LiveData holding the list of virtual users with their [GmsBean] GMS status. */
    val mInstalledLiveData = MutableLiveData<List<GmsBean>>()

    /** LiveData holding the result of a GMS install or uninstall operation as [GmsInstallBean]. */
    val mUpdateInstalledLiveData = MutableLiveData<GmsInstallBean>()

    /**
     * Loads the list of all virtual users with their GMS installation status.
     */
    fun getInstalledUser() {
        launchOnUI {
            mRepo.getGmsInstalledList(mInstalledLiveData)
        }
    }

    /**
     * Installs Google Mobile Services for the specified virtual user.
     *
     * @param userID the virtual user ID to install GMS for.
     */
    fun installGms(userID: Int) {
        launchOnUI {
            mRepo.installGms(userID,mUpdateInstalledLiveData)
        }
    }

    /**
     * Uninstalls Google Mobile Services from the specified virtual user.
     *
     * @param userID the virtual user ID to uninstall GMS from.
     */
    fun uninstallGms(userID: Int) {
        launchOnUI {
            mRepo.uninstallGms(userID,mUpdateInstalledLiveData)
        }
    }
}