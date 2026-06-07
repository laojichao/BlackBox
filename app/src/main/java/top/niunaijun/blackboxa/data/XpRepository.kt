package top.niunaijun.blackboxa.data

import android.net.Uri
import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.BlackBoxCore.getPackageManager
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.XpModuleInfo
import top.niunaijun.blackboxa.util.getString

/**
 * Repository for managing Xposed modules within the BlackBox virtual environment.
 *
 * Provides operations to list, install, and uninstall Xposed modules.
 * Supports both file path and URL sources for module installation.
 */
class XpRepository {
    /**
     * Retrieves all installed Xposed modules and posts them to [modulesLiveData].
     *
     * @param modulesLiveData LiveData to receive the list of [XpModuleInfo] results.
     */
    fun getInstallModules(modulesLiveData: MutableLiveData<List<XpModuleInfo>>) {
        val moduleList = BlackBoxCore.get().installedXPModules
        val result = mutableListOf<XpModuleInfo>()
        moduleList.forEach {
            val info = XpModuleInfo(
                    it.name,
                    it.desc,
                    it.packageName,
                    it.packageInfo.versionName,
                    it.enable,
                    it.application.loadIcon(getPackageManager())
            )
            result.add(info)
        }

        modulesLiveData.postValue(result)
    }

    /**
     * Installs an Xposed module from the given source. Supports both file path
     * and URL sources. Posts a success or failure message to [resultLiveData].
     *
     * @param source The module source, either a local file path or a URL.
     * @param resultLiveData LiveData to receive a human-readable result message.
     */
    fun installModule(source: String, resultLiveData: MutableLiveData<String>) {
        val blackBoxCore = BlackBoxCore.get()

        val installResult = if (URLUtil.isValidUrl(source)) {
            val uri = Uri.parse(source)
            blackBoxCore.installXPModule(uri)
        } else {
            //source == packageName
            blackBoxCore.installXPModule(source)
        }

        if(installResult.success){
            resultLiveData.postValue(getString(R.string.install_success))
        }else{
            resultLiveData.postValue(getString(R.string.install_fail, installResult.msg))
        }
    }

    /**
     * Uninstalls an Xposed module by its package name.
     * Posts a success message to [resultLiveData].
     *
     * @param packageName The package name of the Xposed module to uninstall.
     * @param resultLiveData LiveData to receive a human-readable result message.
     */
    fun unInstallModule(packageName: String, resultLiveData: MutableLiveData<String>) {
        BlackBoxCore.get().uninstallXPModule(packageName)
        resultLiveData.postValue(getString(R.string.remove_success))
    }
}