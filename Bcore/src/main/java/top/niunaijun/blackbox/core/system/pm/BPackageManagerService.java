package top.niunaijun.blackbox.core.system.pm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.RemoteException;
import android.text.TextUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.GmsCore;
import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.BProcessManagerService;
import top.niunaijun.blackbox.core.system.ISystemService;
import top.niunaijun.blackbox.core.system.ProcessRecord;
import top.niunaijun.blackbox.core.system.user.BUserHandle;
import top.niunaijun.blackbox.core.system.user.BUserInfo;
import top.niunaijun.blackbox.core.system.user.BUserManagerService;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.entity.pm.InstallResult;
import top.niunaijun.blackbox.entity.pm.InstalledPackage;
import top.niunaijun.blackbox.utils.AbiUtils;
import top.niunaijun.blackbox.utils.FileUtils;
import top.niunaijun.blackbox.utils.Slog;
import top.niunaijun.blackbox.utils.compat.PackageParserCompat;
import top.niunaijun.blackbox.utils.compat.XposedParserCompat;

import static android.content.pm.PackageManager.MATCH_DIRECT_BOOT_UNAWARE;


/**
 * Central package management service for the virtual environment.
 *
 * <p>This service mirrors Android's {@code PackageManagerService} at the application level.
 * It manages the lifecycle of all packages installed inside the BlackBox container,
 * including installation, uninstallation, component resolution (activities, services,
 * providers, receivers), and per-user package state. It delegates low-level file
 * operations to {@link BPackageInstallerService} and intent resolution to
 * {@link ComponentResolver}.</p>
 *
 * <p>All public query methods are thread-safe; package map access is guarded by
 * {@code synchronized(mPackages)}.</p>
 *
 * @see BPackageInstallerService
 * @see ComponentResolver
 * @see BPackageSettings
 */
public class BPackageManagerService extends IBPackageManagerService.Stub implements ISystemService {
    public static final String TAG = "BPackageManagerService";
    public static BPackageManagerService sService = new BPackageManagerService();
    private final Settings mSettings = new Settings();
    private final ComponentResolver mComponentResolver;
    private static final BUserManagerService sUserManager = BUserManagerService.get();
    private final List<PackageMonitor> mPackageMonitors = new ArrayList<>();

    final Map<String, BPackageSettings> mPackages = mSettings.mPackages;
    final Object mInstallLock = new Object();

    /**
     * Returns the singleton instance of this service.
     *
     * @return the global BPackageManagerService instance
     */
    public static BPackageManagerService get() {
        return sService;
    }

    /**
     * Constructs the package manager service, initializes the component resolver,
     * and registers a broadcast receiver for real host package install/remove events
     * to trigger a settings re-scan.
     */
    public BPackageManagerService() {
        mComponentResolver = new ComponentResolver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        BlackBoxCore.getContext()
                .registerReceiver(mPackageChangedHandler, filter);
    }

