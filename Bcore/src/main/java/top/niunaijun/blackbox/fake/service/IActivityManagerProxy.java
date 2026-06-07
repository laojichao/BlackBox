package top.niunaijun.blackbox.fake.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.IServiceConnection;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;

import black.android.app.BRActivityManagerNative;
import black.android.app.BRActivityManagerOreo;
import black.android.app.BRLoadedApkReceiverDispatcher;
import black.android.app.BRLoadedApkReceiverDispatcherInnerReceiver;
import black.android.app.BRLoadedApkServiceDispatcher;
import black.android.app.BRLoadedApkServiceDispatcherInnerConnection;
import black.android.content.BRContentProviderNative;
import black.android.content.pm.BRUserInfo;
import black.android.util.BRSingleton;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.env.AppSystemEnv;
import top.niunaijun.blackbox.entity.AppConfig;
import top.niunaijun.blackbox.entity.am.RunningAppProcessInfo;
import top.niunaijun.blackbox.entity.am.RunningServiceInfo;
import top.niunaijun.blackbox.fake.delegate.ContentProviderDelegate;
import top.niunaijun.blackbox.fake.delegate.InnerReceiverDelegate;
import top.niunaijun.blackbox.fake.delegate.ServiceConnectionDelegate;
import top.niunaijun.blackbox.fake.frameworks.BActivityManager;
import top.niunaijun.blackbox.fake.frameworks.BPackageManager;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.fake.hook.ScanClass;
import top.niunaijun.blackbox.fake.service.base.PkgMethodProxy;
import top.niunaijun.blackbox.fake.service.context.providers.ContentProviderStub;
import top.niunaijun.blackbox.proxy.ProxyManifest;
import top.niunaijun.blackbox.proxy.record.ProxyBroadcastRecord;
import top.niunaijun.blackbox.proxy.record.ProxyPendingRecord;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.Reflector;
import top.niunaijun.blackbox.utils.compat.ActivityManagerCompat;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.ParceledListSliceCompat;
import top.niunaijun.blackbox.utils.compat.TaskDescriptionCompat;

import static android.content.pm.PackageManager.GET_META_DATA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Proxy for the Android Activity Manager system service (IActivityManager).
 * Intercepts core system service operations including content provider access,
 * service lifecycle (start/stop/bind/unbind), broadcast dispatching, receiver
 * registration, intent sender creation, permission checks, and process management.
 * All operations are redirected through the virtual environment's activity manager
 * to maintain proper isolation between virtual app instances and the host system.
 * Works in conjunction with {@link ActivityManagerCommonProxy} which handles
 * common activity lifecycle methods.
 *
 * @author Milk
 */
@ScanClass(ActivityManagerCommonProxy.class)
public class IActivityManagerProxy extends ClassInvocationStub {
    /** Tag for logging. */
    public static final String TAG = "ActivityManagerStub";

    /**
     * Returns the IActivityManager singleton instance from the system.
     * Handles API differences between Android Oreo+ and Lollipop+.
     *
     * @return the IActivityManager singleton instance
     */
    @Override
    protected Object getWho() {
        Object iActivityManager = null;
        if (BuildCompat.isOreo()) {
            iActivityManager = BRActivityManagerOreo.get().IActivityManagerSingleton();
        } else if (BuildCompat.isL()) {
            iActivityManager = BRActivityManagerNative.get().gDefault();
        }
        return BRSingleton.get(iActivityManager).get();
    }

    /**
     * Replaces the system IActivityManager singleton with this proxy instance.
     * Handles API differences between Android Oreo+ and Lollipop+.
     *
     * @param base the original service invocation object (unused)
     * @param proxy the proxy invocation object to inject as the singleton
     */
    @Override
    protected void inject(Object base, Object proxy) {
        Object iActivityManager = null;
        if (BuildCompat.isOreo()) {
            iActivityManager = BRActivityManagerOreo.get().IActivityManagerSingleton();
        } else if (BuildCompat.isL()) {
            iActivityManager = BRActivityManagerNative.get().gDefault();
        }
        BRSingleton.get(iActivityManager)._set_mInstance(proxy);
    }

    /**
     * Checks if the proxy has been replaced by another instance.
     *
     * @return true if the current singleton does not match this proxy
     */
    @Override
    public boolean isBadEnv() {
        return getProxyInvocation() != getWho();
    }

