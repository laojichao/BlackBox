package top.niunaijun.blackbox.fake.frameworks;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.RemoteException;

import java.util.Collections;
import java.util.List;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.pm.IBPackageManagerService;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.entity.pm.InstallResult;
import top.niunaijun.blackbox.entity.pm.InstalledPackage;

/**
 * Client-side manager for package operations within the virtual environment. Provides
 * a facade over {@link IBPackageManagerService} for resolving, querying, installing,
 * and uninstalling packages scoped to the current virtual user.
 */
public class BPackageManager extends BlackManager<IBPackageManagerService> {
    private static final BPackageManager sPackageManager = new BPackageManager();

    /**
     * Returns the singleton instance of {@link BPackageManager}.
     *
     * @return the singleton BPackageManager instance
     */
    public static BPackageManager get() {
        return sPackageManager;
    }

    @Override
    protected String getServiceName() {
        return ServiceManager.PACKAGE_MANAGER;
    }

    /**
     * Returns a launch Intent for the given package, attempting CATEGORY_INFO first,
     * then falling back to CATEGORY_LAUNCHER.
     *
     * @param packageName the package to find a launch intent for
     * @param userId      the virtual user ID
     * @return the launch Intent, or null if no launchable activity is found
     */
    public Intent getLaunchIntentForPackage(String packageName, int userId) {
        Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
        intentToResolve.addCategory(Intent.CATEGORY_INFO);
        intentToResolve.setPackage(packageName);
        List<ResolveInfo> ris = queryIntentActivities(intentToResolve,
                0,
                intentToResolve.resolveTypeIfNeeded(BlackBoxCore.getContext().getContentResolver()),
                userId);

        // Otherwise, try to find a main launcher activity.
        if (ris == null || ris.size() <= 0) {
            // reuse the intent instance
            intentToResolve.removeCategory(Intent.CATEGORY_INFO);
            intentToResolve.addCategory(Intent.CATEGORY_LAUNCHER);
            intentToResolve.setPackage(packageName);
            ris = queryIntentActivities(intentToResolve,
                    0,
                    intentToResolve.resolveTypeIfNeeded(BlackBoxCore.getContext().getContentResolver()),
                    userId);
        }
        if (ris == null || ris.size() <= 0) {
            return null;
        }
        Intent intent = new Intent(intentToResolve);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(ris.get(0).activityInfo.packageName,
                ris.get(0).activityInfo.name);
        return intent;
    }

