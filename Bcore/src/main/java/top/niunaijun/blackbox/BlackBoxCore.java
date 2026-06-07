package top.niunaijun.blackbox;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import black.android.app.BRActivityThread;
import black.android.os.BRUserHandle;
import me.weishu.reflection.Reflection;
import top.canyie.pine.PineConfig;
import top.niunaijun.blackbox.app.LauncherActivity;
import top.niunaijun.blackbox.app.configuration.AppLifecycleCallback;
import top.niunaijun.blackbox.app.configuration.ClientConfiguration;
import top.niunaijun.blackbox.core.GmsCore;
import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.DaemonService;
import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.user.BUserHandle;
import top.niunaijun.blackbox.core.system.user.BUserInfo;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.entity.pm.InstallResult;
import top.niunaijun.blackbox.entity.pm.InstalledModule;
import top.niunaijun.blackbox.fake.delegate.ContentProviderDelegate;
import top.niunaijun.blackbox.fake.frameworks.BActivityManager;
import top.niunaijun.blackbox.fake.frameworks.BJobManager;
import top.niunaijun.blackbox.fake.frameworks.BPackageManager;
import top.niunaijun.blackbox.fake.frameworks.BStorageManager;
import top.niunaijun.blackbox.fake.frameworks.BUserManager;
import top.niunaijun.blackbox.fake.frameworks.BXposedManager;
import top.niunaijun.blackbox.fake.hook.HookManager;
import top.niunaijun.blackbox.proxy.ProxyManifest;
import top.niunaijun.blackbox.utils.FileUtils;
import top.niunaijun.blackbox.utils.ShellUtils;
import top.niunaijun.blackbox.utils.Slog;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.BundleCompat;
import top.niunaijun.blackbox.utils.compat.XposedParserCompat;
import top.niunaijun.blackbox.utils.provider.ProviderCall;

/**
 * Central singleton entry point for the BlackBox virtual environment framework.
 * Provides the complete API for managing virtual apps (install, launch, uninstall),
 * virtual users, Xposed modules, and Google Mobile Services within sandboxed Android
 * environments. Also handles process type detection (main, server, virtual app client),
 * hook environment initialization, and IPC with the server process.
 * <p>
 * Typical usage:
 * <pre>
 *     BlackBoxCore.get().doAttachBaseContext(context, config);
 *     BlackBoxCore.get().doCreate();
 *     BlackBoxCore.get().installPackageAsUser(apkFile, userId);
 *     BlackBoxCore.get().launchApk("com.example.app", userId);
 * </pre>
 *
 * @author Milk
 */
@SuppressLint({"StaticFieldLeak", "NewApi"})
public class BlackBoxCore extends ClientConfiguration {
    public static final String TAG = "BlackBoxCore";

    private static final BlackBoxCore sBlackBoxCore = new BlackBoxCore();
    private static Context sContext;
    private ProcessType mProcessType;
    private final Map<String, IBinder> mServices = new HashMap<>();
    private Thread.UncaughtExceptionHandler mExceptionHandler;
    private ClientConfiguration mClientConfiguration;
    private final List<AppLifecycleCallback> mAppLifecycleCallbacks = new ArrayList<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final int mHostUid = Process.myUid();
    private final int mHostUserId = BRUserHandle.get().myUserId();

    /**
     * Returns the global singleton instance of BlackBoxCore.
     *
     * @return the singleton BlackBoxCore instance
     */
    public static BlackBoxCore get() {
        return sBlackBoxCore;
    }

    /**
     * Returns the main-thread Handler for posting work to the UI thread.
     *
     * @return the main Looper Handler
     */
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * Returns the host application's PackageManager.
     *
     * @return the host PackageManager instance
     */
    public static PackageManager getPackageManager() {
        return sContext.getPackageManager();
    }

    /**
     * Returns the host application's package name.
     *
     * @return the host package name string
     */
    public static String getHostPkg() {
        return get().getHostPackageName();
    }

