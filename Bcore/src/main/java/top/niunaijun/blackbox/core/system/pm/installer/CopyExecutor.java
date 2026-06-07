package top.niunaijun.blackbox.core.system.pm.installer;


import java.io.File;
import java.io.IOException;

import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.pm.BPackageSettings;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.utils.FileUtils;
import top.niunaijun.blackbox.utils.NativeUtils;

/**
 * Executor that handles file operations during package installation.
 * <p>
 * For non-system packages, copies native libraries (.so files) from the APK to the
 * app's native lib directory. For storage-flagged installations, copies or renames
 * the APK file to the app's base directory and updates the package's baseCodePath.
 * System installations skip file copying since the APK remains in its original location.
 */
public class CopyExecutor implements Executor {

    /**
     * Executes the file copy step of package installation.
     *
     * @param ps     the package settings containing the APK source path
     * @param option the installation flags controlling copy behavior
     * @param userId the virtual user ID (unused in this executor)
     * @return 0 on success, -1 on failure
     */
    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        try {
            if (!option.isFlag(InstallOption.FLAG_SYSTEM)) {
                NativeUtils.copyNativeLib(new File(ps.pkg.baseCodePath), BEnvironment.getAppLibDir(ps.pkg.packageName));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        if (option.isFlag(InstallOption.FLAG_STORAGE)) {
            // 外部安装
            File origFile = new File(ps.pkg.baseCodePath);
            File newFile = BEnvironment.getBaseApkDir(ps.pkg.packageName);
            try {
                if (option.isFlag(InstallOption.FLAG_URI_FILE)) {
                    boolean b = FileUtils.renameTo(origFile, newFile);
                    if (!b) {
                        FileUtils.copyFile(origFile, newFile);
                    }
                } else {
                    FileUtils.copyFile(origFile, newFile);
                }
                // update baseCodePath
                ps.pkg.baseCodePath = newFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        } else if (option.isFlag(InstallOption.FLAG_SYSTEM)) {
            // 系统安装
        }
        return 0;
    }
}
