package top.niunaijun.blackbox.fake.frameworks;

import android.os.RemoteException;

import java.util.Collections;
import java.util.List;

import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.pm.IBXposedManagerService;
import top.niunaijun.blackbox.entity.pm.InstalledModule;

/**
 * Client-side manager for Xposed module operations within the virtual environment.
 * Provides a facade over {@link IBXposedManagerService} for enabling/disabling Xposed
 * framework and individual modules.
 */
public class BXposedManager extends BlackManager<IBXposedManagerService> {
    private static final BXposedManager sXposedManager = new BXposedManager();

    /**
     * Returns the singleton instance of {@link BXposedManager}.
     *
     * @return the singleton BXposedManager instance
     */
    public static BXposedManager get() {
        return sXposedManager;
    }

    @Override
    protected String getServiceName() {
        return ServiceManager.XPOSED_MANAGER;
    }

    /**
     * Checks whether the Xposed framework is enabled.
     *
     * @return true if Xposed is enabled, false otherwise
     */
    public boolean isXPEnable() {
        try {
            return getService().isXPEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Enables or disables the Xposed framework.
     *
     * @param enable true to enable, false to disable
     */
    public void setXPEnable(boolean enable) {
        try {
            getService().setXPEnable(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether a specific Xposed module is enabled.
     *
     * @param packageName the package name of the module
     * @return true if the module is enabled, false otherwise
     */
    public boolean isModuleEnable(String packageName) {
        try {
            return getService().isModuleEnable(packageName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Enables or disables a specific Xposed module.
     *
     * @param packageName the package name of the module
     * @param enable      true to enable, false to disable
     */
    public void setModuleEnable(String packageName, boolean enable) {
        try {
            getService().setModuleEnable(packageName, enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of all installed Xposed modules.
     *
     * @return the list of InstalledModule, or an empty list on failure
     */
    public List<InstalledModule> getInstalledModules() {
        try {
            return getService().getInstalledModules();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
