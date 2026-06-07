package top.niunaijun.blackbox.app.configuration;

import java.io.File;

/**
 * Abstract base configuration class for customizing the behavior of the BlackBox virtual engine.
 * <p>
 * Host applications extend this class to control security features (root/Xposed hiding),
 * service management, launcher behavior, and package installation handling for apps
 * running inside the virtual environment.
 *
 * @author Milk
 */
public abstract class ClientConfiguration {

    /**
     * Returns whether the virtual environment should hide the presence of a rooted device.
     * <p>
     * When enabled, root-related files, binaries, and properties are concealed from
     * applications running inside the virtual container.
     *
     * @return {@code true} to hide root, {@code false} to allow detection; default is {@code false}
     */
    public boolean isHideRoot() {
        return false;
    }

    /**
     * Returns whether the virtual environment should hide Xposed/LSPosed framework artifacts.
     * <p>
     * When enabled, Xposed-related classes, files, and stack traces are concealed from
     * applications running inside the virtual container.
     *
     * @return {@code true} to hide Xposed, {@code false} to allow detection; default is {@code false}
     */
    public boolean isHideXposed() {
        return false;
    }

    /**
     * Returns the package name of the host application that owns this virtual engine instance.
     * <p>
     * This is used internally for resource loading, context creation, and process identification.
     *
     * @return the host application's package name; must not be {@code null}
     */
    public abstract String getHostPackageName();

    /**
     * Returns whether the daemon service for keeping virtual processes alive is enabled.
     * <p>
     * The daemon service monitors and restarts virtual application processes when they
     * are terminated by the system.
     *
     * @return {@code true} to enable the daemon service, {@code false} to disable; default is {@code true}
     */
    public boolean isEnableDaemonService() {
        return true;
    }

    /**
     * Returns whether the transparent launcher activity is used when starting virtual apps.
     * <p>
     * When enabled, a launcher activity displays the app icon before handing off to
     * the actual virtual activity.
     *
     * @return {@code true} to use the launcher activity, {@code false} to start directly; default is {@code true}
     */
    public boolean isEnableLauncherActivity() {
        return true;
    }

    /**
     * Called when an application running inside the virtual environment requests to install
     * a new package (e.g., via an APK file).
     * <p>
     * Implementations can inspect the file and decide whether to handle the installation
     * (e.g., by delegating to the host's package installer).
     *
     * @param file   the APK file that the virtual application wants to install
     * @param userId the virtual user ID requesting the installation
     * @return {@code true} if the installation request was handled, {@code false} to ignore it
     */
    public boolean requestInstallPackage(File file, int userId) {
        return false;
    }
}
