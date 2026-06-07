package top.niunaijun.blackbox.core.system;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.env.AppSystemEnv;
import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.accounts.BAccountManagerService;
import top.niunaijun.blackbox.core.system.am.BActivityManagerService;
import top.niunaijun.blackbox.core.system.am.BJobManagerService;
import top.niunaijun.blackbox.core.system.location.BLocationManagerService;
import top.niunaijun.blackbox.core.system.notification.BNotificationManagerService;
import top.niunaijun.blackbox.core.system.os.BStorageManagerService;
import top.niunaijun.blackbox.core.system.pm.BPackageInstallerService;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.core.system.pm.BXposedManagerService;
import top.niunaijun.blackbox.core.system.user.BUserHandle;
import top.niunaijun.blackbox.core.system.user.BUserManagerService;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.utils.FileUtils;

import static top.niunaijun.blackbox.core.env.BEnvironment.EMPTY_JAR;
import static top.niunaijun.blackbox.core.env.BEnvironment.JUNIT_JAR;

/**
 * Bootstrap entry point for the BlackBox virtual engine's system services.
 *
 * <p>On {@link #startup()}, initializes the {@link BEnvironment} directory
 * tree, registers and starts all core subsystem services (package manager,
 * user manager, activity manager, job scheduler, storage, accounts,
 * location, notifications, Xposed, process management, and package
 * installer), installs any pre-configured system packages, and copies
 * auxiliary JAR resources from the host app's assets.</p>
 *
 * <p>This class is a thread-safe singleton; {@link #startup()} is
 * idempotent and will return immediately on subsequent calls.</p>
 */
public class BlackBoxSystem {
    private static BlackBoxSystem sBlackBoxSystem;
    /** Ordered list of all registered system services. */
    private final List<ISystemService> mServices = new ArrayList<>();
    /** Guard ensuring {@link #startup()} runs at most once. */
    private final static AtomicBoolean isStartup = new AtomicBoolean(false);

    /**
     * Returns the singleton {@code BlackBoxSystem} instance (lazy,
     * double-checked locking).
     *
     * @return the system instance
     */
    public static BlackBoxSystem getSystem() {
        if (sBlackBoxSystem == null) {
            synchronized (BlackBoxSystem.class) {
                if (sBlackBoxSystem == null) {
                    sBlackBoxSystem = new BlackBoxSystem();
                }
            }
        }
        return sBlackBoxSystem;
    }

    /**
     * Boots all virtual engine subsystems.  Safe to call multiple times;
     * only the first invocation performs actual work.
     *
     * <p>Startup sequence:</p>
     * <ol>
     *   <li>Create environment directories via {@link BEnvironment#load()}</li>
     *   <li>Register and notify all {@link ISystemService} implementations</li>
     *   <li>Install any packages listed in {@link AppSystemEnv#getPreInstallPackages()}</li>
     *   <li>Copy {@code junit.jar} and {@code empty.jar} from assets to cache</li>
     * </ol>
     */
    public void startup() {
        if (isStartup.getAndSet(true))
            return;
        BEnvironment.load();

        mServices.add(BPackageManagerService.get());
        mServices.add(BUserManagerService.get());
        mServices.add(BActivityManagerService.get());
        mServices.add(BJobManagerService.get());
        mServices.add(BStorageManagerService.get());
        mServices.add(BPackageInstallerService.get());
        mServices.add(BXposedManagerService.get());
        mServices.add(BProcessManagerService.get());
        mServices.add(BAccountManagerService.get());
        mServices.add(BLocationManagerService.get());
        mServices.add(BNotificationManagerService.get());

        for (ISystemService service : mServices) {
            service.systemReady();
        }

        List<String> preInstallPackages = AppSystemEnv.getPreInstallPackages();
        for (String preInstallPackage : preInstallPackages) {
            try {
                if (!BPackageManagerService.get().isInstalled(preInstallPackage, BUserHandle.USER_ALL)) {
                    PackageInfo packageInfo = BlackBoxCore.getPackageManager().getPackageInfo(preInstallPackage, 0);
                    BPackageManagerService.get().installPackageAsUser(packageInfo.applicationInfo.sourceDir, InstallOption.installBySystem(), BUserHandle.USER_ALL);
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        initJarEnv();
    }

    /**
     * Copies auxiliary JAR files ({@code junit.jar} and {@code empty.jar})
     * from the host app's assets into the virtual cache directory.
     */
    private void initJarEnv() {
        try {
            InputStream junit = BlackBoxCore.getContext().getAssets().open("junit.jar");
            FileUtils.copyFile(junit, JUNIT_JAR);

            InputStream empty = BlackBoxCore.getContext().getAssets().open("empty.jar");
            FileUtils.copyFile(empty, EMPTY_JAR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
