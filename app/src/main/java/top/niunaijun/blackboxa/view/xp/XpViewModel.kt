package top.niunaijun.blackboxa.view.xp

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackboxa.bean.XpModuleInfo
import top.niunaijun.blackboxa.data.XpRepository
import top.niunaijun.blackboxa.view.base.BaseViewModel

/**
 * ViewModel for managing Xposed module operations including listing, installing, and uninstalling modules.
 *
 * Delegates all repository operations to [XpRepository] and exposes results via LiveData observables.
 *
 * @param repo the Xposed module repository for data operations
 */
class XpViewModel(private val repo:XpRepository):BaseViewModel() {

    /** LiveData holding the list of currently installed Xposed modules. */
    val appsLiveData = MutableLiveData<List<XpModuleInfo>>()

    /** LiveData holding result messages from install/uninstall operations. */
    val resultLiveData = MutableLiveData<String>()

    /**
     * Loads the list of installed Xposed modules into [appsLiveData].
     */
    fun getInstalledModule() {
        launchOnUI {
            repo.getInstallModules(appsLiveData)
        }
    }

    /**
     * Installs an Xposed module from the given APK source path.
     *
     * @param source the file path of the module APK to install
     */
    fun installModule(source:String) {
        launchOnUI {
            repo.installModule(source,resultLiveData)
        }
    }

    /**
     * Uninstalls the Xposed module identified by the given package name.
     *
     * @param packageName the package name of the module to uninstall
     */
    fun unInstallModule(packageName: String){
        launchOnUI {
            repo.unInstallModule(packageName,resultLiveData)
        }
    }
}