    /**
     * Resolves a service intent to a ResolveInfo.
     *
     * @param intent       the Intent to resolve
     * @param flags        additional flags
     * @param resolvedType the resolved MIME type
     * @param userId       the virtual user ID
     * @return the ResolveInfo, or null on failure
     */
    public ResolveInfo resolveService(Intent intent, int flags, String resolvedType, int userId) {
        try {
            return getService().resolveService(intent, flags, resolvedType, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Resolves an activity intent to a ResolveInfo.
     *
     * @param intent       the Intent to resolve
     * @param flags        additional flags
     * @param resolvedType the resolved MIME type
     * @param userId       the virtual user ID
     * @return the ResolveInfo, or null on failure
     */
    public ResolveInfo resolveActivity(Intent intent, int flags, String resolvedType, int userId) {
        try {
            return getService().resolveActivity(intent, flags, resolvedType, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Resolves a content provider by authority.
     *
     * @param authority the provider authority
     * @param flags     additional flags
     * @param userId    the virtual user ID
     * @return the ProviderInfo, or null on failure
     */
    public ProviderInfo resolveContentProvider(String authority, int flags, int userId) {
        try {
            return getService().resolveContentProvider(authority, flags, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Resolves an intent to a ResolveInfo.
     *
     * @param intent       the Intent to resolve
     * @param resolvedType the resolved MIME type
     * @param flags        additional flags
     * @param userId       the virtual user ID
     * @return the ResolveInfo, or null on failure
     */
    public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags, int userId) {
        try {
            return getService().resolveIntent(intent, resolvedType, flags, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Returns the ApplicationInfo for a package.
     *
     * @param packageName the package name
     * @param flags       additional flags
     * @param userId      the virtual user ID
     * @return the ApplicationInfo, or null on failure
     */
    public ApplicationInfo getApplicationInfo(String packageName, int flags, int userId) {
        try {
            return getService().getApplicationInfo(packageName, flags, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Returns the PackageInfo for a package.
     *
     * @param packageName the package name
     * @param flags       additional flags
     * @param userId      the virtual user ID
     * @return the PackageInfo, or null on failure
     */
    public PackageInfo getPackageInfo(String packageName, int flags, int userId) {
        try {
            return getService().getPackageInfo(packageName, flags, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Returns the ServiceInfo for a component.
     *
     * @param component the ComponentName of the service
     * @param flags     additional flags
     * @param userId    the virtual user ID
     * @return the ServiceInfo, or null on failure
     */
    public ServiceInfo getServiceInfo(ComponentName component, int flags, int userId) {
        try {
            return getService().getServiceInfo(component, flags, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Returns the ActivityInfo for a broadcast receiver.
     *
     * @param componentName the ComponentName of the receiver
     * @param flags         additional flags
     * @param userId        the virtual user ID
     * @return the ActivityInfo, or null on failure
     */
    public ActivityInfo getReceiverInfo(ComponentName componentName, int flags, int userId) {
        try {
            return getService().getReceiverInfo(componentName, flags, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Returns the ActivityInfo for a component.
     *
     * @param component the ComponentName of the activity
     * @param flags     additional flags
     * @param userId    the virtual user ID
     * @return the ActivityInfo, or null on failure
     */
    public ActivityInfo getActivityInfo(ComponentName component, int flags, int userId) {
        try {
            return getService().getActivityInfo(component, flags, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Returns the ProviderInfo for a component.
     *
     * @param component the ComponentName of the provider
     * @param flags     additional flags
     * @param userId    the virtual user ID
     * @return the ProviderInfo, or null on failure
     */
    public ProviderInfo getProviderInfo(ComponentName component, int flags, int userId) {
        try {
            return getService().getProviderInfo(component, flags, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Queries activities that can handle the given intent.
     *
     * @param intent       the Intent to query
     * @param flags        additional flags
     * @param resolvedType the resolved MIME type
     * @param userId       the virtual user ID
     * @return the list of ResolveInfo, or null on failure
     */
    public List<ResolveInfo> queryIntentActivities(Intent intent, int flags, String resolvedType, int userId) {
        try {
            return getService().queryIntentActivities(intent, flags, resolvedType, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Queries broadcast receivers that can handle the given intent.
     *
     * @param intent       the Intent to query
     * @param flags        additional flags
     * @param resolvedType the resolved MIME type
     * @param userId       the virtual user ID
     * @return the list of ResolveInfo, or null on failure
     */
    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags, String resolvedType, int userId) {
        try {
            return getService().queryBroadcastReceivers(intent, flags, resolvedType, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Queries content providers for the given process.
     *
     * @param processName the process name
     * @param uid         the process UID
     * @param flags       additional flags
     * @param userId      the virtual user ID
     * @return the list of ProviderInfo, or null on failure
     */
    public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags, int userId) {
        try {
            return getService().queryContentProviders(processName, uid, flags, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Installs a package for a given user.
     *
     * @param file   the APK file path
     * @param option the InstallOption configuration
     * @param userId the virtual user ID
     * @return the InstallResult, or null on failure
     */
    public InstallResult installPackageAsUser(String file, InstallOption option, int userId) {
        try {
            return getService().installPackageAsUser(file, option, userId);
        } catch (RemoteException e) {
            crash(e);
        }
        return null;
    }

    /**
     * Returns all installed applications for the given user.
     *
     * @param flags  additional flags
     * @param userId the virtual user ID
     * @return the list of ApplicationInfo, or an empty list on failure
     */
    public List<ApplicationInfo> getInstalledApplications(int flags, int userId) {
        try {
            return getService().getInstalledApplications(flags, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Returns all installed packages for the given user.
     *
     * @param flags  additional flags
     * @param userId the virtual user ID
     * @return the list of PackageInfo, or an empty list on failure
     */
    public List<PackageInfo> getInstalledPackages(int flags, int userId) {
        try {
            return getService().getInstalledPackages(flags, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Clears the data of a package for the given user.
     *
     * @param packageName the package name to clear
     * @param userId      the virtual user ID
     */
    public void clearPackage(String packageName, int userId) {
        try {
            getService().clearPackage(packageName, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops a package for the given user.
     *
     * @param packageName the package name to stop
     * @param userId      the virtual user ID
     */
    public void stopPackage(String packageName, int userId) {
        try {
            getService().stopPackage(packageName, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uninstalls a package for a given user.
     *
     * @param packageName the package name to uninstall
     * @param userId      the virtual user ID
     */
    public void uninstallPackageAsUser(String packageName, int userId) {
        try {
            getService().uninstallPackageAsUser(packageName, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uninstalls a package across all users.
     *
     * @param packageName the package name to uninstall
     */
    public void uninstallPackage(String packageName) {
        try {
            getService().uninstallPackage(packageName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether a package is installed for the given user.
     *
     * @param packageName the package name
     * @param userId      the virtual user ID
     * @return true if the package is installed, false otherwise
     */
    public boolean isInstalled(String packageName, int userId) {
        try {
            return getService().isInstalled(packageName, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns all installed packages as InstalledPackage objects for a given user.
     *
     * @param userId the virtual user ID
     * @return the list of InstalledPackage, or an empty list on failure
     */
    public List<InstalledPackage> getInstalledPackagesAsUser(int userId) {
        try {
            return getService().getInstalledPackagesAsUser(userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Returns the package names associated with a UID.
     *
     * @param uid the UID to query
     * @return an array of package names, or an empty array on failure
     */
    public String[] getPackagesForUid(int uid) {
        try {
            return getService().getPackagesForUid(uid, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new String[]{};
    }

    private void crash(Throwable e) {
        e.printStackTrace();
    }
}
