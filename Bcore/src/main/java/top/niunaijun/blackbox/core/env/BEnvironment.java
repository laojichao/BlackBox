package top.niunaijun.blackbox.core.env;

import java.io.File;
import java.util.Locale;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.utils.FileUtils;

/**
 * Central directory and file-path resolver for the BlackBox virtual engine.
 *
 * <p>All virtual environment data -- user directories, application data,
 * cache, system configuration files, and proc entries -- is stored
 * under a single virtual root inside the host app's private storage.
 * This class provides static accessor methods for every well-known
 * path used by the engine.</p>
 *
 * <p>Typical virtual root: {@code /data/data/<hostPkg>/cache/../blackbox/}</p>
 */
public class BEnvironment {
    private static final File sVirtualRoot = new File(BlackBoxCore.getContext().getCacheDir().getParent(), "blackbox");
    private static final File sExternalVirtualRoot = BlackBoxCore.getContext().getExternalFilesDir("blackbox");

    /** Jar used for unit-test instrumentation. */
    public static File JUNIT_JAR = new File(getCacheDir(), "junit.apk");
    /** Empty jar used as a placeholder when no real dex is needed. */
    public static File EMPTY_JAR = new File(getCacheDir(), "empty.apk");

    /**
     * Creates all mandatory directories under the virtual root.
     * Must be called once during engine startup before any other
     * directory accessor is used.
     */
    public static void load() {
        FileUtils.mkdirs(sVirtualRoot);
        FileUtils.mkdirs(sExternalVirtualRoot);
        FileUtils.mkdirs(getSystemDir());
        FileUtils.mkdirs(getCacheDir());
        FileUtils.mkdirs(getProcDir());
    }

    /**
     * Returns the top-level virtual root directory.
     *
     * @return the virtual root {@link File}
     */
    public static File getVirtualRoot() {
        return sVirtualRoot;
    }

    /**
     * Returns the external (sdcard-backed) virtual root directory.
     *
     * @return the external virtual root {@link File}
     */
    public static File getExternalVirtualRoot() {
        return sExternalVirtualRoot;
    }

    /**
     * Returns the system configuration directory ({@code <root>/system}).
     *
     * @return the system directory
     */
    public static File getSystemDir() {
        return new File(sVirtualRoot, "system");
    }

    /**
     * Returns the virtual {@code /proc} directory ({@code <root>/proc}).
     *
     * @return the proc directory
     */
    public static File getProcDir() {
        return new File(sVirtualRoot, "proc");
    }

    /**
     * Returns the cache directory ({@code <root>/cache}).
     *
     * @return the cache directory
     */
    public static File getCacheDir() {
        return new File(sVirtualRoot, "cache");
    }

    /**
     * Returns the user information configuration file.
     *
     * @return path to {@code user.conf}
     */
    public static File getUserInfoConf() {
        return new File(getSystemDir(), "user.conf");
    }

    /**
     * Returns the accounts configuration file.
     *
     * @return path to {@code accounts.conf}
     */
    public static File getAccountsConf() {
        return new File(getSystemDir(), "accounts.conf");
    }

    /**
     * Returns the UID mapping configuration file.
     *
     * @return path to {@code uid.conf}
     */
    public static File getUidConf() {
        return new File(getSystemDir(), "uid.conf");
    }

    /**
     * Returns the shared-user mapping configuration file.
     *
     * @return path to {@code shared-user.conf}
     */
    public static File getSharedUserConf() {
        return new File(getSystemDir(), "shared-user.conf");
    }

    /**
     * Returns the Xposed module configuration file.
     *
     * @return path to {@code xposed-module.conf}
     */
    public static File getXPModuleConf() {
        return new File(getSystemDir(), "xposed-module.conf");
    }

    /**
     * Returns the fake-location configuration file.
     *
     * @return path to {@code fake-location.conf}
     */
    public static File getFakeLocationConf() {
        return new File(getSystemDir(), "fake-location.conf");
    }

    /**
     * Returns the per-package configuration file.
     *
     * @param packageName the application package name
     * @return path to {@code package.conf} inside the app's directory
     */
    public static File getPackageConf(String packageName) {
        return new File(getAppDir(packageName), "package.conf");
    }

    /**
     * Returns the external (sdcard) storage directory for the given
     * virtual user, mirroring {@code /storage/emulated/<userId>}.
     *
     * @param userId virtual user id
     * @return external storage directory for the user
     */
    public static File getExternalUserDir(int userId) {
        return new File(sExternalVirtualRoot, String.format(Locale.CHINA, "storage/emulated/%d/", userId));
    }

    /**
     * Returns the user data root directory, mirroring
     * {@code /data/user/<userId>}.
     *
     * @param userId virtual user id
     * @return the user data directory
     */
    public static File getUserDir(int userId) {
        return new File(sVirtualRoot, String.format(Locale.CHINA, "data/user/%d", userId));
    }

