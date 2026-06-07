package top.niunaijun.blackbox.core.system.pm;

import java.util.ArrayList;
import java.util.List;

import top.niunaijun.blackbox.core.system.ISystemService;
import top.niunaijun.blackbox.core.system.pm.installer.CopyExecutor;
import top.niunaijun.blackbox.core.system.pm.installer.CreatePackageExecutor;
import top.niunaijun.blackbox.core.system.pm.installer.CreateUserExecutor;
import top.niunaijun.blackbox.core.system.pm.installer.Executor;
import top.niunaijun.blackbox.core.system.pm.installer.RemoveAppExecutor;
import top.niunaijun.blackbox.core.system.pm.installer.RemoveUserExecutor;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.utils.Slog;

/**
 * System service responsible for executing package installation, uninstallation,
 * and data-clearing operations within the virtual environment.
 *
 * <p>This service acts as the low-level executor that delegates to a chain of
 * {@link Executor} steps (user creation, package directory setup, file copying,
 * and cleanup). Higher-level services such as {@link BPackageManagerService}
 * call into this service after parsing and validation are complete.</p>
 *
 * @see BPackageManagerService
 * @see Executor
 */
public class BPackageInstallerService extends IBPackageInstallerService.Stub implements ISystemService {
    private static final BPackageInstallerService sService = new BPackageInstallerService();

    /**
     * Returns the singleton instance of this service.
     *
     * @return the global BPackageInstallerService instance
     */
    public static BPackageInstallerService get() {
        return sService;
    }

    public static final String TAG = "BPackageInstallerService";

    /**
     * Installs a package for a specific virtual user by executing a chained sequence
     * of executors: user environment creation, package directory creation, and file copying.
     *
     * @param ps     the package settings describing what to install
     * @param userId the virtual user ID to install for
     * @return 0 on success, or a negative error code from the failing executor
     */
    @Override
    public int installPackageAsUser(BPackageSettings ps, int userId) {
        List<Executor> executors = new ArrayList<>();
        // 创建用户环境相关操作
        executors.add(new CreateUserExecutor());
        // 创建应用环境相关操作
        executors.add(new CreatePackageExecutor());
        // 拷贝应用相关文件
        executors.add(new CopyExecutor());
        InstallOption option = ps.installOption;
        for (Executor executor : executors) {
            int exec = executor.exec(ps, option, userId);
            Slog.d(TAG, "installPackageAsUser: " + executor.getClass().getSimpleName() + " exec: " + exec);
            if (exec != 0) {
                return exec;
            }
        }
        return 0;
    }

    /**
     * Uninstalls a package for a specific virtual user. Optionally removes the
     * shared application files when {@code removeApp} is true (i.e., the last user
     * is being removed). Always removes user-specific data directories.
     *
     * @param ps       the package settings describing what to uninstall
     * @param removeApp if true, also removes the shared application directory
     * @param userId   the virtual user ID to uninstall for
     * @return 0 on success, or a negative error code from the failing executor
     */
    @Override
    public int uninstallPackageAsUser(BPackageSettings ps, boolean removeApp, int userId) {
        List<Executor> executors = new ArrayList<>();
        if (removeApp) {
            // 移除App
            executors.add(new RemoveAppExecutor());
        }
        // 移除用户相关目录
        executors.add(new RemoveUserExecutor());
        InstallOption option = ps.installOption;
        for (Executor executor : executors) {
            int exec = executor.exec(ps, option, userId);
            Slog.d(TAG, "uninstallPackageAsUser: " + executor.getClass().getSimpleName() + " exec: " + exec);
            if (exec != 0) {
                return exec;
            }
        }
        return 0;
    }

    /**
     * Clears user-specific package data by removing the user directory and
     * recreating a fresh user environment, effectively resetting the app's
     * data for the specified user without uninstalling the package.
     *
     * @param ps     the package settings whose data should be cleared
     * @param userId the virtual user ID whose data to clear
     * @return 0 on success, or a negative error code from the failing executor
     */
    @Override
    public int clearPackage(BPackageSettings ps, int userId) {
        List<Executor> executors = new ArrayList<>();
        // 移除用户相关目录
        executors.add(new RemoveUserExecutor());
        // 创建用户环境相关操作
        executors.add(new CreateUserExecutor());
        InstallOption option = ps.installOption;
        for (Executor executor : executors) {
            int exec = executor.exec(ps, option, userId);
            Slog.d(TAG, "uninstallPackageAsUser: " + executor.getClass().getSimpleName() + " exec: " + exec);
            if (exec != 0) {
                return exec;
            }
        }
        return 0;
    }

    /**
     * Updates an existing package by re-creating its package environment and
     * copying updated files. Does not modify per-user state.
     *
     * @param ps the package settings describing the package to update
     * @return 0 on success, or a negative error code from the failing executor
     */
    @Override
    public int updatePackage(BPackageSettings ps) {
        List<Executor> executors = new ArrayList<>();
        executors.add(new CreatePackageExecutor());
        executors.add(new CopyExecutor());
        InstallOption option = ps.installOption;
        for (Executor executor : executors) {
            int exec = executor.exec(ps, option, -1);
            if (exec != 0) {
                return exec;
            }
        }
        return 0;
    }

    /**
     * Called when the system is fully initialized. This service has no
     * startup-time work to perform.
     */
    @Override
    public void systemReady() {

    }
}
