package top.niunaijun.blackbox.core.system;

import android.os.IBinder;

import java.util.HashMap;
import java.util.Map;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.system.accounts.BAccountManagerService;
import top.niunaijun.blackbox.core.system.am.BActivityManagerService;
import top.niunaijun.blackbox.core.system.am.BJobManagerService;
import top.niunaijun.blackbox.core.system.location.BLocationManagerService;
import top.niunaijun.blackbox.core.system.notification.BNotificationManagerService;
import top.niunaijun.blackbox.core.system.os.BStorageManagerService;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.core.system.pm.BXposedManagerService;
import top.niunaijun.blackbox.core.system.user.BUserManagerService;

/**
 * Central registry of BlackBox virtual engine system services, analogous
 * to Android's {@code ServiceManager}.
 *
 * <p>Each service is identified by a string name constant (e.g.
 * {@link #PACKAGE_MANAGER}) and is backed by an {@link IBinder}
 * implementation.  Client code obtains service binders via
 * {@link #getService(String)}.</p>
 *
 * <p>The constructor eagerly registers all core services; the static
 * {@link #initBlackManager()} method triggers their lazy initialization
 * through {@link BlackBoxCore#getService(String)}.</p>
 */
public class ServiceManager {
    private static ServiceManager sServiceManager = null;

    /** Name constant for the activity manager service. */
    public static final String ACTIVITY_MANAGER = "activity_manager";
    /** Name constant for the job scheduler service. */
    public static final String JOB_MANAGER = "job_manager";
    /** Name constant for the package manager service. */
    public static final String PACKAGE_MANAGER = "package_manager";
    /** Name constant for the storage manager service. */
    public static final String STORAGE_MANAGER = "storage_manager";
    /** Name constant for the user manager service. */
    public static final String USER_MANAGER = "user_manager";
    /** Name constant for the Xposed manager service. */
    public static final String XPOSED_MANAGER = "xposed_manager";
    /** Name constant for the account manager service. */
    public static final String ACCOUNT_MANAGER = "account_manager";
    /** Name constant for the location manager service. */
    public static final String LOCATION_MANAGER = "location_manager";
    /** Name constant for the notification manager service. */
    public static final String NOTIFICATION_MANAGER = "notification_manager";

    /** Cached binder instances keyed by service name. */
    private final Map<String, IBinder> mCaches = new HashMap<>();

    /**
     * Returns the singleton {@code ServiceManager} (lazy, double-checked
     * locking).
     *
     * @return the singleton instance
     */
    public static ServiceManager get() {
        if (sServiceManager == null) {
            synchronized (ServiceManager.class) {
                if (sServiceManager == null) {
                    sServiceManager = new ServiceManager();
                }
            }
        }
        return sServiceManager;
    }

    /**
     * Returns the binder for the named service.
     *
     * @param name one of the {@code *_MANAGER} constants
     * @return the service binder, or {@code null} if not registered
     */
    public static IBinder getService(String name) {
        return get().getServiceInternal(name);
    }

    private ServiceManager() {
        mCaches.put(ACTIVITY_MANAGER, BActivityManagerService.get());
        mCaches.put(JOB_MANAGER, BJobManagerService.get());
        mCaches.put(PACKAGE_MANAGER, BPackageManagerService.get());
        mCaches.put(STORAGE_MANAGER, BStorageManagerService.get());
        mCaches.put(USER_MANAGER, BUserManagerService.get());
        mCaches.put(XPOSED_MANAGER, BXposedManagerService.get());
        mCaches.put(ACCOUNT_MANAGER, BAccountManagerService.get());
        mCaches.put(LOCATION_MANAGER, BLocationManagerService.get());
        mCaches.put(NOTIFICATION_MANAGER, BNotificationManagerService.get());
    }

    /**
     * Internal lookup into the binder cache.
     *
     * @param name the service name
     * @return the cached binder, or {@code null}
     */
    public IBinder getServiceInternal(String name) {
        return mCaches.get(name);
    }

    /**
     * Forces lazy initialization of all registered BlackBox system
     * services by requesting each one through {@link BlackBoxCore}.
     *
     * <p>Should be called from the client process to ensure all service
     * singletons are created and bound before any IPC is attempted.</p>
     */
    public static void initBlackManager() {
        BlackBoxCore.get().getService(ACTIVITY_MANAGER);
        BlackBoxCore.get().getService(JOB_MANAGER);
        BlackBoxCore.get().getService(PACKAGE_MANAGER);
        BlackBoxCore.get().getService(STORAGE_MANAGER);
        BlackBoxCore.get().getService(USER_MANAGER);
        BlackBoxCore.get().getService(XPOSED_MANAGER);
        BlackBoxCore.get().getService(ACCOUNT_MANAGER);
        BlackBoxCore.get().getService(LOCATION_MANAGER);
        BlackBoxCore.get().getService(NOTIFICATION_MANAGER);
    }
}
