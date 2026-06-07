package top.niunaijun.blackboxa.view.list

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackboxa.bean.InstalledAppBean
import top.niunaijun.blackboxa.data.AppsRepository
import top.niunaijun.blackboxa.view.base.BaseViewModel

/**
 * ViewModel for loading and displaying lists of installed applications or Xposed modules.
 *
 * Handles fetching the list of installed apps for a given virtual user, fetching installed
 * Xposed modules, and triggering preview/cache updates. Delegates all work to [AppsRepository]
 * via coroutine launches on the IO dispatcher.
 *
 * @property repo the repository providing app listing data operations.
 */
class ListViewModel(private val repo: AppsRepository) : BaseViewModel() {

    /** LiveData holding the list of [InstalledAppBean] items to display. */
    val appsLiveData = MutableLiveData<List<InstalledAppBean>>()

    /** LiveData indicating whether a loading operation is in progress. */
    val loadingLiveData = MutableLiveData<Boolean>()

    /**
     * Triggers a preview/cache update of the installed app list in the repository.
     */
    fun previewInstalledList() {
        launchOnUI{
            repo.previewInstallList()
        }
    }

    /**
     * Loads the list of installed applications for the specified virtual user.
     *
     * @param userID the virtual user ID to query installed apps for.
     */
    fun getInstallAppList(userID:Int){
        launchOnUI {
            repo.getInstalledAppList(userID,loadingLiveData,appsLiveData)
        }
    }

    /**
     * Loads the list of installed Xposed modules across all virtual users.
     */
    fun getInstalledModules() {
        launchOnUI {
            repo.getInstalledModuleList(loadingLiveData, appsLiveData)
        }
    }

}