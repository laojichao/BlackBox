package top.niunaijun.blackbox.fake.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import black.android.app.BRActivityThread;
import black.android.app.BRContextImpl;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.env.AppSystemEnv;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.fake.service.base.PkgMethodProxy;
import top.niunaijun.blackbox.fake.service.base.ValueMethodProxy;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.Reflector;
import top.niunaijun.blackbox.utils.Slog;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.ParceledListSliceCompat;

/**
 * Proxy for the Android Package Manager system service (IPackageManager).
 * Intercepts all package management operations to resolve package info,
 * activity/service/provider/receiver info, intent resolution, and installed
 * package queries through the virtual environment's package manager. Falls
 * back to the real system service for packages that are not installed in the
 * virtual environment (open packages).
 *
 * @author Milk
 */
public class IPackageManagerProxy extends BinderInvocationStub {
    /** Tag used for logging within this proxy. */
    public static final String TAG = "PackageManagerStub";

    /**
     * Constructs a new proxy by obtaining the system package manager binder.
     */
    public IPackageManagerProxy() {
        super(BRActivityThread.get().sPackageManager().asBinder());
    }

    /**
     * Returns the IPackageManager interface instance from the system service.
     *
     * @return the original IPackageManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRActivityThread.get().sPackageManager();
    }

    /**
     * Replaces the system Package Manager service with this proxy instance,
     * updating both the ActivityThread's sPackageManager reference and the
     * ApplicationPackageManager's mPM field via reflection.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        BRActivityThread.get()._set_sPackageManager(proxyInvocation);
        replaceSystemService("package");
        Object systemContext = BRActivityThread.get(BlackBoxCore.mainThread()).getSystemContext();
        PackageManager packageManager = BRContextImpl.get(systemContext).mPackageManager();
        if (packageManager != null) {
            try {
                Reflector.on("android.app.ApplicationPackageManager")
                        .field("mPM")
                        .set(packageManager, proxyInvocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the environment has been corrupted by another proxy.
     *
     * @return always returns false
     */
    @Override
    public boolean isBadEnv() {
        return false;
    }

