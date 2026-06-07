package top.niunaijun.blackbox.core.system.pm;

/**
 * Callback interface for monitoring package installation and uninstallation events
 * within the virtual environment.
 * <p>
 * Implementations receive notifications when apps are installed into or removed from
 * a virtual user space, enabling dependent services to update their state accordingly.
 */
public interface PackageMonitor {

    /**
     * Called when a package has been uninstalled from a virtual user.
     *
     * @param packageName the name of the uninstalled package
     * @param isRemove    {@code true} if the package was fully removed, {@code false} if only
     *                    uninstalled for a specific user
     * @param userId      the virtual user ID the package was uninstalled from
     */
    void onPackageUninstalled(String packageName, boolean isRemove, int userId);

    /**
     * Called when a package has been installed into a virtual user.
     *
     * @param packageName the name of the installed package
     * @param userId      the virtual user ID the package was installed into
     */
    void onPackageInstalled(String packageName, int userId);
}