    /**
     * Returns the UID of the host process.
     *
     * @return the host process UID
     */
    public static int getHostUid() {
        return get().mHostUid;
    }

    /**
     * Returns the Android user ID of the host process.
     *
     * @return the host user ID
     */
    public static int getHostUserId() {
        return get().mHostUserId;
    }

    /**
     * Returns the application Context for the host app.
     *
     * @return the global application Context
     */
    public static Context getContext() {
        return sContext;
    }

    /**
     * Returns the currently registered uncaught exception handler, if any.
     *
     * @return the exception handler, or {@code null}
     */
    public Thread.UncaughtExceptionHandler getExceptionHandler() {
        return mExceptionHandler;
    }

    /**
     * Sets a custom uncaught exception handler for the virtual environment.
     *
     * @param exceptionHandler the handler to set, or {@code null} to clear
     */
    public void setExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        mExceptionHandler = exceptionHandler;
    }

    /**
     * Initializes the BlackBox framework. Must be called from the host app's
     * {@code attachBaseContext}. Unseals hidden API restrictions, determines the
     * current process type, loads the virtual environment for virtual app processes,
     * starts the daemon service for the server process, and initializes the hook engine.
     *
     * @param context            the host application's base context
     * @param clientConfiguration the client configuration for the framework
     * @throws IllegalArgumentException if {@code clientConfiguration} is {@code null}
     */
    public void doAttachBaseContext(Context context, ClientConfiguration clientConfiguration) {
        if (clientConfiguration == null) {
            throw new IllegalArgumentException("ClientConfiguration is null!");
        }
        Reflection.unseal(context);
        sContext = context;
        mClientConfiguration = clientConfiguration;
        initNotificationManager();

        String processName = getProcessName(getContext());
        if (processName.equals(BlackBoxCore.getHostPkg())) {
            mProcessType = ProcessType.Main;
            startLogcat();
        } else if (processName.endsWith(getContext().getString(R.string.black_box_service_name))) {
            mProcessType = ProcessType.Server;
        } else {
            mProcessType = ProcessType.BAppClient;
        }
        if (BlackBoxCore.get().isBlackProcess()) {
            BEnvironment.load();
            if (processName.endsWith("p0")) {
//                android.os.Debug.waitForDebugger();
            }
//            android.os.Debug.waitForDebugger();
        }
        if (isServerProcess()) {
            if (clientConfiguration.isEnableDaemonService()) {
                Intent intent = new Intent();
                intent.setClass(getContext(), DaemonService.class);
                if (BuildCompat.isOreo()) {
                    getContext().startForegroundService(intent);
                } else {
                    getContext().startService(intent);
                }
            }
        }
        PineConfig.debug = true;
        PineConfig.debuggable = true;
        HookManager.get().init();
    }

    /**
     * Completes framework initialization after the Application's {@code onCreate}.
     * Initializes content provider delegates for virtual app processes and the
     * service manager for non-server processes.
     */
    public void doCreate() {
        // fix contentProvider
        if (isBlackProcess()) {
            ContentProviderDelegate.init();
        }
        if (!isServerProcess()) {
            ServiceManager.initBlackManager();
        }
    }

    /**
     * Returns the current ActivityThread instance via reflection.
     *
     * @return the current ActivityThread object
     */
    public static Object mainThread() {
        return BRActivityThread.get().currentActivityThread();
    }

    /**
     * Starts an Activity within the virtual environment. If the launcher activity feature
     * is enabled in configuration, uses {@link LauncherActivity} as an intermediary;
     * otherwise delegates directly to {@link BActivityManager}.
     *
     * @param intent the Intent specifying the virtual Activity to start
     * @param userId the virtual user ID to start the Activity under
     */
    public void startActivity(Intent intent, int userId) {
        if (mClientConfiguration.isEnableLauncherActivity()) {
            LauncherActivity.launch(intent, userId);
        } else {
            getBActivityManager().startActivity(intent, userId);
        }
    }

    /**
     * Returns the virtual JobManager for scheduling and managing jobs within virtual apps.
     *
     * @return the BJobManager singleton
     */
    public static BJobManager getBJobManager() {
        return BJobManager.get();
    }

    /**
     * Returns the virtual PackageManager for installing, querying, and managing
     * packages within the virtual environment.
     *
     * @return the BPackageManager singleton
     */
    public static BPackageManager getBPackageManager() {
        return BPackageManager.get();
    }

    /**
     * Returns the virtual ActivityManager for managing activities, tasks, and processes
     * within the virtual environment.
     *
     * @return the BActivityManager singleton
     */
    public static BActivityManager getBActivityManager() {
        return BActivityManager.get();
    }

    /**
     * Returns the virtual StorageManager for managing storage volumes within the
     * virtual environment.
     *
     * @return the BStorageManager singleton
     */
    public static BStorageManager getBStorageManager() {
        return BStorageManager.get();
    }

    /**
     * Launches an installed virtual app by its package name. Retrieves the launch Intent
     * and starts the main Activity within the virtual environment.
     *
     * @param packageName the package name of the virtual app to launch
     * @param userId      the virtual user ID
     * @return {@code true} if the app was successfully launched, {@code false} if no
     *         launch Intent was found
     */
    public boolean launchApk(String packageName, int userId) {
        Intent launchIntentForPackage = getBPackageManager().getLaunchIntentForPackage(packageName, userId);
        if (launchIntentForPackage == null) {
            return false;
        }
        startActivity(launchIntentForPackage, userId);
        return true;
    }

    /**
     * Checks whether a package is installed in the virtual environment for a given user.
     *
     * @param packageName the package name to check
     * @param userId      the virtual user ID
     * @return {@code true} if the package is installed
     */
    public boolean isInstalled(String packageName, int userId) {
        return getBPackageManager().isInstalled(packageName, userId);
    }

    /**
     * Uninstalls a package from a specific virtual user's environment.
     *
     * @param packageName the package name to uninstall
     * @param userId      the virtual user ID
     */
    public void uninstallPackageAsUser(String packageName, int userId) {
        getBPackageManager().uninstallPackageAsUser(packageName, userId);
    }

    /**
     * Uninstalls a package from all virtual users.
     *
     * @param packageName the package name to uninstall
     */
    public void uninstallPackage(String packageName) {
        getBPackageManager().uninstallPackage(packageName);
    }

    /**
     * Installs an already-installed system app into the virtual environment for a given user.
     * Uses the system package's source directory as the APK path.
     *
     * @param packageName the system package name to install
     * @param userId      the virtual user ID
     * @return the {@link InstallResult} containing success or error information
     */
    public InstallResult installPackageAsUser(String packageName, int userId) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            return getBPackageManager().installPackageAsUser(packageInfo.applicationInfo.sourceDir, InstallOption.installBySystem(), userId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new InstallResult().installError(e.getMessage());
        }
    }

    /**
     * Installs an APK file into the virtual environment for a given user.
     *
     * @param apk    the APK file to install
     * @param userId the virtual user ID
     * @return the {@link InstallResult} containing success or error information
     */
    public InstallResult installPackageAsUser(File apk, int userId) {
        return getBPackageManager().installPackageAsUser(apk.getAbsolutePath(), InstallOption.installByStorage(), userId);
    }

    /**
     * Installs an APK from a content URI into the virtual environment for a given user.
     *
     * @param apk    the content URI of the APK to install
     * @param userId the virtual user ID
     * @return the {@link InstallResult} containing success or error information
     */
    public InstallResult installPackageAsUser(Uri apk, int userId) {
        return getBPackageManager().installPackageAsUser(apk.toString(), InstallOption.installByStorage().makeUriFile(), userId);
    }

    /**
     * Installs an APK file as an Xposed module in the virtual Xposed user environment.
     *
     * @param apk the APK file containing the Xposed module
     * @return the {@link InstallResult} containing success or error information
     */
    public InstallResult installXPModule(File apk) {
        return getBPackageManager().installPackageAsUser(apk.getAbsolutePath(), InstallOption.installByStorage().makeXposed(), BUserHandle.USER_XPOSED);
    }

    /**
     * Installs an APK from a content URI as an Xposed module in the virtual Xposed
     * user environment.
     *
     * @param apk the content URI of the APK containing the Xposed module
     * @return the {@link InstallResult} containing success or error information
     */
    public InstallResult installXPModule(Uri apk) {
        return getBPackageManager().installPackageAsUser(apk.toString(), InstallOption.installByStorage()
                .makeXposed()
                .makeUriFile(), BUserHandle.USER_XPOSED);
    }

    /**
     * Installs a system package as an Xposed module by looking up its source directory.
     *
     * @param packageName the system package name containing the Xposed module
     * @return the {@link InstallResult} containing success or error information
     */
    public InstallResult installXPModule(String packageName) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            String path = packageInfo.applicationInfo.sourceDir;
            return getBPackageManager().installPackageAsUser(path, InstallOption.installBySystem().makeXposed(), BUserHandle.USER_XPOSED);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new InstallResult().installError(e.getMessage());
        }
    }

    /**
     * Uninstalls an Xposed module by its package name.
     *
     * @param packageName the Xposed module package name to uninstall
     */
    public void uninstallXPModule(String packageName) {
        uninstallPackage(packageName);
    }

    /**
     * Checks whether Xposed framework support is enabled globally.
     *
     * @return {@code true} if Xposed is enabled
     */
    public boolean isXPEnable() {
        return BXposedManager.get().isXPEnable();
    }

    /**
     * Enables or disables Xposed framework support globally.
     *
     * @param enable {@code true} to enable, {@code false} to disable
     */
    public void setXPEnable(boolean enable) {
        BXposedManager.get().setXPEnable(enable);
    }

    /**
     * Checks whether the given APK file is an Xposed module.
     *
     * @param file the APK file to check
     * @return {@code true} if the file is a valid Xposed module
     */
    public boolean isXposedModule(File file) {
        return XposedParserCompat.isXPModule(file.getAbsolutePath());
    }

    /**
     * Checks whether an Xposed module is installed in the virtual Xposed environment.
     *
     * @param packageName the module package name
     * @return {@code true} if the module is installed
     */
    public boolean isInstalledXposedModule(String packageName) {
        return isInstalled(packageName, BUserHandle.USER_XPOSED);
    }

    /**
     * Checks whether a specific Xposed module is enabled.
     *
     * @param packageName the module package name
     * @return {@code true} if the module is enabled
     */
    public boolean isModuleEnable(String packageName) {
        return BXposedManager.get().isModuleEnable(packageName);
    }

    /**
     * Enables or disables a specific Xposed module.
     *
     * @param packageName the module package name
     * @param enable      {@code true} to enable, {@code false} to disable
     */
    public void setModuleEnable(String packageName, boolean enable) {
        BXposedManager.get().setModuleEnable(packageName, enable);
    }

    /**
     * Returns the list of all installed Xposed modules in the virtual environment.
     *
     * @return list of {@link InstalledModule} objects
     */
    public List<InstalledModule> getInstalledXPModules() {
        return BXposedManager.get().getInstalledModules();
    }

    /**
     * Returns installed application info for all virtual apps of a given user.
     *
     * @param flags  flags to pass to the underlying PackageManager query
     * @param userId the virtual user ID
     * @return list of {@link ApplicationInfo} for installed virtual applications
     */
    public List<ApplicationInfo> getInstalledApplications(int flags, int userId) {
        return getBPackageManager().getInstalledApplications(flags, userId);
    }

    /**
     * Returns package info for all installed virtual packages of a given user.
     *
     * @param flags  flags to pass to the underlying PackageManager query
     * @param userId the virtual user ID
     * @return list of {@link PackageInfo} for installed virtual packages
     */
    public List<PackageInfo> getInstalledPackages(int flags, int userId) {
        return getBPackageManager().getInstalledPackages(flags, userId);
    }

    /**
     * Clears all data associated with a virtual app package for a given user.
     *
     * @param packageName the package name to clear
     * @param userId      the virtual user ID
     */
    public void clearPackage(String packageName, int userId) {
        BPackageManager.get().clearPackage(packageName, userId);
    }

    /**
     * Force-stops a virtual app package for a given user.
     *
     * @param packageName the package name to stop
     * @param userId      the virtual user ID
     */
    public void stopPackage(String packageName, int userId) {
        BPackageManager.get().stopPackage(packageName, userId);
    }

    /**
     * Returns all virtual users created in the BlackBox environment.
     *
     * @return list of {@link BUserInfo} objects
     */
    public List<BUserInfo> getUsers() {
        return BUserManager.get().getUsers();
    }

    /**
     * Creates a new virtual user with the specified user ID.
     *
     * @param userId the ID for the new virtual user
     * @return the created {@link BUserInfo}
     */
    public BUserInfo createUser(int userId) {
        return BUserManager.get().createUser(userId);
    }

    /**
     * Deletes a virtual user and all associated data.
     *
     * @param userId the virtual user ID to delete
     */
    public void deleteUser(int userId) {
        BUserManager.get().deleteUser(userId);
    }

    /**
     * Returns the list of registered app lifecycle callbacks.
     *
     * @return list of {@link AppLifecycleCallback} instances
     */
    public List<AppLifecycleCallback> getAppLifecycleCallbacks() {
        return mAppLifecycleCallbacks;
    }

    /**
     * Removes a previously registered app lifecycle callback.
     *
     * @param appLifecycleCallback the callback to remove
     */
    public void removeAppLifecycleCallback(AppLifecycleCallback appLifecycleCallback) {
        mAppLifecycleCallbacks.remove(appLifecycleCallback);
    }

    /**
     * Registers an app lifecycle callback to receive Activity lifecycle events
     * for all virtual apps.
     *
     * @param appLifecycleCallback the callback to register
     */
    public void addAppLifecycleCallback(AppLifecycleCallback appLifecycleCallback) {
        mAppLifecycleCallbacks.add(appLifecycleCallback);
    }

    /**
     * Checks whether the device supports Google Mobile Services (GMS).
     *
     * @return {@code true} if GMS is supported on this device
     */
    public boolean isSupportGms() {
        return GmsCore.isSupportGms();
    }

    /**
     * Checks whether GMS is installed for a specific virtual user.
     *
     * @param userId the virtual user ID
     * @return {@code true} if GMS is installed for the user
     */
    public boolean isInstallGms(int userId) {
        return GmsCore.isInstalledGoogleService(userId);
    }

    /**
     * Installs Google Mobile Services (GMS) for a specific virtual user.
     *
     * @param userId the virtual user ID to install GMS for
     * @return the {@link InstallResult} containing success or error information
     */
    public InstallResult installGms(int userId) {
        return GmsCore.installGApps(userId);
    }

    /**
     * Uninstalls Google Mobile Services (GMS) from a specific virtual user.
     *
     * @param userId the virtual user ID to uninstall GMS from
     * @return {@code true} if GMS was successfully uninstalled
     */
    public boolean uninstallGms(int userId) {
        GmsCore.uninstallGApps(userId);
        return !GmsCore.isInstalledGoogleService(userId);
    }

    /**
     * Retrieves a remote service binder by name from the server process. Caches the
     * binder for subsequent calls and re-fetches if the cached binder is no longer alive.
     * Uses the bind provider to perform IPC with the server process.
     *
     * @param name the service name to look up
     * @return the {@link IBinder} for the requested service
     */
    public IBinder getService(String name) {
        IBinder binder = mServices.get(name);
        if (binder != null && binder.isBinderAlive()) {
            return binder;
        }
        Bundle bundle = new Bundle();
        bundle.putString("_B_|_server_name_", name);
        Bundle vm = ProviderCall.callSafely(ProxyManifest.getBindProvider(), "VM", null, bundle);
        binder = BundleCompat.getBinder(vm, "_B_|_server_");
        Slog.d(TAG, "getService: " + name + ", " + binder);
        mServices.put(name, binder);
        return binder;
    }

    /**
     * Process type
     */
    private enum ProcessType {
        /**
         * Server process
         */
        Server,
        /**
         * Black app process
         */
        BAppClient,
        /**
         * Main process
         */
        Main,
    }

    /**
     * Checks whether the current process is a virtual app client process (hosting
     * a virtual app's code).
     *
     * @return {@code true} if running in a virtual app client process
     */
    public boolean isBlackProcess() {
        return mProcessType == ProcessType.BAppClient;
    }

    /**
     * Checks whether the current process is the main (host app) process.
     *
     * @return {@code true} if running in the main process
     */
    public boolean isMainProcess() {
        return mProcessType == ProcessType.Main;
    }

    /**
     * Checks whether the current process is the server/service process that manages
     * the virtual environment.
     *
     * @return {@code true} if running in the server process
     */
    public boolean isServerProcess() {
        return mProcessType == ProcessType.Server;
    }

    /**
     * Checks whether root access should be hidden from virtual apps.
     *
     * @return {@code true} if root hiding is enabled
     */
    @Override
    public boolean isHideRoot() {
        return mClientConfiguration.isHideRoot();
    }

    /**
     * Checks whether Xposed framework presence should be hidden from virtual apps.
     *
     * @return {@code true} if Xposed hiding is enabled
     */
    @Override
    public boolean isHideXposed() {
        return mClientConfiguration.isHideXposed();
    }

    /**
     * Returns the host application package name from the client configuration.
     *
     * @return the host package name
     */
    @Override
    public String getHostPackageName() {
        return mClientConfiguration.getHostPackageName();
    }

    /**
     * Delegates the install-package request callback to the client configuration.
     *
     * @param file   the APK file to install
     * @param userId the virtual user ID
     * @return {@code true} if the install request was handled
     */
    @Override
    public boolean requestInstallPackage(File file, int userId) {
        return mClientConfiguration.requestInstallPackage(file, userId);
    }

    private void startLogcat() {
        new Thread(() -> {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getContext().getPackageName() + "_logcat.txt");
            FileUtils.deleteDir(file);
            ShellUtils.execCommand("logcat -c", false);
            ShellUtils.execCommand("logcat -f " + file.getAbsolutePath(), false);
        }).start();
    }

    private static String getProcessName(Context context) {
        int pid = Process.myPid();
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == pid) {
                processName = info.processName;
                break;
            }
        }
        if (processName == null) {
            throw new RuntimeException("processName = null");
        }
        return processName;
    }

    /**
     * Checks whether the current process is running in a 64-bit architecture.
     *
     * @return {@code true} if the process is 64-bit
     */
    public static boolean is64Bit() {
        if (BuildCompat.isM()) {
            return Process.is64Bit();
        } else {
            return Build.CPU_ABI.equals("arm64-v8a");
        }
    }

    private void initNotificationManager() {
        NotificationManager nm = (NotificationManager) BlackBoxCore.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ONE_ID = BlackBoxCore.getContext().getPackageName() + ".blackbox_core";
        String CHANNEL_ONE_NAME = "blackbox_core";
        if (BuildCompat.isOreo()) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(notificationChannel);
        }
    }
}
