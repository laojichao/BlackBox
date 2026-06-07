package top.niunaijun.blackbox.core.system.pm.installer;

import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.pm.BPackageSettings;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.utils.FileUtils;

/**
 * Executor that removes per-user data directories for a package within the virtual environment.
 * <p>
 * Deletes the user-specific data directory, device-encrypted data directory, and external
 * data directory for the given package and user. Used when uninstalling a package for a
 * specific user or cleaning up user data during full removal.
 */
public class RemoveUserExecutor implements Executor {

    /**
     * Executes the per-user directory removal step.
     *
     * @param ps     the package settings identifying the package
     * @param option the installation options (unused in this executor)
     * @param userId the virtual user ID whose data directories will be deleted
     * @return always returns 0 (success)
     */
    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        String packageName = ps.pkg.packageName;
        // delete user dir
        FileUtils.deleteDir(BEnvironment.getDataDir(packageName, userId));
        FileUtils.deleteDir(BEnvironment.getDeDataDir(packageName, userId));
        FileUtils.deleteDir(BEnvironment.getExternalDataDir(packageName, userId));
        return 0;
    }
}