    /**
     * Binds value-returning and package-name-aware method hooks for
     * permission change listeners and permission rationale queries.
     */
    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("addOnPermissionsChangeListener", 0));
        addMethodHook(new ValueMethodProxy("removeOnPermissionsChangeListener", 0));
        addMethodHook(new PkgMethodProxy("shouldShowRequestPermissionRationale"));
    }

    /**
     * Hook that intercepts {@code resolveIntent} to resolve intents through
     * the virtual environment's package manager before falling back to the system.
     */
    @ProxyMethod("resolveIntent")
    public static class ResolveIntent extends MethodHook {

        /**
         * Resolves the intent using the virtual package manager; returns the result
         * if found, otherwise delegates to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is the Intent, args[1] is resolvedType, args[2] is flags
         * @return a ResolveInfo from the virtual or real package manager
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            int flags = (int) args[2];
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveIntent(intent, resolvedType, flags, BActivityThread.getUserId());
            if (resolveInfo != null) {
                return resolveInfo;
            }
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code resolveService} to resolve service intents
     * through the virtual environment's package manager before falling back.
     */
    @ProxyMethod("resolveService")
    public static class ResolveService extends MethodHook {

        /**
         * Resolves the service using the virtual package manager; returns the result
         * if found, otherwise delegates to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is the Intent, args[1] is resolvedType, args[2] is flags
         * @return a ResolveInfo from the virtual or real package manager
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            int flags = (int) args[2];
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, flags, resolvedType, BActivityThread.getUserId());
            if (resolveInfo != null) {
                return resolveInfo;
            }
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code setComponentEnabledSetting} to silently
     * ignore component state changes within the virtual environment.
     */
    @ProxyMethod("setComponentEnabledSetting")
    public static class SetComponentEnabledSetting extends MethodHook {

        /**
         * Suppresses the call by returning 0 immediately.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code getPackageInfo} to return package info
     * from the virtual environment's package manager.
     */
    @ProxyMethod("getPackageInfo")
    public static class GetPackageInfo extends MethodHook {

        /**
         * Retrieves package info from the virtual package manager; falls back to the
         * real system service for open packages, or returns null if not found.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is packageName, args[1] is flags
         * @return the PackageInfo, or null if not found in the virtual environment
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String packageName = (String) args[0];
            int flag = (int) args[1];
//            if (ClientSystemEnv.isFakePackage(packageName)) {
//                packageName = BlackBoxCore.getHostPkg();
//            }
            PackageInfo packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, flag, BActivityThread.getUserId());
            if (packageInfo != null) {
                return packageInfo;
            }
            if (AppSystemEnv.isOpenPackage(packageName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    /**
     * Hook that intercepts {@code getPackageUid} to replace the package name
     * with the virtual environment's equivalent before delegation.
     */
    @ProxyMethod("getPackageUid")
    public static class GetPackageUid extends MethodHook {

        /**
         * Replaces the first app package name argument and delegates to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; the first package name is replaced
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code getProviderInfo} to return provider info
     * from the virtual environment's package manager.
     */
    @ProxyMethod("getProviderInfo")
    public static class GetProviderInfo extends MethodHook {

        /**
         * Retrieves provider info from the virtual package manager; falls back to the
         * real system service for open packages, or returns null if not found.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is ComponentName, args[1] is flags
         * @return the ProviderInfo, or null if not found
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            int flags = (int) args[1];
            ProviderInfo providerInfo = BlackBoxCore.getBPackageManager().getProviderInfo(componentName, flags, BActivityThread.getUserId());
            if (providerInfo != null)
                return providerInfo;
            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    /**
     * Hook that intercepts {@code getReceiverInfo} to return broadcast receiver info
     * from the virtual environment's package manager.
     */
    @ProxyMethod("getReceiverInfo")
    public static class GetReceiverInfo extends MethodHook {

        /**
         * Retrieves receiver info from the virtual package manager; falls back to the
         * real system service for open packages, or returns null if not found.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is ComponentName, args[1] is flags
         * @return the ActivityInfo for the receiver, or null if not found
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            int flags = (int) args[1];
            ActivityInfo receiverInfo = BlackBoxCore.getBPackageManager().getReceiverInfo(componentName, flags, BActivityThread.getUserId());
            if (receiverInfo != null)
                return receiverInfo;
            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    /**
     * Hook that intercepts {@code getActivityInfo} to return activity info
     * from the virtual environment's package manager.
     */
    @ProxyMethod("getActivityInfo")
    public static class GetActivityInfo extends MethodHook {

        /**
         * Retrieves activity info from the virtual package manager; falls back to the
         * real system service for open packages, or returns null if not found.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is ComponentName, args[1] is flags
         * @return the ActivityInfo, or null if not found
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            int flags = (int) args[1];
            ActivityInfo activityInfo = BlackBoxCore.getBPackageManager().getActivityInfo(componentName, flags, BActivityThread.getUserId());
            if (activityInfo != null)
                return activityInfo;
            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    /**
     * Hook that intercepts {@code getServiceInfo} to return service info
     * from the virtual environment's package manager.
     */
    @ProxyMethod("getServiceInfo")
    public static class GetServiceInfo extends MethodHook {

        /**
         * Retrieves service info from the virtual package manager; falls back to the
         * real system service for open packages, or returns null if not found.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is ComponentName, args[1] is flags
         * @return the ServiceInfo, or null if not found
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            int flags = (int) args[1];
            ServiceInfo serviceInfo = BlackBoxCore.getBPackageManager().getServiceInfo(componentName, flags, BActivityThread.getUserId());
            if (serviceInfo != null)
                return serviceInfo;
            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    /**
     * Hook that intercepts {@code getInstalledApplications} to return only
     * applications installed in the virtual environment.
     */
    @ProxyMethod("getInstalledApplications")
    public static class GetInstalledApplications extends MethodHook {

        /**
         * Returns the list of installed applications from the virtual package manager
         * wrapped in a ParceledListSlice.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is flags
         * @return a ParceledListSlice of ApplicationInfo objects
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int flags = (int) args[0];
            List<ApplicationInfo> installedApplications = BlackBoxCore.getBPackageManager().getInstalledApplications(flags, BActivityThread.getUserId());
            return ParceledListSliceCompat.create(installedApplications);
        }
    }

    /**
     * Hook that intercepts {@code getInstalledPackages} to return only
     * packages installed in the virtual environment.
     */
    @ProxyMethod("getInstalledPackages")
    public static class GetInstalledPackages extends MethodHook {

        /**
         * Returns the list of installed packages from the virtual package manager
         * wrapped in a ParceledListSlice.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is flags
         * @return a ParceledListSlice of PackageInfo objects
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int flags = (int) args[0];
            List<PackageInfo> installedPackages = BlackBoxCore.getBPackageManager().getInstalledPackages(flags, BActivityThread.getUserId());
            return ParceledListSliceCompat.create(installedPackages);
        }
    }

    /**
     * Hook that intercepts {@code getApplicationInfo} to return application info
     * from the virtual environment's package manager.
     */
    @ProxyMethod("getApplicationInfo")
    public static class GetApplicationInfo extends MethodHook {

        /**
         * Retrieves application info from the virtual package manager; falls back to the
         * real system service for open packages, or returns null if not found.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is packageName, args[1] is flags
         * @return the ApplicationInfo, or null if not found
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String packageName = (String) args[0];
            int flags = (int) args[1];
//            if (ClientSystemEnv.isFakePackage(packageName)) {
//                packageName = BlackBoxCore.getHostPkg();
//            }
            ApplicationInfo applicationInfo = BlackBoxCore.getBPackageManager().getApplicationInfo(packageName, flags, BActivityThread.getUserId());
            if (applicationInfo != null) {
                return applicationInfo;
            }
            if (AppSystemEnv.isOpenPackage(packageName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    /**
     * Hook that intercepts {@code queryContentProviders} to return content providers
     * from the virtual environment's package manager.
     */
    @ProxyMethod("queryContentProviders")
    public static class QueryContentProviders extends MethodHook {

        /**
         * Queries content providers for the current virtual app's process name and UID.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[2] is flags
         * @return a ParceledListSlice of ProviderInfo objects
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int flags = (int) args[2];
            List<ProviderInfo> providers = BlackBoxCore.getBPackageManager().
                    queryContentProviders(BActivityThread.getAppProcessName(), BActivityThread.getBUid(), flags, BActivityThread.getUserId());
            return ParceledListSliceCompat.create(providers);
        }
    }

    /**
     * Hook that intercepts {@code queryIntentReceivers} to query broadcast receivers
     * through the virtual environment's package manager.
     */
    @ProxyMethod("queryIntentReceivers")
    public static class QueryBroadcastReceivers extends MethodHook {

        /**
         * Queries broadcast receivers matching the intent from the virtual package manager.
         * Returns a ParceledListSlice on Android N+ or a plain list on older versions.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments containing Intent, type, and flags
         * @return a list or ParceledListSlice of ResolveInfo objects
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = MethodParameterUtils.getFirstParam(args, Intent.class);
            String type = MethodParameterUtils.getFirstParam(args, String.class);
            Integer flags = MethodParameterUtils.getFirstParam(args, Integer.class);
            List<ResolveInfo> resolves = BlackBoxCore.getBPackageManager().queryBroadcastReceivers(intent, flags, type, BActivityThread.getUserId());
            Slog.d(TAG, "queryIntentReceivers: " + resolves);

            // http://androidxref.com/7.0.0_r1/xref/frameworks/base/core/java/android/app/ApplicationPackageManager.java#872
            if (BuildCompat.isN()) {
                return ParceledListSliceCompat.create(resolves);
            }

            // http://androidxref.com/6.0.1_r10/xref/frameworks/base/core/java/android/app/ApplicationPackageManager.java#699
            return resolves;
        }
    }

    /**
     * Hook that intercepts {@code resolveContentProvider} to resolve content providers
     * through the virtual environment's package manager before falling back.
     */
    @ProxyMethod("resolveContentProvider")
    public static class ResolveContentProvider extends MethodHook {

        /**
         * Resolves the content provider by authority from the virtual package manager;
         * falls back to the real system service if not found.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is authority, args[1] is flags
         * @return the ProviderInfo from the virtual or real package manager
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String authority = (String) args[0];
            int flags = (int) args[1];
            ProviderInfo providerInfo = BlackBoxCore.getBPackageManager().resolveContentProvider(authority, flags, BActivityThread.getUserId());
            if (providerInfo == null) {
                return method.invoke(who, args);
            }
            return providerInfo;
        }
    }

    /**
     * Hook that intercepts {@code canRequestPackageInstalls} to replace the
     * package name before delegating to the system service.
     */
    @ProxyMethod("canRequestPackageInstalls")
    public static class CanRequestPackageInstalls extends MethodHook {

        /**
         * Replaces the first app package name argument and delegates to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; the first package name is replaced
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code getPackagesForUid} to return the virtual app's
     * package names for a given UID, translating host UIDs to virtual UIDs.
     */
    @ProxyMethod("getPackagesForUid")
    public static class GetPackagesForUid extends MethodHook {

        /**
         * Translates the host UID to the virtual UID if necessary, then returns
         * the package names from the virtual package manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is the UID
         * @return an array of package names associated with the UID
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int uid = (Integer) args[0];
            if (uid == BlackBoxCore.getHostUid()) {
                args[0] = BActivityThread.getBUid();
                uid = (int) args[0];
            }
            String[] packagesForUid = BlackBoxCore.getBPackageManager().getPackagesForUid(uid);
            Slog.d(TAG, args[0] + " , " + BActivityThread.getAppProcessName() + " GetPackagesForUid: " + Arrays.toString(packagesForUid));
            return packagesForUid;
        }
    }

    /**
     * Hook that intercepts {@code getInstallerPackageName} to fake the installer
     * as Google Play Store (com.android.vending).
     */
    @ProxyMethod("getInstallerPackageName")
    public static class GetInstallerPackageName extends MethodHook {

        /**
         * Returns "com.android.vending" to indicate Google Play as the installer.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns "com.android.vending"
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            // fake google play
            return "com.android.vending";
        }
    }

    /**
     * Hook that intercepts {@code getSharedLibraries} to return an empty list,
     * hiding shared library information within the virtual environment.
     */
    @ProxyMethod("getSharedLibraries")
    public static class GetSharedLibraries extends MethodHook {

        /**
         * Returns an empty ParceledListSlice to hide shared libraries.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return an empty ParceledListSlice
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            // todo
            return ParceledListSliceCompat.create(new ArrayList<>());
        }
    }

    /**
     * Hook that intercepts {@code getComponentEnabledSetting} to always return
     * the default component enabled state.
     */
    @ProxyMethod("getComponentEnabledSetting")
    public static class getComponentEnabledSetting extends MethodHook {

        /**
         * Returns COMPONENT_ENABLED_STATE_DEFAULT regardless of the actual setting.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
        }
    }
}
