package top.niunaijun.blackbox.core.system.pm.installer;

import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.pm.BPackageSettings;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.utils.FileUtils;

/**
 * Executor that creates the application directory structure for a newly installed package.
 * <p>
 * Deletes any pre-existing app directory for the package, then creates fresh directories
 * for the application root and native libraries. This is typically the first step in the
 * installation pipeline, ensuring a clean directory layout before files are copied.
 */
public class CreatePackageExecutor implements Executor {

    /**
     * Executes the directory creation step of package installation.
     *
     * @param ps     the package settings identifying the package to create directories for
     * @param option the installation options (unused in this executor)
     * @param userId the virtual user ID (unused in this executor)
     * @return always returns 0 (success)
     */
    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        FileUtils.deleteDir(BEnvironment.getAppDir(ps.pkg.packageName));

        // create app dir
        FileUtils.mkdirs(BEnvironment.getAppDir(ps.pkg.packageName));
        FileUtils.mkdirs(BEnvironment.getAppLibDir(ps.pkg.packageName));
        return 0;
    }
}