    /**
     * Returns the device-encrypted data directory for a package, mirroring
     * {@code /data/user_de/<userId>/<packageName>}.
     *
     * @param packageName the application package name
     * @param userId      virtual user id
     * @return the device-encrypted data directory
     */
    public static File getDeDataDir(String packageName, int userId) {
        return new File(sVirtualRoot, String.format(Locale.CHINA, "data/user_de/%d/%s", userId, packageName));
    }

    /**
     * Returns the external data directory for a package and user,
     * mirroring {@code /storage/emulated/<userId>/Android/data/<packageName>}.
     *
     * @param packageName the application package name
     * @param userId      virtual user id
     * @return the external data directory
     */
    public static File getExternalDataDir(String packageName, int userId) {
        return new File(getExternalUserDir(userId), String.format(Locale.CHINA, "Android/data/%s", packageName));
    }


    /**
     * Returns the primary data directory for a package and user,
     * mirroring {@code /data/user/<userId>/<packageName>}.
     *
     * @param packageName the application package name
     * @param userId      virtual user id
     * @return the data directory
     */
    public static File getDataDir(String packageName, int userId) {
        return new File(sVirtualRoot, String.format(Locale.CHINA, "data/user/%d/%s", userId, packageName));
    }

    /**
     * Returns (and creates if needed) the proc directory for a specific
     * virtual process, used to spoof {@code /proc/<pid>/cmdline}.
     *
     * @param pid the virtual process id
     * @return the proc directory for that pid
     */
    public static File getProcDir(int pid) {
        File file = new File(getProcDir(), String.format(Locale.CHINA, "%d", pid));
        FileUtils.mkdirs(file);
        return file;
    }

    /**
     * Returns the external files directory for a package and user.
     *
     * @param packageName the application package name
     * @param userId      virtual user id
     * @return the external "files" directory
     */
    public static File getExternalDataFilesDir(String packageName, int userId) {
        return new File(getExternalDataDir(packageName, userId), "files");
    }

    /**
     * Returns the internal files directory for a package and user.
     *
     * @param packageName the application package name
     * @param userId      virtual user id
     * @return the "files" directory
     */
    public static File getDataFilesDir(String packageName, int userId) {
        return new File(getDataDir(packageName, userId), "files");
    }

    /**
     * Returns the external cache directory for a package and user.
     *
     * @param packageName the application package name
     * @param userId      virtual user id
     * @return the external "cache" directory
     */
    public static File getExternalDataCacheDir(String packageName, int userId) {
        return new File(getExternalDataDir(packageName, userId), "cache");
    }

    /**
     * Returns the internal cache directory for a package and user.
     *
     * @param packageName the application package name
     * @param userId      virtual user id
     * @return the "cache" directory
     */
    public static File getDataCacheDir(String packageName, int userId) {
        return new File(getDataDir(packageName, userId), "cache");
    }

    /**
     * Returns the native library directory for a package and user.
     *
     * @param packageName the application package name
     * @param userId      virtual user id
     * @return the "lib" directory
     */
    public static File getDataLibDir(String packageName, int userId) {
        return new File(getDataDir(packageName, userId), "lib");
    }

    /**
     * Returns the databases directory for a package and user.
     *
     * @param packageName the application package name
     * @param userId      virtual user id
     * @return the "databases" directory
     */
    public static File getDataDatabasesDir(String packageName, int userId) {
        return new File(getDataDir(packageName, userId), "databases");
    }

    /**
     * Returns the root of the application installation directory
     * ({@code <root>/data/app/}).
     *
     * @return the app root directory
     */
    public static File getAppRootDir() {
        return getAppDir("");
    }

    /**
     * Returns the installation directory for a specific package.
     *
     * @param packageName the application package name
     * @return the app directory
     */
    public static File getAppDir(String packageName) {
        return new File(sVirtualRoot, "data/app/" + packageName);
    }

    /**
     * Returns the path to the base APK for a package.
     *
     * @param packageName the application package name
     * @return path to {@code base.apk} in the app directory
     */
    public static File getBaseApkDir(String packageName) {
        return new File(sVirtualRoot, "data/app/" + packageName + "/base.apk");
    }

    /**
     * Returns the native library directory inside a package's install dir.
     *
     * @param packageName the application package name
     * @return the "lib" directory
     */
    public static File getAppLibDir(String packageName) {
        return new File(getAppDir(packageName), "lib");
    }

    /**
     * Returns the XSharedPreferences file for a package and preference
     * name, enabling Xposed modules to read app preferences from outside
     * the sandbox.
     *
     * @param packageName   the application package name
     * @param prefFileName  the SharedPreferences file name (without extension)
     * @return path to the shared-preferences XML file
     */
    public static File getXSharedPreferences(String packageName, String prefFileName) {
       return new File(BEnvironment.getDataDir(packageName, BActivityThread.getUserId()), "shared_prefs/" + prefFileName + ".xml");
    }
}
