package top.niunaijun.blackbox.core.system.pm.installer;

import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.pm.BPackageSettings;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.utils.FileUtils;

/**
 * Executor that removes an application's directory structure from the virtual environment.
 * <p>
 * Deletes the entire app directory (including APK, native libraries, and any cached data)
 * for the specified package. Used during full package uninstallation.
 */
public class RemoveAppExecutor implements Executor {

    /**
     * Executes the application directory removal step of package uninstallation.
     *
     * @param ps     the package settings identifying the package to remove
     * @param option the installation options (unused in this executor)
     * @param userId the virtual user ID (unused in this executor)
     * @return always returns 0 (success)
     */
    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        FileUtils.deleteDir(BEnvironment.getAppDir(ps.pkg.packageName));
        return 0;
    }
}
