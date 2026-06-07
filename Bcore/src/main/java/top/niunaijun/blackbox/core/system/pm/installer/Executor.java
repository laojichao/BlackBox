package top.niunaijun.blackbox.core.system.pm.installer;

import top.niunaijun.blackbox.core.system.pm.BPackageSettings;
import top.niunaijun.blackbox.entity.pm.InstallOption;

/**
 * Base interface for all package installation and uninstallation executors within the
 * virtual environment.
 * <p>
 * Each implementation performs a single step of the install/uninstall pipeline (e.g.,
 * creating directories, copying files, registering packages). Executors are chained
 * together by the package manager service to complete a full operation.
 */
public interface Executor {
    public static final String TAG = "InstallExecutor";

    /**
     * Executes this installation step for the given package.
     *
     * @param ps     the package settings being installed or uninstalled
     * @param option the installation options (system, storage, URI, etc.)
     * @param userId the virtual user ID the operation targets
     * @return 0 on success, a negative value on failure
     */
    int exec(BPackageSettings ps, InstallOption option, int userId);
}
