package top.niunaijun.blackbox.core.system.pm.installer;

import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.pm.BPackageSettings;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.utils.FileUtils;

/**
 * Executor that creates per-user data directories for a package within the virtual environment.
 * <p>
 * Sets up the full Android-standard data directory structure for a given user, including
 * the main data directory, cache, files, databases, and device-encrypted data directories.
 * Cleans up any pre-existing data-lib symlink directory before creating new directories.
 */
public class CreateUserExecutor implements Executor {

    /**
     * Executes the per-user directory creation step.
     *
     * @param ps     the package settings identifying the package
     * @param option the installation options (unused in this executor)
     * @param userId the virtual user ID to create directories for
     * @return always returns 0 (success)
     */
    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        String packageName = ps.pkg.packageName;
        FileUtils.deleteDir(BEnvironment.getDataLibDir(packageName, userId));

        // create user dir
        FileUtils.mkdirs(BEnvironment.getDataDir(packageName, userId));
        FileUtils.mkdirs(BEnvironment.getDataCacheDir(packageName, userId));
        FileUtils.mkdirs(BEnvironment.getDataFilesDir(packageName, userId));
        FileUtils.mkdirs(BEnvironment.getDataDatabasesDir(packageName, userId));
        FileUtils.mkdirs(BEnvironment.getDeDataDir(packageName, userId));

//        try {
//            // /data/data/xx/lib -> /data/app/xx/lib
//            FileUtils.createSymlink(BEnvironment.getAppLibDir(ps.pkg.packageName).getAbsolutePath(), BEnvironment.getDataLibDir(packageName, userId).getAbsolutePath());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return -1;
//        }
        return 0;
    }
}
