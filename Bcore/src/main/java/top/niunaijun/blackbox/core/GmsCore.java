package top.niunaijun.blackbox.core;

import android.content.pm.PackageManager;

import java.util.HashSet;
import java.util.Set;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.entity.pm.InstallResult;

/**
 * Manages Google Mobile Services (GMS) installation and detection inside the
 * BlackBox virtual environment.
 *
 * <p>Maintains two package sets -- core Google services (GMS, GSF, backup
 * transport, etc.) and Google apps (Play Store, Play Games, Wear OS).
 * Provides helpers to install / uninstall these packages for a given
 * virtual user and to query whether the host device ships with GMS.</p>
 */
public class GmsCore {
    private static final String TAG = "GmsCore";

    private static final HashSet<String> GOOGLE_APP = new HashSet<>();
    private static final HashSet<String> GOOGLE_SERVICE = new HashSet<>();

    /** Package name of Google Play Services. */
    public static final String GMS_PKG = "com.google.android.gms";
    /** Package name of Google Services Framework. */
    public static final String GSF_PKG = "com.google.android.gsf";
    /** Package name of the Play Store. */
    public static final String VENDING_PKG = "com.android.vending";

    static {
        GOOGLE_APP.add(VENDING_PKG);
        GOOGLE_APP.add("com.google.android.play.games");
        GOOGLE_APP.add("com.google.android.wearable.app");
        GOOGLE_APP.add("com.google.android.wearable.app.cn");

        // GMS must install at first
        GOOGLE_SERVICE.add(GMS_PKG);
        GOOGLE_SERVICE.add(GSF_PKG);
        GOOGLE_SERVICE.add("com.google.android.gsf.login");
        GOOGLE_SERVICE.add("com.google.android.backuptransport");
        GOOGLE_SERVICE.add("com.google.android.backup");
        GOOGLE_SERVICE.add("com.google.android.configupdater");
        GOOGLE_SERVICE.add("com.google.android.syncadapters.contacts");
        GOOGLE_SERVICE.add("com.google.android.feedback");
        GOOGLE_SERVICE.add("com.google.android.onetimeinitializer");
        GOOGLE_SERVICE.add("com.google.android.partnersetup");
        GOOGLE_SERVICE.add("com.google.android.setupwizard");
        GOOGLE_SERVICE.add("com.google.android.syncadapters.calendar");
    }

    /**
     * Checks whether the given package name belongs to a core Google service.
     *
     * @param packageName the package name to test
     * @return {@code true} if the package is in the Google service set
     */
    public static boolean isGoogleService(String packageName) {
        return GOOGLE_SERVICE.contains(packageName);
    }

    /**
     * Checks whether the given package name belongs to any Google app or service.
     *
     * @param str the package name to test
     * @return {@code true} if the package is in either the Google app or service set
     */
    public static boolean isGoogleAppOrService(String str) {
        return GOOGLE_APP.contains(str) || GOOGLE_SERVICE.contains(str);
    }

    /**
     * Installs the given set of packages into the virtual environment for
     * the specified user.  Packages already installed or not present on the
     * host device are silently skipped.
     *
     * @param list   set of package names to install
     * @param userId target virtual user id
     * @return {@link InstallResult} indicating success or the first failure
     */
    private static InstallResult installPackages(Set<String> list, int userId) {
        BlackBoxCore blackBoxCore = BlackBoxCore.get();
        for (String packageName : list) {
            if (blackBoxCore.isInstalled(packageName, userId)) {
                continue;
            }
            try {
                BlackBoxCore.getContext().getPackageManager().getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                // Ignore
                continue;
            }
            InstallResult installResult = blackBoxCore.installPackageAsUser(packageName, userId);
            if (!installResult.success) {
                return installResult;
            }
        }
        return new InstallResult();
    }

    /**
     * Uninstalls the given set of packages from the virtual environment
     * for the specified user.
     *
     * @param list   set of package names to uninstall
     * @param userId target virtual user id
     */
    private static void uninstallPackages(Set<String> list, int userId) {
        BlackBoxCore blackBoxCore = BlackBoxCore.get();
        for (String packageName : list) {
            blackBoxCore.uninstallPackageAsUser(packageName, userId);
        }
    }

    /**
     * Installs all Google services and apps into the virtual environment
     * for the given user.  If installation of any package fails, all
     * previously installed GApps for that user are rolled back.
     *
     * @param userId target virtual user id
     * @return {@link InstallResult} indicating success or the first failure
     */
    public static InstallResult installGApps(int userId) {
        Set<String> googleApps = new HashSet<>();

        googleApps.addAll(GOOGLE_SERVICE);
        googleApps.addAll(GOOGLE_APP);

        InstallResult installResult = installPackages(googleApps, userId);
        if (!installResult.success) {
            uninstallGApps(userId);
            return installResult;
        }
        return installResult;
    }

    /**
     * Uninstalls all Google services and apps from the virtual environment
     * for the given user.
     *
     * @param userId target virtual user id
     */
    public static void uninstallGApps(int userId) {
        uninstallPackages(GOOGLE_SERVICE, userId);
        uninstallPackages(GOOGLE_APP, userId);
    }

    /**
     * Removes the given package name from both the Google service and app
     * tracking sets.  Useful when a particular GMS component should be
     * excluded from future installations.
     *
     * @param packageName the package to remove
     */
    public static void remove(String packageName) {
        GOOGLE_SERVICE.remove(packageName);
        GOOGLE_APP.remove(packageName);
    }


    /**
     * Checks whether the host device has Google Play Services installed.
     *
     * @return {@code true} if {@value #GMS_PKG} is present on the device
     */
    public static boolean isSupportGms() {
        try {
            BlackBoxCore.getPackageManager().getPackageInfo(GMS_PKG, 0);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }

    /**
     * Checks whether Google Play Services is installed inside the virtual
     * environment for the specified user.
     *
     * @param userId target virtual user id
     * @return {@code true} if GMS is installed for the user
     */
    public static boolean isInstalledGoogleService(int userId) {
        return BlackBoxCore.get().isInstalled(GMS_PKG, userId);
    }
}