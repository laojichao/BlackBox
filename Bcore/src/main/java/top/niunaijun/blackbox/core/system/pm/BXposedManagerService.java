package top.niunaijun.blackbox.core.system.pm;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;

import androidx.core.util.AtomicFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.ISystemService;
import top.niunaijun.blackbox.core.system.user.BUserHandle;
import top.niunaijun.blackbox.entity.pm.InstalledModule;
import top.niunaijun.blackbox.entity.pm.XposedConfig;
import top.niunaijun.blackbox.utils.CloseUtils;
import top.niunaijun.blackbox.utils.FileUtils;
import top.niunaijun.blackbox.utils.compat.XposedParserCompat;

/**
 * System service managing Xposed module installation, activation, and state
 * within the virtual environment.
 *
 * <p>Tracks which Xposed modules are installed in the dedicated
 * {@link BUserHandle#USER_XPOSED} user, maintains per-module enable/disable
 * state in a persisted {@link XposedConfig}, and listens for package events
 * via {@link PackageMonitor} to keep the module cache in sync.</p>
 *
 * <p>Module state is persisted atomically to {@link BEnvironment#getXPModuleConf()}
 * using a {@link Parcel}-based format through {@link AtomicFile}.</p>
 *
 * @see BPackageManagerService
 * @see XposedConfig
 * @see InstalledModule
 */
public class BXposedManagerService extends IBXposedManagerService.Stub implements ISystemService, PackageMonitor {
    private static final BXposedManagerService sService = new BXposedManagerService();

    private XposedConfig mXposedConfig;
    private final Object mLock = new Object();
    private BPackageManagerService mPms;
    private final Map<String, InstalledModule> mCacheModule = new HashMap<>();

    /**
     * Returns the singleton instance of this service.
     *
     * @return the global BXposedManagerService instance
     */
    public static BXposedManagerService get() {
        return sService;
    }

    /**
     * Constructs the Xposed manager service. Module state is loaded during {@link #systemReady()}.
     */
    public BXposedManagerService() {
    }

    /**
     * Called when the system is fully initialized. Loads persisted module state
     * from disk and registers this service as a package event monitor.
     */
    @Override
    public void systemReady() {
        loadModuleStateLr();
        mPms = BPackageManagerService.get();
        mPms.addPackageMonitor(this);
    }

    /**
     * Returns whether the Xposed framework is globally enabled.
     *
     * @return true if Xposed is enabled, false otherwise
     */
    @Override
    public boolean isXPEnable() {
        synchronized (mLock) {
            return mXposedConfig.enable;
        }
    }

    /**
     * Enables or disables the Xposed framework globally. Persists the change to disk.
     *
     * @param enable true to enable, false to disable
     */
    @Override
    public void setXPEnable(boolean enable) {
        synchronized (mLock) {
            mXposedConfig.enable = enable;
            saveModuleStateLw();
        }
    }

    /**
     * Returns whether a specific Xposed module is enabled.
     *
     * @param packageName the package name of the Xposed module
     * @return true if the module is installed and enabled, false otherwise
     */
    @Override
    public boolean isModuleEnable(String packageName) {
        synchronized (mLock) {
            Boolean enable = mXposedConfig.moduleState.get(packageName);
            return enable != null && enable;
        }
    }

    /**
     * Enables or disables a specific Xposed module. The module must be installed
     * in the Xposed user ({@link BUserHandle#USER_XPOSED}). Persists the change to disk.
     *
     * @param packageName the package name of the Xposed module
     * @param enable      true to enable, false to disable
     */
    @Override
    public void setModuleEnable(String packageName, boolean enable) {
        synchronized (mLock) {
            if (!mPms.isInstalled(packageName, BUserHandle.USER_XPOSED)) {
                return;
            }
            mXposedConfig.moduleState.put(packageName, enable);
            saveModuleStateLw();
        }
    }

    /**
     * Returns all installed Xposed modules with their current enable state.
     * Newly discovered modules are parsed from application metadata and cached.
     *
     * @return a list of InstalledModule entries representing all known Xposed modules
     */
    @Override
    public List<InstalledModule> getInstalledModules() {
        List<ApplicationInfo> installedApplications = mPms.getInstalledApplications(PackageManager.GET_META_DATA, BUserHandle.USER_XPOSED);
        synchronized (mCacheModule) {
            for (ApplicationInfo installedApplication : installedApplications) {
                if (mCacheModule.containsKey(installedApplication.packageName))
                    continue;
                InstalledModule installedModule = XposedParserCompat.parseModule(installedApplication);
                if (installedModule != null) {
                    mCacheModule.put(installedApplication.packageName, installedModule);
                }
            }
            ArrayList<InstalledModule> installedModules = new ArrayList<>(mCacheModule.values());
            for (InstalledModule installedModule : installedModules) {
                installedModule.enable = isModuleEnable(installedModule.packageName);
            }
            return installedModules;
        }
    }

    private void loadModuleStateLr() {
        File xpModuleConf = BEnvironment.getXPModuleConf();
        if (!xpModuleConf.exists()) {
            mXposedConfig = new XposedConfig();
            saveModuleStateLw();
            return;
        }
        Parcel parcel = null;
        try {
            parcel = FileUtils.readToParcel(xpModuleConf);
            mXposedConfig = new XposedConfig(parcel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (parcel != null) {
                parcel.recycle();
            }
        }
    }

    private void saveModuleStateLw() {
        Parcel parcel = Parcel.obtain();
        AtomicFile atomicFile = new AtomicFile(BEnvironment.getXPModuleConf());
        FileOutputStream fileOutputStream = null;
        try {
            mXposedConfig.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            fileOutputStream = atomicFile.startWrite();
            FileUtils.writeParcelToOutput(parcel, fileOutputStream);
            atomicFile.finishWrite(fileOutputStream);
        } catch (Exception ignored) {
            atomicFile.failWrite(fileOutputStream);
        } finally {
            parcel.recycle();
            CloseUtils.close(fileOutputStream);
        }
    }

    /**
     * Called when a package is uninstalled. Clears the module cache and removes
     * the module's enable state if the event is for the Xposed user or all users.
     *
     * @param packageName the name of the uninstalled package
     * @param removeApp   true if the application files were also removed
     * @param userId      the virtual user ID the uninstall occurred for
     */
    @Override
    public void onPackageUninstalled(String packageName, boolean removeApp, int userId) {
        if (userId != BUserHandle.USER_XPOSED && userId != BUserHandle.USER_ALL) {
            return;
        }
        synchronized (mCacheModule) {
            mCacheModule.remove(packageName);
        }
        synchronized (mLock) {
            mXposedConfig.moduleState.remove(packageName);
            saveModuleStateLw();
        }
    }

    /**
     * Called when a package is installed. Invalidates the module cache entry and
     * registers the new module as disabled by default if the event is for the Xposed user.
     *
     * @param packageName the name of the installed package
     * @param userId      the virtual user ID the install occurred for
     */
    @Override
    public void onPackageInstalled(String packageName, int userId) {
        if (userId != BUserHandle.USER_XPOSED && userId != BUserHandle.USER_ALL) {
            return;
        }
        synchronized (mCacheModule) {
            mCacheModule.remove(packageName);
        }
        synchronized (mLock) {
            mXposedConfig.moduleState.put(packageName, false);
            saveModuleStateLw();
        }
    }
}