    private final BroadcastReceiver mPackageChangedHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if ("android.intent.action.PACKAGE_ADDED".equals(action) || "android.intent.action.PACKAGE_REMOVED".equals(action)) {
                    mSettings.scanPackage();
                }
            }
        }
    };

    /**
     * Retrieves the {@link ApplicationInfo} for a given package in the virtual environment.
     * If the package name matches the host application, the real system info is returned.
     *
     * @param packageName the name of the package to look up
     * @param flags       additional option flags (e.g., GET_META_DATA)
     * @param userId      the virtual user ID
     * @return the ApplicationInfo, or null if the package is not installed for this user
     */
    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags, int userId) {
        if (!sUserManager.exists(userId)) return null;
        if (Objects.equals(packageName, BlackBoxCore.getHostPkg())) {
            try {
                return BlackBoxCore.getPackageManager().getApplicationInfo(packageName, flags);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
        flags = updateFlags(flags, userId);
        // reader
        synchronized (mPackages) {
            // Normalize package name to handle renamed packages and static libs
            BPackageSettings ps = mPackages.get(packageName);
            if (ps != null) {
                BPackage p = ps.pkg;
                return PackageManagerCompat.generateApplicationInfo(p, flags, ps.readUserState(userId), userId);
            }
        }
        return null;
    }

    /**
     * Resolves an intent to a single service within the virtual environment.
     * Returns the first matching service if multiple candidates exist.
     *
     * @param intent        the intent to resolve
     * @param flags         option flags for resolution
     * @param resolvedType  the MIME type resolved from the intent's data
     * @param userId        the virtual user ID
     * @return the best-matching ResolveInfo, or null if no service matches
     */
    @Override
    public ResolveInfo resolveService(Intent intent, int flags, String resolvedType, int userId) {
        if (!sUserManager.exists(userId)) return null;
        List<ResolveInfo> query = queryIntentServicesInternal(
                intent, resolvedType, flags, userId);
        if (query != null) {
            if (query.size() >= 1) {
                // If there is more than one service with the same priority,
                // just arbitrarily pick the first one.
                return query.get(0);
            }
        }
        return null;
    }

    private List<ResolveInfo> queryIntentServicesInternal(Intent intent, String resolvedType, int flags, int userId) {
        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (intent.getSelector() != null) {
                intent = intent.getSelector();
                comp = intent.getComponent();
            }
        }
        if (comp != null) {
            final List<ResolveInfo> list = new ArrayList<>(1);
            final ServiceInfo si = getServiceInfo(comp, flags, userId);
            if (si != null) {
                // When specifying an explicit component, we prevent the service from being
                // used when either 1) the service is in an instant application and the
                // caller is not the same instant application or 2) the calling package is
                // ephemeral and the activity is not visible to ephemeral applications.
                final ResolveInfo ri = new ResolveInfo();
                ri.serviceInfo = si;
                list.add(ri);
            }
            return list;
        }

        // reader
        synchronized (mPackages) {
            String pkgName = intent.getPackage();
            if (pkgName != null) {
                BPackageSettings bPackageSettings = mPackages.get(pkgName);
                if (bPackageSettings != null) {
                    final BPackage pkg = bPackageSettings.pkg;
                    return mComponentResolver.queryServices(intent, resolvedType, flags, pkg.services,
                            userId);
                }
            } else {
               return mComponentResolver.queryServices(intent, resolvedType, flags, userId);
            }
            return Collections.emptyList();
        }
    }

    /**
     * Resolves an intent to the best matching activity within the virtual environment.
     *
     * @param intent        the intent to resolve
     * @param flags         option flags for resolution
     * @param resolvedType  the MIME type resolved from the intent's data
     * @param userId        the virtual user ID
     * @return the best-matching ResolveInfo, or null if no activity matches
     */
    @Override
    public ResolveInfo resolveActivity(Intent intent, int flags, String resolvedType, int userId) {
        if (!sUserManager.exists(userId)) return null;
        List<ResolveInfo> resolves = queryIntentActivities(intent, resolvedType, flags, userId);
        return chooseBestActivity(intent, resolvedType, flags, resolves);
    }

    /**
     * Resolves a content provider by its authority string.
     *
     * @param authority the content provider authority to look up
     * @param flags     option flags for resolution
     * @param userId    the virtual user ID
     * @return the ProviderInfo, or null if no provider matches the authority
     */
    @Override
    public ProviderInfo resolveContentProvider(String authority, int flags, int userId) {
        if (!sUserManager.exists(userId)) return null;
        return mComponentResolver.queryProvider(authority, flags, userId);
    }

    /**
     * Resolves an intent to the best matching activity, considering both implicit
     * and explicit intent resolution rules.
     *
     * @param intent        the intent to resolve
     * @param resolvedType  the MIME type resolved from the intent's data
     * @param flags         option flags for resolution
     * @param userId        the virtual user ID
     * @return the best-matching ResolveInfo, or null if no activity matches
     */
    @Override
    public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags, int userId) {
        if (!sUserManager.exists(userId)) return null;
        List<ResolveInfo> resolves = queryIntentActivities(intent, resolvedType, flags, userId);
        return chooseBestActivity(intent, resolvedType, flags, resolves);
    }

    private ResolveInfo chooseBestActivity(Intent intent, String resolvedType,
                                           int flags, List<ResolveInfo> query) {
        if (query != null) {
            final int N = query.size();
            if (N == 1) {
                return query.get(0);
            } else if (N > 1) {
                // If there is more than one activity with the same priority,
                // then let the user decide between them.
                ResolveInfo r0 = query.get(0);
                ResolveInfo r1 = query.get(1);
                // If the first activity has a higher priority, or a different
                // default, then it is always desirable to pick it.
                if (r0.priority != r1.priority
                        || r0.preferredOrder != r1.preferredOrder
                        || r0.isDefault != r1.isDefault) {
                    return query.get(0);
                }
            }
        }
        return null;
    }

    private List<ResolveInfo> queryIntentActivities(Intent intent,
                                                    String resolvedType, int flags, int userId) {
        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (intent.getSelector() != null) {
                intent = intent.getSelector();
                comp = intent.getComponent();
            }
        }

        if (comp != null) {
            final List<ResolveInfo> list = new ArrayList<>(1);
            final ActivityInfo ai = getActivity(comp, flags, userId);
            if (ai != null) {
                // When specifying an explicit component, we prevent the activity from being
                // used when either 1) the calling package is normal and the activity is within
                // an ephemeral application or 2) the calling package is ephemeral and the
                // activity is not visible to ephemeral applications.
                final ResolveInfo ri = new ResolveInfo();
                ri.activityInfo = ai;
                list.add(ri);
                return list;
            }
        }

        // reader
        synchronized (mPackages) {
            return mComponentResolver.queryActivities(intent, resolvedType, flags, userId);
        }
    }

    /**
     * Queries all services that match the given intent within the virtual environment.
     *
     * @param intent   the intent to match against service intent filters
     * @param flags    option flags for the query
     * @param userId   the virtual user ID
     * @return a list of matching ResolveInfo entries, or an empty list if none match
     */
    @Override
    public List<ResolveInfo> queryIntentServices(
            Intent intent, int flags, int userId) {
        final String resolvedType = intent.resolveTypeIfNeeded(BlackBoxCore.getContext().getContentResolver());
        return this.queryIntentServicesInternal(intent, resolvedType, flags, userId);
    }

    private ActivityInfo getActivity(ComponentName component, int flags,
                                     int userId) {
        flags = updateFlags(flags, userId);
        synchronized (mPackages) {
            BPackage.Activity a = mComponentResolver.getActivity(component);

            if (a != null) {
                BPackageSettings ps = mSettings.mPackages.get(component.getPackageName());
                if (ps == null) return null;
                return PackageManagerCompat.generateActivityInfo(a, flags, ps.readUserState(userId), userId);
            }
        }
        return null;
    }

    /**
     * Retrieves the {@link PackageInfo} for a given package in the virtual environment.
     * If the package name matches the host application, the real system info is returned.
     *
     * @param packageName the name of the package to look up
     * @param flags       additional option flags (e.g., GET_ACTIVITIES, GET_SERVICES)
     * @param userId      the virtual user ID
     * @return the PackageInfo, or null if the package is not installed for this user
     */
    @Override
    public PackageInfo getPackageInfo(String packageName, int flags, int userId) {
        if (!sUserManager.exists(userId)) return null;
        if (Objects.equals(packageName, BlackBoxCore.getHostPkg())) {
            try {
                return BlackBoxCore.getPackageManager().getPackageInfo(packageName, flags);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        flags = updateFlags(flags, userId);
        BPackageSettings ps = null;
        // reader
        synchronized (mPackages) {
            // Normalize package name to handle renamed packages and static libs
            ps = mPackages.get(packageName);
        }
        if (ps != null) {
            return PackageManagerCompat.generatePackageInfo(ps, flags, ps.readUserState(userId), userId);
        }
        return null;
    }

    /**
     * Retrieves the {@link ServiceInfo} for a specific service component.
     *
     * @param component the ComponentName of the service to look up
     * @param flags     additional option flags
     * @param userId    the virtual user ID
     * @return the ServiceInfo, or null if the service is not found or not installed for this user
     */
    @Override
    public ServiceInfo getServiceInfo(ComponentName component, int flags, int userId) {
        if (!sUserManager.exists(userId)) return null;
        synchronized (mPackages) {
            BPackage.Service s = mComponentResolver.getService(component);
            if (s != null) {
                BPackageSettings ps = mPackages.get(component.getPackageName());
                if (ps == null) return null;
                return PackageManagerCompat.generateServiceInfo(
                        s, flags, ps.readUserState(userId), userId);
            }
        }
        return null;
    }

    /**
     * Retrieves the {@link ActivityInfo} for a specific broadcast receiver component.
     *
     * @param component the ComponentName of the receiver to look up
     * @param flags     additional option flags
     * @param userId    the virtual user ID
     * @return the ActivityInfo, or null if the receiver is not found or not installed for this user
     */
    @Override
    public ActivityInfo getReceiverInfo(ComponentName component, int flags, int userId) {
        if (!sUserManager.exists(userId)) return null;
        synchronized (mPackages) {
            BPackage.Activity a = mComponentResolver.getReceiver(component);
            if (a != null) {
                BPackageSettings ps = mPackages.get(component.getPackageName());
                if (ps == null) return null;
                return PackageManagerCompat.generateActivityInfo(
                        a, flags, ps.readUserState(userId), userId);
            }
        }
        return null;
    }

    /**
     * Retrieves the {@link ActivityInfo} for a specific activity component.
     *
     * @param component the ComponentName of the activity to look up
     * @param flags     additional option flags
     * @param userId    the virtual user ID
     * @return the ActivityInfo, or null if the activity is not found or not installed for this user
     */
    @Override
    public ActivityInfo getActivityInfo(ComponentName component, int flags, int userId) {
        if (!sUserManager.exists(userId)) return null;
        synchronized (mPackages) {
            BPackage.Activity a = mComponentResolver.getActivity(component);

            if (a != null) {
                BPackageSettings ps = mPackages.get(component.getPackageName());
                if (ps == null) return null;
                return PackageManagerCompat.generateActivityInfo(
                        a, flags, ps.readUserState(userId), userId);
            }
        }
        return null;
    }

    /**
     * Retrieves the {@link ProviderInfo} for a specific content provider component.
     *
     * @param component the ComponentName of the provider to look up
     * @param flags     additional option flags
     * @param userId    the virtual user ID
     * @return the ProviderInfo, or null if the provider is not found or not installed for this user
     */
    @Override
    public ProviderInfo getProviderInfo(ComponentName component, int flags, int userId) {
        if (!sUserManager.exists(userId)) return null;
        synchronized (mPackages) {
            BPackage.Provider p = mComponentResolver.getProvider(component);
            if (p != null) {
                BPackageSettings ps = mPackages.get(component.getPackageName());
                if (ps == null) return null;
                return PackageManagerCompat.generateProviderInfo(
                        p, flags, ps.readUserState(userId), userId);
            }
        }
        return null;
    }

    /**
     * Returns a list of {@link ApplicationInfo} for all installed applications
     * in the virtual environment for the specified user.
     *
     * @param flags  additional option flags
     * @param userId the virtual user ID
     * @return list of ApplicationInfo entries; empty if the user does not exist
     */
    @Override
    public List<ApplicationInfo> getInstalledApplications(int flags, int userId) {
        return getInstalledApplicationsListInternal(flags, userId, Binder.getCallingUid());
    }

    /**
     * Returns a list of {@link PackageInfo} for all installed packages
     * in the virtual environment for the specified user.
     *
     * @param flags  additional option flags
     * @param userId the virtual user ID
     * @return list of PackageInfo entries; empty if the user does not exist
     */
    @Override
    public List<PackageInfo> getInstalledPackages(int flags, int userId) {
        final int callingUid = Binder.getCallingUid();
//        if (getInstantAppPackageName(callingUid) != null) {
//            return ParceledListSlice.emptyList();
//        }
        if (!sUserManager.exists(userId)) return Collections.emptyList();

        // writer
        synchronized (mPackages) {
            ArrayList<PackageInfo> list;
            list = new ArrayList<>(mPackages.size());
            for (BPackageSettings ps : mPackages.values()) {
//                if (filterSharedLibPackageLPr(ps, callingUid, userId, flags)) {
//                    continue;
//                }
//                if (filterAppAccessLPr(ps, callingUid, userId)) {
//                    continue;
//                }
                PackageInfo pi = getPackageInfo(ps.pkg.packageName, flags, userId);
                if (pi != null) {
                    list.add(pi);
                }
            }
            return new ArrayList<>(list);
        }
    }

    private List<ApplicationInfo> getInstalledApplicationsListInternal(int flags, int userId,
                                                                       int callingUid) {
        if (!sUserManager.exists(userId)) return Collections.emptyList();

        // writer
        synchronized (mPackages) {
            ArrayList<ApplicationInfo> list;
            list = new ArrayList<>(mPackages.size());
            Collection<BPackageSettings> packageSettings = mPackages.values();
            for (BPackageSettings ps : packageSettings) {
//                if (filterSharedLibPackageLPr(ps, Binder.getCallingUid(), userId, flags)) {
//                    continue;
//                }
//                if (filterAppAccessLPr(ps, callingUid, userId)) {
//                    continue;
//                }
                if (GmsCore.isGoogleAppOrService(ps.pkg.packageName))
                    continue;
                ApplicationInfo ai = PackageManagerCompat.generateApplicationInfo(ps.pkg, flags,
                        ps.readUserState(userId), userId);
                if (ai != null) {
                    list.add(ai);
                }
            }
            return list;
        }
    }

    /**
     * Queries all activities that match the given intent within the virtual environment.
     * Supports both implicit resolution (all activities) and explicit package-scoped queries.
     *
     * @param intent        the intent to match against activity intent filters
     * @param flags         option flags for the query
     * @param resolvedType  the MIME type resolved from the intent's data
     * @param userId        the virtual user ID
     * @return a list of matching ResolveInfo entries
     * @throws RemoteException if the remote call fails
     */
    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent, int flags, String resolvedType, int userId) throws RemoteException {
        if (!sUserManager.exists(userId)) return Collections.emptyList();
        final String pkgName = intent.getPackage();
        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (intent.getSelector() != null) {
                intent = intent.getSelector();
                comp = intent.getComponent();
            }
        }

        if (comp != null) {
            final List<ResolveInfo> list = new ArrayList<>(1);
            final ActivityInfo ai = getActivityInfo(comp, flags, userId);
            if (ai != null) {
                // When specifying an explicit component, we prevent the activity from being
                // used when either 1) the calling package is normal and the activity is within
                // an ephemeral application or 2) the calling package is ephemeral and the
                // activity is not visible to ephemeral applications.
                final ResolveInfo ri = new ResolveInfo();
                ri.activityInfo = ai;
                list.add(ri);
            }
            return list;
        }

        // reader
        List<ResolveInfo> result;
        synchronized (mPackages) {
            if (pkgName != null) {
                BPackageSettings bPackageSettings = mPackages.get(pkgName);
                result = null;
                if (bPackageSettings != null) {
                    final BPackage pkg = bPackageSettings.pkg;

                    result = mComponentResolver.queryActivities(
                            intent, resolvedType, flags, pkg.activities, userId);
                }
                if (result == null || result.size() == 0) {
                    // the caller wants to resolve for a particular package; however, there
                    // were no installed results, so, try to find an ephemeral result
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                }
                return result;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Queries all broadcast receivers that match the given intent within the virtual environment.
     * Supports both implicit and explicit (package-scoped) resolution.
     *
     * @param intent        the intent to match against receiver intent filters
     * @param flags         option flags for the query
     * @param resolvedType  the MIME type resolved from the intent's data
     * @param userId        the virtual user ID
     * @return a list of matching ResolveInfo entries
     * @throws RemoteException if the remote call fails
     */
    @Override
    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags, String resolvedType, int userId) throws RemoteException {
        if (!sUserManager.exists(userId)) return Collections.emptyList();

        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (intent.getSelector() != null) {
                intent = intent.getSelector();
                comp = intent.getComponent();
            }
        }
        if (comp != null) {
            final List<ResolveInfo> list = new ArrayList<>(1);
            final ActivityInfo ai = getReceiverInfo(comp, flags, userId);
            if (ai != null) {
                // When specifying an explicit component, we prevent the activity from being
                // used when either 1) the calling package is normal and the activity is within
                // an instant application or 2) the calling package is ephemeral and the
                // activity is not visible to instant applications.
                ResolveInfo ri = new ResolveInfo();
                ri.activityInfo = ai;
                list.add(ri);
            }
            return list;
        }

        // reader
        synchronized (mPackages) {
            String pkgName = intent.getPackage();
            BPackageSettings bPackageSettings = mPackages.get(pkgName);
            if (bPackageSettings != null) {
                final BPackage pkg = bPackageSettings.pkg;
                return mComponentResolver.queryReceivers(
                        intent, resolvedType, flags, pkg.receivers, userId);
            } else {
                return mComponentResolver.queryReceivers(intent, resolvedType, flags, userId);
            }
        }
    }

    /**
     * Queries all content providers running in the specified process.
     *
     * @param processName the process name to filter by
     * @param uid         the UID (currently unused in filtering)
     * @param flags       additional option flags
     * @param userId      the virtual user ID
     * @return a list of matching ProviderInfo entries
     * @throws RemoteException if the remote call fails
     */
    @Override
    public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags, int userId) throws RemoteException {
        if (!sUserManager.exists(userId)) return Collections.emptyList();

        List<ProviderInfo> providers = new ArrayList<>();
        if (TextUtils.isEmpty(processName))
            return providers;
        providers.addAll(mComponentResolver.queryProviders(processName, null, flags, userId));
        return providers;
    }

    /**
     * Installs an APK file for a specific virtual user. Parses the APK, validates
     * ABI compatibility, creates package settings, delegates file operations to
     * {@link BPackageInstallerService}, registers all components, and notifies monitors.
     *
     * @param file   the path or URI string of the APK to install
     * @param option installation options and flags
     * @param userId the virtual user ID to install for
     * @return an InstallResult containing the package name on success or an error message on failure
     */
    @Override
    public InstallResult installPackageAsUser(String file, InstallOption option, int userId) {
        synchronized (mInstallLock) {
            return installPackageAsUserLocked(file, option, userId);
        }
    }

    /**
     * Uninstalls a package for a specific virtual user. Kills running processes,
     * removes user data, and if this is the last user, removes the package entirely
     * from settings and the component resolver.
     *
     * @param packageName the name of the package to uninstall
     * @param userId      the virtual user ID to uninstall for
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void uninstallPackageAsUser(String packageName, int userId) throws RemoteException {
        synchronized (mInstallLock) {
            synchronized (mPackages) {
                BPackageSettings ps = mPackages.get(packageName);
                if (ps == null)
                    return;
                if (ps.installOption.isFlag(InstallOption.FLAG_XPOSED) && userId != BUserHandle.USER_XPOSED) {
                    return;
                }
                if (!isInstalled(packageName, userId)) {
                    return;
                }
                boolean removeApp = ps.getUserState().size() <= 1;
                BProcessManagerService.get().killPackageAsUser(packageName, userId);
                int i = BPackageInstallerService.get().uninstallPackageAsUser(ps, removeApp, userId);
                if (i < 0) {
                    // todo
                }

                if (removeApp) {
                    mSettings.removePackage(packageName);
                    mComponentResolver.removeAllComponents(ps.pkg);
                } else {
                    ps.removeUser(userId);
                    ps.save();
                }
                onPackageUninstalled(packageName, removeApp, userId);
            }
        }
    }

    /**
     * Uninstalls a package from all virtual users. For Xposed modules, iterates
     * all users; for regular packages, iterates only the users that have it installed.
     * Kills all associated processes, removes all user data, and clears the package
     * from settings and the component resolver.
     *
     * @param packageName the name of the package to uninstall globally
     */
    @Override
    public void uninstallPackage(String packageName) {
        synchronized (mInstallLock) {
            synchronized (mPackages) {
                BPackageSettings ps = mPackages.get(packageName);
                if (ps == null)
                    return;
                BProcessManagerService.get().killAllByPackageName(packageName);
                if (ps.installOption.isFlag(InstallOption.FLAG_XPOSED)) {
                    for (BUserInfo user : BUserManagerService.get().getAllUsers()) {
                        int i = BPackageInstallerService.get().uninstallPackageAsUser(ps, true, user.id);
                        if (i < 0) {
                            continue;
                        }
                        onPackageUninstalled(packageName, true, user.id);
                    }
                } else {
                    for (Integer userId : ps.getUserIds()) {
                        int i = BPackageInstallerService.get().uninstallPackageAsUser(ps, true, userId);
                        if (i < 0) {
                            continue;
                        }
                        onPackageUninstalled(packageName, true, userId);
                    }
                }
                mSettings.removePackage(packageName);
                mComponentResolver.removeAllComponents(ps.pkg);
            }
        }
    }

    /**
     * Clears the data of a package for a specific virtual user without uninstalling it.
     * Kills running processes and delegates to {@link BPackageInstallerService#clearPackage}.
     *
     * @param packageName the name of the package whose data to clear
     * @param userId      the virtual user ID
     */
    @Override
    public void clearPackage(String packageName, int userId) {
        if (!isInstalled(packageName, userId)) {
            return;
        }
        BProcessManagerService.get().killPackageAsUser(packageName, userId);
        BPackageSettings ps = mPackages.get(packageName);
        if (ps == null)
            return;
        int i = BPackageInstallerService.get().clearPackage(ps, userId);
    }

    /**
     * Stops all processes belonging to a package for a specific virtual user.
     *
     * @param packageName the name of the package to stop
     * @param userId      the virtual user ID
     */
    @Override
    public void stopPackage(String packageName, int userId) {
        BProcessManagerService.get().killPackageAsUser(packageName, userId);
    }

    /**
     * Removes all packages installed for a specific virtual user, effectively
     * deleting the user's entire virtual application environment.
     *
     * @param userId the virtual user ID to delete
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void deleteUser(int userId) throws RemoteException {
        synchronized (mPackages) {
            for (BPackageSettings ps : mPackages.values()) {
                uninstallPackageAsUser(ps.pkg.packageName, userId);
            }
        }
    }

    /**
     * Checks whether a package is installed for a specific virtual user.
     *
     * @param packageName the name of the package to check
     * @param userId      the virtual user ID
     * @return true if the package is installed for the given user, false otherwise
     */
    @Override
    public boolean isInstalled(String packageName, int userId) {
        if (!sUserManager.exists(userId)) return false;
        synchronized (mPackages) {
            BPackageSettings ps = mPackages.get(packageName);
            if (ps == null)
                return false;
            return ps.getInstalled(userId);
        }
    }

    /**
     * Returns a simplified list of installed packages for a specific virtual user,
     * containing only package name and user ID. Excludes Google service packages.
     *
     * @param userId the virtual user ID
     * @return list of InstalledPackage entries; empty if the user does not exist
     */
    @Override
    public List<InstalledPackage> getInstalledPackagesAsUser(int userId) {
        if (!sUserManager.exists(userId)) return Collections.emptyList();
        synchronized (mPackages) {
            List<InstalledPackage> installedPackages = new ArrayList<>();
            for (BPackageSettings ps : mPackages.values()) {
                if (ps.getInstalled(userId) && !GmsCore.isGoogleAppOrService(ps.pkg.packageName)) {
                    InstalledPackage installedPackage = new InstalledPackage();
                    installedPackage.userId = userId;
                    installedPackage.packageName = ps.pkg.packageName;
                    installedPackages.add(installedPackage);
                }
            }
            return installedPackages;
        }
    }

    /**
     * Returns the package names associated with a given UID for a specific virtual user.
     * Falls back to the calling process's package name if no match is found.
     *
     * @param uid    the application UID to look up
     * @param userId the virtual user ID
     * @return an array of package names matching the UID; may be empty
     * @throws RemoteException if the remote call fails
     */
    @Override
    public String[] getPackagesForUid(int uid, int userId) throws RemoteException {
        if (!sUserManager.exists(userId)) return new String[]{};
        synchronized (mPackages) {
            List<String> packages = new ArrayList<>();
            for (BPackageSettings ps : mPackages.values()) {
                String packageName = ps.pkg.packageName;
                if (ps.getInstalled(userId) && getAppId(packageName) == uid) {
                    packages.add(packageName);
                }
            }
            if (packages.isEmpty()) {
                ProcessRecord processByPid = BProcessManagerService.get().findProcessByPid(getCallingPid());
                if (processByPid != null) {
                    packages.add(processByPid.getPackageName());
                }
            }
            return packages.toArray(new String[]{});
        }
    }

    private InstallResult installPackageAsUserLocked(String file, InstallOption option, int userId) {
        long l = System.currentTimeMillis();
        InstallResult result = new InstallResult();
        File apkFile = null;
        try {
            if (!sUserManager.exists(userId)) {
                sUserManager.createUser(userId);
            }
            if (option.isFlag(InstallOption.FLAG_URI_FILE)) {
                apkFile = new File(BEnvironment.getCacheDir(), UUID.randomUUID().toString() + ".apk");
                InputStream inputStream = BlackBoxCore.getContext().getContentResolver().openInputStream(Uri.parse(file));
                FileUtils.copyFile(inputStream, apkFile);
            } else {
                apkFile = new File(file);
            }

            if (option.isFlag(InstallOption.FLAG_XPOSED) && userId != BUserHandle.USER_XPOSED) {
                return new InstallResult().installError("Please install the XP module in XP module management");
            }
            if (option.isFlag(InstallOption.FLAG_XPOSED) && !XposedParserCompat.isXPModule(apkFile.getAbsolutePath())) {
                return new InstallResult().installError("not a XP module");
            }

            PackageInfo packageArchiveInfo = BlackBoxCore.getPackageManager().getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);
            if (packageArchiveInfo == null) {
                return result.installError("getPackageArchiveInfo error.Please check whether APK is normal.");
            }

            boolean support = AbiUtils.isSupport(apkFile);
            if (!support) {
                String msg = packageArchiveInfo.applicationInfo.loadLabel(BlackBoxCore.getPackageManager()) + "[" + packageArchiveInfo.packageName + "]";
                return result.installError(packageArchiveInfo.packageName,
                        msg + "\n" + (BlackBoxCore.is64Bit() ? "The box does not support 32-bit Application" : "The box does not support 64-bit Application"));
            }
            PackageParser.Package aPackage = parserApk(apkFile.getAbsolutePath());
            if (aPackage == null) {
                return result.installError("parser apk error.");
            }
            result.packageName = aPackage.packageName;

            if (option.isFlag(InstallOption.FLAG_SYSTEM)) {
                aPackage.applicationInfo = BlackBoxCore.getPackageManager().getPackageInfo(aPackage.packageName, 0).applicationInfo;
            }
            BPackageSettings bPackageSettings = mSettings.getPackageLPw(aPackage.packageName, aPackage, option);

            // stop pkg
            BProcessManagerService.get().killPackageAsUser(aPackage.packageName, userId);

            int i = BPackageInstallerService.get().installPackageAsUser(bPackageSettings, userId);
            if (i < 0) {
                return result.installError("install apk error.");
            }
            synchronized (mPackages) {
                bPackageSettings.setInstalled(true, userId);
                bPackageSettings.save();
            }
            mComponentResolver.removeAllComponents(bPackageSettings.pkg);
            mComponentResolver.addAllComponents(bPackageSettings.pkg);
            mSettings.scanPackage(aPackage.packageName);
            onPackageInstalled(bPackageSettings.pkg.packageName, userId);
            return result;
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (apkFile != null && option.isFlag(InstallOption.FLAG_URI_FILE)) {
                FileUtils.deleteDir(apkFile);
            }
            Slog.d(TAG, "install finish: " + (System.currentTimeMillis() - l) + "ms");
        }
        return result;
    }

    private PackageParser.Package parserApk(String file) {
        try {
            PackageParser parser = PackageParserCompat.createParser(new File(file));
            PackageParser.Package aPackage = PackageParserCompat.parsePackage(parser, new File(file), 0);
            PackageParserCompat.collectCertificates(parser, aPackage, 0);
            return aPackage;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    static String fixProcessName(String defProcessName, String processName) {
        if (processName == null) {
            return defProcessName;
        }
        return processName;
    }

    /**
     * Update given flags based on encryption status of current user.
     */
    private int updateFlags(int flags, int userId) {
        if ((flags & (PackageManager.MATCH_DIRECT_BOOT_UNAWARE
                | PackageManager.MATCH_DIRECT_BOOT_AWARE)) != 0) {
            // Caller expressed an explicit opinion about what encryption
            // aware/unaware components they want to see, so fall through and
            // give them what they want
        } else {
            // Caller expressed no opinion, so match based on user state
            flags |= PackageManager.MATCH_DIRECT_BOOT_AWARE | MATCH_DIRECT_BOOT_UNAWARE;
        }
        return flags;
    }

    /**
     * Returns the application ID (UID) for a given package name, or -1 if not found.
     *
     * @param packageName the name of the package to look up
     * @return the application ID, or -1 if the package is not registered
     */
    public int getAppId(String packageName) {
        BPackageSettings bPackageSettings = mPackages.get(packageName);
        if (bPackageSettings != null)
            return bPackageSettings.appId;
        return -1;
    }

    Settings getSettings() {
        return mSettings;
    }

    /**
     * Registers a {@link PackageMonitor} to receive notifications when packages
     * are installed or uninstalled.
     *
     * @param monitor the monitor to register
     */
    public void addPackageMonitor(PackageMonitor monitor) {
        mPackageMonitors.add(monitor);
    }

    /**
     * Unregisters a previously registered {@link PackageMonitor}.
     *
     * @param monitor the monitor to remove
     */
    public void removePackageMonitor(PackageMonitor monitor) {
        mPackageMonitors.remove(monitor);
    }

    void onPackageUninstalled(String packageName, boolean isRemove, int userId) {
        for (PackageMonitor packageMonitor : mPackageMonitors) {
            packageMonitor.onPackageUninstalled(packageName, isRemove, userId);
        }
        Slog.d(TAG, "onPackageUninstalled: " + packageName + ", userId: " + userId);
    }

    void onPackageInstalled(String packageName, int userId) {
        for (PackageMonitor packageMonitor : mPackageMonitors) {
            packageMonitor.onPackageInstalled(packageName, userId);
        }
        Slog.d(TAG, "onPackageInstalled: " + packageName + ", userId: " + userId);
    }

    /**
     * Retrieves the {@link BPackageSettings} for a specific package by name.
     *
     * @param packageName the name of the package to look up
     * @return the BPackageSettings, or null if the package is not registered
     */
    public BPackageSettings getBPackageSetting(String packageName) {
        return mPackages.get(packageName);
    }

    /**
     * Returns a snapshot list of all registered package settings.
     *
     * @return a new list containing all BPackageSettings currently tracked
     */
    public List<BPackageSettings> getBPackageSettings() {
        return new ArrayList<>(mPackages.values());
    }

    /**
     * Called when the system is fully initialized. Scans all installed packages,
     * clears stale component registrations, and re-registers all known components.
     */
    @Override
    public void systemReady() {
        mSettings.scanPackage();
        for (BPackageSettings value : mPackages.values()) {
            mComponentResolver.removeAllComponents(value.pkg);
            mComponentResolver.addAllComponents(value.pkg);
        }
    }
}