    /**
     * Called after method hooks are bound. Adds additional package-aware proxies
     * for methods that require package name replacement.
     */
    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new PkgMethodProxy("getAppStartMode"));
        addMethodHook(new PkgMethodProxy("setAppLockedVerifying"));
        addMethodHook(new PkgMethodProxy("reportJunkFromApp"));
    }

    /**
     * Hook that intercepts {@code getContentProvider} to resolve content providers
     * within the virtual environment. Routes provider access through the virtual
     * package manager and activity manager, replacing authorities and user IDs
     * as needed to maintain virtual environment isolation.
     */
    @ProxyMethod("getContentProvider")
    public static class GetContentProvider extends MethodHook {
    /**
     * Hook that intercepts {@code getContentProvider} to resolve content providers
     * within the virtual environment.
     *
     * @param who the original object being hooked
     * @param method the method being intercepted
     * @param args the method arguments containing authority string, user ID, etc.
     * @return the resolved ContentProviderHolder, or null if provider not found
     * @throws Exception if the underlying method call fails
     */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Exception {
            int authIndex = getAuthIndex();
            Object auth = args[authIndex];
            Object content = null;

            if (auth instanceof String) {
                if (ProxyManifest.isProxy((String) auth)) {
                    return method.invoke(who, args);
                }

                if (BuildCompat.isQ()) {
                    args[1] = BlackBoxCore.getHostPkg();
                }

                if (auth.equals("settings") || auth.equals("media") || auth.equals("telephony")) {
                    content = method.invoke(who, args);
                    ContentProviderDelegate.update(content, (String) auth);
                    return content;
                } else {
                    Log.d(TAG, "hook getContentProvider: " + auth);

                    ProviderInfo providerInfo = BlackBoxCore.getBPackageManager().resolveContentProvider((String) auth, GET_META_DATA, BActivityThread.getUserId());
                    if (providerInfo == null) {
//                        Log.d(TAG, "hook system: " + auth);
//                        Object invoke = method.invoke(who, args);
//                        if (invoke != null) {
//                            Object provider = Reflector.with(invoke)
//                                    .field("provider")
//                                    .get();
//                            if (provider != null && !(provider instanceof Proxy)) {
//                                Reflector.with(invoke)
//                                        .field("provider")
//                                        .set(new SettingsProviderStub().wrapper((IInterface) provider, BlackBoxCore.getHostPkg()));
//                            }
//                        }
                        return null;
                    }

                    Log.d(TAG, "hook app: " + auth);
                    IBinder providerBinder = null;
                    if (BActivityThread.getAppPid() != -1) {
                        AppConfig appConfig = BlackBoxCore.getBActivityManager().initProcess(providerInfo.packageName, providerInfo.processName, BActivityThread.getUserId());
                        if (appConfig.bpid != BActivityThread.getAppPid()) {
                            providerBinder = BlackBoxCore.getBActivityManager().acquireContentProviderClient(providerInfo);
                        }
                        args[authIndex] = ProxyManifest.getProxyAuthorities(appConfig.bpid);
                        args[getUserIndex()] = BlackBoxCore.getHostUserId();
                    }
                    if (providerBinder == null)
                        return null;

                    content = method.invoke(who, args);
                    Reflector.with(content)
                            .field("info")
                            .set(providerInfo);
                    Reflector.with(content)
                            .field("provider")
                            .set(new ContentProviderStub().wrapper(BRContentProviderNative.get().asInterface(providerBinder), BActivityThread.getAppPackageName()));
                }

                return content;
            }
            return method.invoke(who, args);
        }

        private int getAuthIndex() {
            // 10.0
            if (BuildCompat.isQ()) {
                return 2;
            } else {
                return 1;
            }
        }

        private int getUserIndex() {
            return getAuthIndex() + 1;
        }
    }

    /**
     * Hook that intercepts {@code startService} to route service start requests
     * through the virtual activity manager when the target service is resolved
     * within the virtual environment.
     */
    @ProxyMethod("startService")
    public static class StartService extends MethodHook {
        /**
         * Resolves the service intent within the virtual environment and delegates
         * the start request to the virtual activity manager if found.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IApplicationThread, args[1] is the Intent, args[2] is resolvedType,
         *             args[3] is requireForeground (Android Oreo+)
         * @return the result component name from the virtual activity manager, or the original result
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[1];
            String resolvedType = (String) args[2];
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, 0, resolvedType, BActivityThread.getUserId());
            if (resolveInfo == null) {
                return method.invoke(who, args);
            }

            int requireForegroundIndex = getRequireForeground();
            boolean requireForeground = false;
            if (requireForegroundIndex != -1) {
                requireForeground = (boolean) args[requireForegroundIndex];
            }
            return BlackBoxCore.getBActivityManager().startService(intent, resolvedType, requireForeground, BActivityThread.getUserId());
        }

        /**
         * Returns the argument index for requireForeground based on Android version.
         *
         * @return the index of the requireForeground boolean, or -1 if not applicable
         */
        public int getRequireForeground() {
            if (BuildCompat.isOreo()) {
                return 3;
            }
            return -1;
        }
    }

    /**
     * Hook that intercepts {@code stopService} to route service stop requests
     * through the virtual activity manager.
     */
    @ProxyMethod("stopService")
    public static class StopService extends MethodHook {
        /**
         * Stops the service identified by the intent via the virtual activity manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IApplicationThread, args[1] is the Intent, args[2] is resolvedType
         * @return the result from the virtual activity manager's stopService call
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[1];
            String resolvedType = (String) args[2];
            return BlackBoxCore.getBActivityManager().stopService(intent, resolvedType, BActivityThread.getUserId());
        }
    }

    /**
     * Hook that intercepts {@code stopServiceToken} to stop a service by its
     * component name and token via the virtual activity manager.
     */
    @ProxyMethod("stopServiceToken")
    public static class StopServiceToken extends MethodHook {
        /**
         * Stops the service identified by the component name and token.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the ComponentName, args[1] is the IBinder token, args[2] is startId
         * @return always returns true
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            IBinder token = (IBinder) args[1];
            BlackBoxCore.getBActivityManager().stopServiceToken(componentName, token, BActivityThread.getUserId());
            return true;
        }
    }

    /**
     * Hook that intercepts {@code bindService} to route service binding through
     * the virtual activity manager. Creates a proxy service connection delegate
     * to intercept callbacks from the bound service.
     */
    @ProxyMethod("bindService")
    public static class BindService extends MethodHook {

        /**
         * Binds to a service within the virtual environment by resolving the intent,
         * creating a proxy connection delegate, and routing through the virtual activity manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is IApplicationThread, args[1] is IBinder, args[2] is the Intent,
         *             args[3] is resolvedType, args[4] is IServiceConnection, args[5] is flags,
         *             args[6] is callingPackage
         * @return the result from binding, or 0 if the service was not resolved
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[2];
            String resolvedType = (String) args[3];
            IServiceConnection connection = (IServiceConnection) args[4];

            int userId = intent.getIntExtra("_B_|_UserId", -1);
            userId = userId == -1 ? BActivityThread.getUserId() : userId;
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, 0, resolvedType, userId);
            if (resolveInfo != null || AppSystemEnv.isOpenPackage(intent.getComponent())) {
                Intent proxyIntent = BlackBoxCore.getBActivityManager().bindService(intent,
                        connection == null ? null : connection.asBinder(),
                        resolvedType,
                        userId);
                if (connection != null) {
                    if (intent.getComponent() == null && resolveInfo != null) {
                        intent.setComponent(new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name));
                    }
                    IServiceConnection proxy = ServiceConnectionDelegate.createProxy(connection, intent);
                    args[4] = proxy;

                    WeakReference<?> weakReference = BRLoadedApkServiceDispatcherInnerConnection.get(connection).mDispatcher();
                    if (weakReference != null) {
                        BRLoadedApkServiceDispatcher.get(weakReference.get())._set_mConnection(proxy);
                    }
                }
                if (proxyIntent != null) {
                    args[2] = proxyIntent;
                    return method.invoke(who, args);
                }
            }
            return 0;
        }

        /**
         * Checks whether this hook is active. Only enabled in black (virtual) or server processes.
         *
         * @return true if running in a virtual or server process
         */
        @Override
        protected boolean isEnable() {
            return BlackBoxCore.get().isBlackProcess() || BlackBoxCore.get().isServerProcess();
        }
    }

    /**
     * Hook that intercepts {@code bindIsolatedService} (Android 10+) to route
     * isolated service binding through the virtual activity manager. Clears the
     * instanceName parameter before delegating to {@link BindService}.
     */
    // 10.0
    @ProxyMethod("bindIsolatedService")
    public static class BindIsolatedService extends BindService {
        /**
         * Clears the instanceName argument and delegates to the parent BindService hook.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[6] (instanceName) is set to null
         * @return the result from the parent BindService hook
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object beforeHook(Object who, Method method, Object[] args) throws Throwable {
            // instanceName
            args[6] = null;
            return super.beforeHook(who, method, args);
        }
    }

    /**
     * Hook that intercepts {@code unbindService} to route service unbinding through
     * the virtual activity manager and replace the connection with the proxy delegate.
     */
    @ProxyMethod("unbindService")
    public static class UnbindService extends MethodHook {

        /**
         * Unbinds the service via the virtual activity manager and replaces the
         * IServiceConnection with its proxy delegate if one exists.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the IServiceConnection to unbind
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IServiceConnection iServiceConnection = (IServiceConnection) args[0];
            if (iServiceConnection == null) {
                return method.invoke(who, args);
            }
            BlackBoxCore.getBActivityManager().unbindService(iServiceConnection.asBinder(), BActivityThread.getUserId());
            ServiceConnectionDelegate delegate = ServiceConnectionDelegate.getDelegate(iServiceConnection.asBinder());
            if (delegate != null) {
                args[0] = delegate;
            }
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code getRunningAppProcesses} to return the virtual
     * app's running process information from the virtual activity manager.
     */
    @ProxyMethod("getRunningAppProcesses")
    public static class GetRunningAppProcesses extends MethodHook {

        /**
         * Returns the running process info list for the current virtual app.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return a list of RunningAppProcessInfo, or an empty list if none found
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            RunningAppProcessInfo runningAppProcesses = BActivityManager.get().getRunningAppProcesses(BActivityThread.getAppPackageName(), BActivityThread.getUserId());
            if (runningAppProcesses == null) {
                return new ArrayList<>();
            }
            return runningAppProcesses.mAppProcessInfoList;
        }
    }

    /**
     * Hook that intercepts {@code getServices} to return the virtual app's
     * running service information from the virtual activity manager.
     */
    @ProxyMethod("getServices")
    public static class GetServices extends MethodHook {

        /**
         * Returns the running service info list for the current virtual app.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return a list of RunningServiceInfo, or an empty list if none found
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            RunningServiceInfo runningServices = BActivityManager.get().getRunningServices(BActivityThread.getAppPackageName(), BActivityThread.getUserId());
            if (runningServices == null) {
                return new ArrayList<>();
            }
            return runningServices.mRunningServiceInfoList;
        }
    }

    /**
     * Hook that intercepts {@code getIntentSender} to create virtual intent senders.
     * Replaces activity-type intents with proxy pending activities and registers
     * the intent sender with the virtual activity manager.
     */
    @ProxyMethod("getIntentSender")
    public static class GetIntentSender extends MethodHook {
        /**
         * Creates a virtual intent sender by replacing intents with proxy pending
         * activity components and registering with the virtual activity manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is type, args[1] is callingPackage, args[2] is callingFeatureId (R+),
         *             args[3/4] is IBinder token, args[4/5] is resultCode, args[5/6] is data Intent,
         *             followed by Intent[] array
         * @return the IIntentSender interface from the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int type = (int) args[0];
            Intent[] intents = (Intent[]) args[getIntentsIndex(args)];
            MethodParameterUtils.replaceFirstAppPkg(args);

            for (int i = 0; i < intents.length; i++) {
                Intent intent = intents[i];
                switch (type) {
                    case ActivityManagerCompat.INTENT_SENDER_ACTIVITY:
                        Intent shadow = new Intent();
                        shadow.setComponent(new ComponentName(BlackBoxCore.getHostPkg(), ProxyManifest.getProxyPendingActivity(BActivityThread.getAppPid())));
                        ProxyPendingRecord.saveStub(shadow, intent, BActivityThread.getUserId());
                        intents[i] = shadow;
                        break;
                }
            }
            IInterface invoke = (IInterface) method.invoke(who, args);
            if (invoke != null) {
                String[] packagesForUid = BPackageManager.get().getPackagesForUid(BActivityThread.getCallingBUid());
                if (packagesForUid.length < 1) {
                    packagesForUid = new String[]{BlackBoxCore.getHostPkg()};
                }
                BlackBoxCore.getBActivityManager().getIntentSender(invoke.asBinder(), packagesForUid[0], BActivityThread.getCallingBUid());
            }
            return invoke;
        }

        /**
         * Finds the Intent[] array argument index in the method arguments.
         *
         * @param args the method arguments to search
         * @return the index of the Intent[] array, or a default based on Android version
         */
        private int getIntentsIndex(Object[] args) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent[]) {
                    return i;
                }
            }
            if (BuildCompat.isR()) {
                return 6;
            } else {
                return 5;
            }
        }
    }

    /**
     * Hook that intercepts {@code getPackageForIntentSender} to return the package
     * name associated with an intent sender from the virtual activity manager.
     */
    @ProxyMethod("getPackageForIntentSender")
    public static class getPackageForIntentSender extends MethodHook {
        /**
         * Returns the package name for the given intent sender binder.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the IIntentSender
         * @return the package name associated with the intent sender
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IInterface invoke = (IInterface) args[0];
            return BlackBoxCore.getBActivityManager().getPackageForIntentSender(invoke.asBinder());
        }
    }

    /**
     * Hook that intercepts {@code getUidForIntentSender} to return the UID
     * associated with an intent sender from the virtual activity manager.
     */
    @ProxyMethod("getUidForIntentSender")
    public static class getUidForIntentSender extends MethodHook {
        /**
         * Returns the UID for the given intent sender binder.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the IIntentSender
         * @return the UID associated with the intent sender
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IInterface invoke = (IInterface) args[0];
            return BlackBoxCore.getBActivityManager().getUidForIntentSender(invoke.asBinder());
        }
    }

    /**
     * Hook that intercepts {@code getIntentSenderWithSourceToken} by extending
     * {@link GetIntentSender}. Delegates to the parent implementation.
     */
    @ProxyMethod("getIntentSenderWithSourceToken")
    public static class GetIntentSenderWithSourceToken extends GetIntentSender {
    }

    /**
     * Hook that intercepts {@code getIntentSenderWithFeature} by extending
     * {@link GetIntentSender}. Delegates to the parent implementation.
     */
    @ProxyMethod("getIntentSenderWithFeature")
    public static class GetIntentSenderWithFeature extends GetIntentSender {
    }

    /**
     * Hook that intercepts {@code broadcastIntentWithFeature} by extending
     * {@link BroadcastIntent}. Delegates to the parent implementation.
     */
    @ProxyMethod("broadcastIntentWithFeature")
    public static class BroadcastIntentWithFeature extends BroadcastIntent {
    }

    /**
     * Hook that intercepts {@code broadcastIntent} to route broadcast dispatching
     * through the virtual activity manager. Replaces the intent with a proxy
     * broadcast and strips permission checks to allow delivery within the sandbox.
     */
    @ProxyMethod("broadcastIntent")
    public static class BroadcastIntent extends MethodHook {
        /**
         * Sends the broadcast through the virtual activity manager, replacing the
         * intent with a proxy broadcast record for proper routing.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments containing the Intent and resolved type
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int intentIndex = getIntentIndex(args);
            Intent intent = (Intent) args[intentIndex];
            String resolvedType = (String) args[intentIndex + 1];
            Intent proxyIntent = BlackBoxCore.getBActivityManager().sendBroadcast(intent, resolvedType, BActivityThread.getUserId());
            if (proxyIntent != null) {
                proxyIntent.setExtrasClassLoader(BActivityThread.getApplication().getClassLoader());
                ProxyBroadcastRecord.saveStub(proxyIntent, intent, BActivityThread.getUserId());
                args[intentIndex] = proxyIntent;
            }
            // ignore permission
            for (int i = 0; i < args.length; i++) {
                Object o = args[i];
                if (o instanceof String[]) {
                    args[i] = null;
                }
            }
            return method.invoke(who, args);
        }

        /**
         * Finds the Intent argument index in the method arguments.
         *
         * @param args the method arguments to search
         * @return the index of the Intent argument, defaulting to 1
         */
        int getIntentIndex(Object[] args) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Intent) {
                    return i;
                }
            }
            return 1;
        }
    }

    /**
     * Hook that intercepts {@code unregisterReceiver}. Passes through to the
     * original method without modification.
     */
    @ProxyMethod("unregisterReceiver")
    public static class unregisterReceiver extends MethodHook {

        /**
         * Delegates directly to the original unregisterReceiver method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code finishReceiver}. Passes through to the
     * original method without modification.
     */
    @ProxyMethod("finishReceiver")
    public static class finishReceiver extends MethodHook {

        /**
         * Delegates directly to the original finishReceiver method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code publishService}. Passes through to the
     * original method without modification.
     */
    @ProxyMethod("publishService")
    public static class PublishService extends MethodHook {

        /**
         * Delegates directly to the original publishService method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code peekService} to peek at a bound service's
     * binder through the virtual activity manager.
     */
    @ProxyMethod("peekService")
    public static class PeekService extends MethodHook {

        /**
         * Peeks at the service binder by resolving the intent within the virtual environment.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the Intent, args[1] is resolvedType, args[2] is callingPackage
         * @return the IBinder of the peeked service, or null
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            IBinder peek = BlackBoxCore.getBActivityManager().peekService(intent, resolvedType, BActivityThread.getUserId());
            return peek;
        }
    }

    /**
     * Hook that intercepts {@code sendIntentSender}. Currently returns 0 as a
     * placeholder to prevent intent sender execution in the virtual environment.
     */
    // todo
    @ProxyMethod("sendIntentSender")
    public static class SendIntentSender extends MethodHook {

        /**
         * Silently drops the intent sender request.
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
     * Hook that intercepts {@code registerReceiverWithFeature} (Android 10+) by
     * extending {@link RegisterReceiver}. Delegates to the parent implementation.
     */
    // android 10
    @ProxyMethod("registerReceiverWithFeature")
    public static class RegisterReceiverWithFeature extends RegisterReceiver {
    }

    /**
     * Hook that intercepts {@code registerReceiver} to wrap the broadcast receiver
     * with a proxy delegate and strip permission requirements for delivery within
     * the virtual sandbox.
     */
    @ProxyMethod("registerReceiver")
    public static class RegisterReceiver extends MethodHook {

        /**
         * Registers a broadcast receiver by replacing it with a proxy delegate and
         * removing permission restrictions.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments containing IIntentReceiver, IntentFilter, etc.
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            int receiverIndex = getReceiverIndex();
            if (args[receiverIndex] != null) {
                IIntentReceiver intentReceiver = (IIntentReceiver) args[receiverIndex];
                IIntentReceiver proxy = InnerReceiverDelegate.createProxy(intentReceiver);

                WeakReference<?> weakReference = BRLoadedApkReceiverDispatcherInnerReceiver.get(intentReceiver).mDispatcher();
                if (weakReference != null) {
                    BRLoadedApkReceiverDispatcher.get(weakReference.get())._set_mIIntentReceiver(proxy);
                }

                args[receiverIndex] = proxy;
            }
            // ignore permission
            if (args[getPermissionIndex()] != null) {
                args[getPermissionIndex()] = null;
            }
            return method.invoke(who, args);
        }

        /**
         * Returns the argument index for the IIntentReceiver based on Android version.
         *
         * @return the index of the receiver argument
         */
        public int getReceiverIndex() {
            if (BuildCompat.isS()) {
                return 4;
            } else if (BuildCompat.isR()) {
                return 3;
            }
            return 2;
        }

        /**
         * Returns the argument index for the permission string based on Android version.
         *
         * @return the index of the permission argument
         */
        public int getPermissionIndex() {
            if (BuildCompat.isS()) {
                return 6;
            } else if (BuildCompat.isR()) {
                return 5;
            }
            return 4;
        }
    }

    /**
     * Hook that intercepts {@code grantUriPermission} to replace the target UID
     * with the virtual environment's UID before delegation.
     */
    @ProxyMethod("grantUriPermission")
    public static class GrantUriPermission extends MethodHook {
        /**
         * Replaces the last UID argument with the virtual UID and delegates.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; the last UID is replaced
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUid(args);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code setServiceForeground}. Silently returns 0 to
     * prevent virtual apps from setting foreground service status directly.
     */
    @ProxyMethod("setServiceForeground")
    public static class setServiceForeground extends MethodHook {
        /**
         * Silently drops the setServiceForeground request.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//            if (args[0] instanceof ComponentName) {
//                args[0] = new ComponentName(BlackBoxCore.getHostPkg(), ProxyManifest.getProxyService(BActivityThread.getAppPid()));
//            }
//            return method.invoke(who, args);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code getHistoricalProcessExitReasons} to return
     * an empty list, preventing virtual apps from querying real process exit reasons.
     */
    @ProxyMethod("getHistoricalProcessExitReasons")
    public static class getHistoricalProcessExitReasons extends MethodHook {
        /**
         * Returns an empty ParceledListSlice to hide real process exit reasons.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return an empty ParceledListSlice
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return ParceledListSliceCompat.create(new ArrayList<>());
        }
    }

    /**
     * Hook that intercepts {@code getCurrentUser} to return a virtual UserInfo
     * object with the virtual environment's user ID.
     */
    @ProxyMethod("getCurrentUser")
    public static class getCurrentUser extends MethodHook {
        /**
         * Returns a UserInfo object representing the virtual user.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return a UserInfo with the virtual user ID and "BlackBox" name
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object blackBox = BRUserInfo.get()._new(BActivityThread.getUserId(), "BlackBox", BRUserInfo.get().FLAG_PRIMARY());
            return blackBox;
        }
    }

    /**
     * Hook that intercepts {@code checkPermission} to replace the target UID
     * and grant ACCOUNT_MANAGER and SEND_SMS permissions automatically.
     */
    @ProxyMethod("checkPermission")
    public static class checkPermission extends MethodHook {
        /**
         * Checks permission after replacing the UID. Grants ACCOUNT_MANAGER and
         * SEND_SMS permissions unconditionally; delegates others to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the permission string, args[1] is the PID, args[2] is the UID
         * @return {@link PackageManager#PERMISSION_GRANTED} for auto-granted permissions,
         *         otherwise the result from the original method
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUid(args);
            String permission = (String) args[0];
            if (permission.equals(Manifest.permission.ACCOUNT_MANAGER)
                    || permission.equals(Manifest.permission.SEND_SMS)) {
                return PackageManager.PERMISSION_GRANTED;
            }
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code checkUriPermission} to always grant URI
     * permissions within the virtual environment.
     */
    @ProxyMethod("checkUriPermission")
    public static class checkUriPermission extends MethodHook {
        /**
         * Always returns PERMISSION_GRANTED for URI permission checks.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns {@link PackageManager#PERMISSION_GRANTED}
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return PERMISSION_GRANTED;
        }
    }

    /**
     * Hook that intercepts {@code setTaskDescription} on Android versions below 10
     * to fix task description display in the recent apps list.
     */
    // for < Android 10
    @ProxyMethod("setTaskDescription")
    public static class SetTaskDescription extends MethodHook {
        /**
         * Fixes the TaskDescription to show correct app info in the recent apps list.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the IBinder token, args[1] is the ActivityManager.TaskDescription
         * @return the result of the original method invocation with the fixed TaskDescription
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ActivityManager.TaskDescription td = (ActivityManager.TaskDescription) args[1];
            args[1] = TaskDescriptionCompat.fix(td);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code setRequestedOrientation} to catch and suppress
     * exceptions that may occur when setting orientation in the virtual environment.
     */
    @ProxyMethod("setRequestedOrientation")
    public static class setRequestedOrientation extends MethodHook {

        /**
         * Attempts to set the requested orientation, catching and logging any exceptions.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args args[0] is the IBinder token, args[1] is the orientation integer
         * @return the result of the original method, or 0 on failure
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(who, args);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code registerUidObserver} to silently ignore
     * UID observer registration in the virtual environment.
     */
    @ProxyMethod("registerUidObserver")
    public static class registerUidObserver extends MethodHook {

        /**
         * Silently drops the registerUidObserver request.
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
     * Hook that intercepts {@code unregisterUidObserver} to silently ignore
     * UID observer unregistration in the virtual environment.
     */
    @ProxyMethod("unregisterUidObserver")
    public static class unregisterUidObserver extends MethodHook {

        /**
         * Silently drops the unregisterUidObserver request.
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
     * Hook that intercepts {@code updateConfiguration} to silently ignore
     * configuration update requests from virtual apps.
     */
    @ProxyMethod("updateConfiguration")
    public static class updateConfiguration extends MethodHook {

        /**
         * Silently drops the updateConfiguration request.
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
}
