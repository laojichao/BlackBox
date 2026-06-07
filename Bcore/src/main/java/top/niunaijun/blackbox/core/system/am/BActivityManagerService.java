package top.niunaijun.blackbox.core.system.am;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.system.BProcessManagerService;
import top.niunaijun.blackbox.core.system.ISystemService;
import top.niunaijun.blackbox.core.system.ProcessRecord;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.entity.AppConfig;
import top.niunaijun.blackbox.entity.UnbindRecord;
import top.niunaijun.blackbox.entity.am.PendingResultData;
import top.niunaijun.blackbox.entity.am.ReceiverData;
import top.niunaijun.blackbox.entity.am.RunningAppProcessInfo;
import top.niunaijun.blackbox.entity.am.RunningServiceInfo;
import top.niunaijun.blackbox.utils.Slog;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * Virtual implementation of the Android {@link android.app.ActivityManager} system service.
 * Central coordinator for activity, service, broadcast, and content provider operations
 * within the virtual environment. Delegates to per-user {@link UserSpace} instances that
 * contain independent {@link ActivityStack} and {@link ActiveServices} managers.
 *
 * <p>Implements {@link ISystemService} for lifecycle management. Handles process initialization,
 * activity lifecycle callbacks, service binding/unbinding, broadcast dispatching, intent sender
 * tracking, and caller identity resolution -- all scoped to virtual user IDs.</p>
 */
public class BActivityManagerService extends IBActivityManagerService.Stub implements ISystemService {
    public static final String TAG = "BActivityManagerService";
    private static final BActivityManagerService sService = new BActivityManagerService();
    private final Map<Integer, UserSpace> mUserSpace = new HashMap<>();
    private final BPackageManagerService mPms = BPackageManagerService.get();
    private final BroadcastManager mBroadcastManager;

    /**
     * Returns the singleton instance of this service.
     *
     * @return the global BActivityManagerService instance
     */
    public static BActivityManagerService get() {
        return sService;
    }

    /**
     * Constructs the service, initializing the broadcast manager with this service
     * and the package manager.
     */
    public BActivityManagerService() {
        mBroadcastManager = BroadcastManager.startSystem(this, mPms);
    }

    /**
     * Starts a service in the virtual environment for the given user.
     *
     * @param intent             the intent identifying the service
     * @param resolvedType       the MIME type of the intent
     * @param requireForeground whether to start as a foreground service
     * @param userId             the virtual user ID
     * @return always null (service component name not tracked)
     */
    @Override
    public ComponentName startService(Intent intent, String resolvedType, boolean requireForeground, int userId) {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mActiveServices) {
            userSpace.mActiveServices.startService(intent, resolvedType, requireForeground, userId);
        }
        return null;
    }

    /**
     * Acquires a content provider client for the given provider info. Starts the hosting
     * process if necessary and delegates to the virtual process's activity thread.
     *
     * @param providerInfo the content provider to acquire
     * @return the IBinder of the content provider client
     * @throws RemoteException if the remote call fails
     */
    @Override
    public IBinder acquireContentProviderClient(ProviderInfo providerInfo) throws RemoteException {
        int callingPid = Binder.getCallingPid();
        ProcessRecord processRecord = BProcessManagerService.get().startProcessLocked(providerInfo.packageName,
                providerInfo.processName,
                BProcessManagerService.get().getUserIdByCallingPid(callingPid),
                -1,
                Binder.getCallingPid());
        if (processRecord == null) {
            throw new RuntimeException("Unable to create process " + providerInfo.name);
        }
        try {
            return processRecord.bActivityThread.acquireContentProviderClient(providerInfo);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    /**
     * Sends a broadcast intent to all matching receivers in the virtual environment.
     * Ensures receiver processes are started and bound, then returns a shadow intent
     * scoped to the host package.
     *
     * @param intent       the broadcast intent
     * @param resolvedType the MIME type of the intent
     * @param userId       the virtual user ID
     * @return a shadow intent with the original action, scoped to the host package
     * @throws RemoteException if the remote call fails
     */
    @Override
    public Intent sendBroadcast(Intent intent, String resolvedType, int userId) throws RemoteException {
        List<ResolveInfo> resolves = BPackageManagerService.get().queryBroadcastReceivers(intent, GET_META_DATA, resolvedType, userId);

        for (ResolveInfo resolve : resolves) {
            ProcessRecord processRecord = BProcessManagerService.get().findProcessRecord(resolve.activityInfo.packageName, resolve.activityInfo.processName, userId);
            if (processRecord == null) {
                continue;
            }
            try {
                processRecord.bActivityThread.bindApplication();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        Intent shadow = new Intent();
        shadow.setPackage(BlackBoxCore.getHostPkg());
        shadow.setComponent(null);
        shadow.setAction(intent.getAction());
        return shadow;
    }

    /**
     * Returns the IBinder of a running service without binding to it.
     *
     * @param intent       the intent identifying the service
     * @param resolvedType the MIME type of the intent
     * @param userId       the virtual user ID
     * @return the service's IBinder, or null if not running
     * @throws RemoteException if the remote call fails
     */
    @Override
    public IBinder peekService(Intent intent, String resolvedType, int userId) throws RemoteException {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mActiveServices) {
            return userSpace.mActiveServices.peekService(intent, resolvedType, userId);
        }
    }

    /**
     * Callback invoked when a virtual activity is created. Delegates to the user's activity stack.
     *
     * @param taskId         the Android task ID
     * @param token          the IBinder token of the created activity
     * @param activityRecord the {@link ActivityRecord} passed as an IBinder
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void onActivityCreated(int taskId, IBinder token, IBinder activityRecord) throws RemoteException {
        int callingPid = Binder.getCallingPid();
        ProcessRecord process = BProcessManagerService.get().findProcessByPid(callingPid);
        if (process == null) {
            return;
        }
        ActivityRecord record = (ActivityRecord) activityRecord;
        UserSpace userSpace = getOrCreateSpaceLocked(process.userId);
        synchronized (userSpace.mStack) {
            userSpace.mStack.onActivityCreated(process, taskId, token, record);
        }
    }

    /**
     * Callback invoked when a virtual activity is resumed.
     *
     * @param token the IBinder token of the resumed activity
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void onActivityResumed(IBinder token) throws RemoteException {
        int callingPid = Binder.getCallingPid();
        ProcessRecord process = BProcessManagerService.get().findProcessByPid(callingPid);
        if (process == null) {
            return;
        }
        UserSpace userSpace = getOrCreateSpaceLocked(process.userId);
        synchronized (userSpace.mStack) {
            userSpace.mStack.onActivityResumed(process.userId, token);
        }
    }

    /**
     * Callback invoked when a virtual activity is destroyed.
     *
     * @param token the IBinder token of the destroyed activity
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void onActivityDestroyed(IBinder token) throws RemoteException {
        int callingPid = Binder.getCallingPid();
        ProcessRecord process = BProcessManagerService.get().findProcessByPid(callingPid);
        if (process == null) {
            return;
        }
        UserSpace userSpace = getOrCreateSpaceLocked(process.userId);
        synchronized (userSpace.mStack) {
            userSpace.mStack.onActivityDestroyed(process.userId, token);
        }
    }

    /**
     * Callback invoked when a virtual activity finishes.
     *
     * @param token the IBinder token of the finishing activity
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void onFinishActivity(IBinder token) throws RemoteException {
        int callingPid = Binder.getCallingPid();
        ProcessRecord process = BProcessManagerService.get().findProcessByPid(callingPid);
        if (process == null) {
            return;
        }
        UserSpace userSpace = getOrCreateSpaceLocked(process.userId);
        synchronized (userSpace.mStack) {
            userSpace.mStack.onFinishActivity(process.userId, token);
        }
    }

    /**
     * Returns running process information for the given virtual package. Correlates
     * virtual process records with the host system's process list.
     *
     * @param callerPackage the package name to query
     * @param userId        the virtual user ID
     * @return a {@link RunningAppProcessInfo} containing process details
     * @throws RemoteException if the remote call fails
     */
    @Override
    public RunningAppProcessInfo getRunningAppProcesses(String callerPackage, int userId) throws RemoteException {
        ActivityManager manager = (ActivityManager)
                BlackBoxCore.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
        Map<Integer, ActivityManager.RunningAppProcessInfo> runningProcessMap = new HashMap<>();
        for (ActivityManager.RunningAppProcessInfo runningProcess : runningAppProcesses) {
            runningProcessMap.put(runningProcess.pid, runningProcess);
        }
        List<ProcessRecord> packageProcessAsUser = BProcessManagerService.get().getPackageProcessAsUser(callerPackage, userId);

        RunningAppProcessInfo appProcessInfo = new RunningAppProcessInfo();
        for (ProcessRecord processRecord : packageProcessAsUser) {
            ActivityManager.RunningAppProcessInfo runningAppProcessInfo = runningProcessMap.get(processRecord.pid);
            if (runningAppProcessInfo != null) {
                runningAppProcessInfo.processName = processRecord.processName;
                appProcessInfo.mAppProcessInfoList.add(runningAppProcessInfo);
            }
        }
        return appProcessInfo;
    }

    /**
     * Returns running service information for the given virtual package.
     *
     * @param callerPackage the package name to query
     * @param userId        the virtual user ID
     * @return a {@link RunningServiceInfo} containing service details
     * @throws RemoteException if the remote call fails
     */
    @Override
    public RunningServiceInfo getRunningServices(String callerPackage, int userId) throws RemoteException {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mActiveServices) {
            return userSpace.mActiveServices.getRunningServiceInfo(callerPackage, userId);
        }
    }

    /**
     * Schedules delivery of a broadcast to registered receivers in the virtual environment.
     * Delegates to the {@link BroadcastManager} for timeout handling.
     *
     * @param intent            the broadcast intent
     * @param pendingResultData the pending result data for the broadcast
     * @param userId            the virtual user ID
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void scheduleBroadcastReceiver(Intent intent, PendingResultData pendingResultData, int userId) throws RemoteException {
        List<ResolveInfo> resolves = BPackageManagerService.get().queryBroadcastReceivers(intent, GET_META_DATA, null, userId);

        if (resolves.isEmpty()) {
            pendingResultData.build().finish();
            Slog.d(TAG, "scheduleBroadcastReceiver empty");
            return;
        }
        mBroadcastManager.sendBroadcast(pendingResultData);
        for (ResolveInfo resolve : resolves) {
            ProcessRecord processRecord = BProcessManagerService.get().findProcessRecord(resolve.activityInfo.packageName, resolve.activityInfo.processName, userId);
            if (processRecord != null) {
                ReceiverData data = new ReceiverData();
                data.intent = intent;
                data.activityInfo = resolve.activityInfo;
                data.data = pendingResultData;
                processRecord.bActivityThread.scheduleReceiver(data);
            }
        }
    }

    /**
     * Signals that a broadcast has finished processing.
     *
     * @param data the pending result data of the completed broadcast
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void finishBroadcast(PendingResultData data) throws RemoteException {
        mBroadcastManager.finishBroadcast(data);
    }

    /**
     * Returns the package name of the activity that started the activity identified by the given token.
     *
     * @param token  the IBinder token of the target activity
     * @param userId the virtual user ID
     * @return the calling package name
     * @throws RemoteException if the remote call fails
     */
    @Override
    public String getCallingPackage(IBinder token, int userId) throws RemoteException {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mStack) {
            return userSpace.mStack.getCallingPackage(token, userId);
        }
    }

    /**
     * Returns the component name of the activity that started the activity identified by the given token.
     *
     * @param token  the IBinder token of the target activity
     * @param userId the virtual user ID
     * @return the calling activity's component name
     * @throws RemoteException if the remote call fails
     */
    @Override
    public ComponentName getCallingActivity(IBinder token, int userId) throws RemoteException {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mStack) {
            return userSpace.mStack.getCallingActivity(token, userId);
        }
    }

    /**
     * Registers a pending intent sender record for the given target binder.
     *
     * @param target      the IBinder token of the intent sender
     * @param packageName the package that owns the intent sender
     * @param uid         the UID of the calling process
     * @param userId      the virtual user ID
     */
    @Override
    public void getIntentSender(IBinder target, String packageName, int uid, int userId) {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mIntentSenderRecords) {
            PendingIntentRecord record = new PendingIntentRecord();
            record.uid = uid;
            record.packageName = packageName;
            userSpace.mIntentSenderRecords.put(target, record);
        }
    }

    /**
     * Returns the package name associated with a pending intent sender.
     *
     * @param target the IBinder token of the intent sender
     * @param userId the virtual user ID
     * @return the package name, or null if not found
     * @throws RemoteException if the remote call fails
     */
    @Override
    public String getPackageForIntentSender(IBinder target, int userId) throws RemoteException {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mIntentSenderRecords) {
            PendingIntentRecord record = userSpace.mIntentSenderRecords.get(target);
            if (record != null) {
                return record.packageName;
            }
        }
        return null;
    }

    /**
     * Returns the UID associated with a pending intent sender.
     *
     * @param target the IBinder token of the intent sender
     * @param userId the virtual user ID
     * @return the UID, or -1 if not found
     * @throws RemoteException if the remote call fails
     */
    @Override
    public int getUidForIntentSender(IBinder target, int userId) throws RemoteException {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mIntentSenderRecords) {
            PendingIntentRecord record = userSpace.mIntentSenderRecords.get(target);
            if (record != null) {
                return record.uid;
            }
        }
        return -1;
    }

    /**
     * Callback when a proxy service receives onStartCommand.
     *
     * @param intent the proxy intent
     * @param userId the virtual user ID
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void onStartCommand(Intent intent, int userId) throws RemoteException {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mActiveServices) {
            userSpace.mActiveServices.onStartCommand(intent, userId);
        }
    }

    /**
     * Callback when a service binding is released from the proxy side.
     *
     * @param proxyIntent the proxy intent identifying the service
     * @param userId      the virtual user ID
     * @return the unbind record with service state, or null if not found
     * @throws RemoteException if the remote call fails
     */
    @Override
    public UnbindRecord onServiceUnbind(Intent proxyIntent, int userId) throws RemoteException {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mActiveServices) {
            return userSpace.mActiveServices.onServiceUnbind(proxyIntent, userId);
        }
    }

    /**
     * Callback when a proxy service is destroyed.
     *
     * @param proxyIntent the proxy intent identifying the service
     * @param userId      the virtual user ID
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void onServiceDestroy(Intent proxyIntent, int userId) throws RemoteException {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mActiveServices) {
            userSpace.mActiveServices.onServiceDestroy(proxyIntent, userId);
        }
    }

    /**
     * Stops a service in the virtual environment.
     *
     * @param intent       the intent identifying the service
     * @param resolvedType the MIME type of the intent
     * @param userId       the virtual user ID
     * @return 0 in all cases
     */
    @Override
    public int stopService(Intent intent, String resolvedType, int userId) {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mActiveServices) {
            return userSpace.mActiveServices.stopService(intent, resolvedType, userId);
        }
    }

    /**
     * Binds to a service in the virtual environment.
     *
     * @param service      the intent identifying the service
     * @param binder       the caller's IBinder for connection tracking
     * @param resolvedType the MIME type of the intent
     * @param userId       the virtual user ID
     * @return the proxy stub intent for the bound service
     * @throws RemoteException if the remote call fails
     */
    @Override
    public Intent bindService(Intent service, IBinder binder, String resolvedType, int userId) throws RemoteException {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mActiveServices) {
            return userSpace.mActiveServices.bindService(service, binder, resolvedType, userId);
        }
    }

    /**
     * Unbinds a service connection in the virtual environment.
     *
     * @param binder the caller's IBinder used during bind
     * @param userId the virtual user ID
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void unbindService(IBinder binder, int userId) throws RemoteException {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mActiveServices) {
            userSpace.mActiveServices.unbindService(binder, userId);
        }
    }

    /**
     * Stops a service identified by its component name and token.
     *
     * @param className the component name of the service
     * @param token     the service's IBinder token
     * @param userId    the virtual user ID
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void stopServiceToken(ComponentName className, IBinder token, int userId) throws RemoteException {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mActiveServices) {
            userSpace.mActiveServices.stopServiceToken(className, token, userId);
        }
    }

    /**
     * Initializes a virtual process for the given package. Starts the process if not already running.
     *
     * @param packageName the package name to initialize
     * @param processName the process name within the package
     * @param userId      the virtual user ID
     * @return the {@link AppConfig} for the initialized process, or null on failure
     * @throws RemoteException if the remote call fails
     */
    @Override
    public AppConfig initProcess(String packageName, String processName, int userId) throws RemoteException {
        ProcessRecord processRecord = BProcessManagerService.get().startProcessLocked(packageName, processName, userId, -1, Binder.getCallingPid());
        if (processRecord == null)
            return null;
        return processRecord.getClientConfig();
    }

    /**
     * Restarts a virtual app process.
     *
     * @param packageName the package name
     * @param processName the process name
     * @param userId      the virtual user ID
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void restartProcess(String packageName, String processName, int userId) throws RemoteException {
        BProcessManagerService.get().restartAppProcess(packageName, processName, userId);
    }

    /**
     * Starts an activity in the virtual environment using the basic intent-only API.
     *
     * @param intent the intent to start
     * @param userId the virtual user ID
     */
    @Override
    public void startActivity(Intent intent, int userId) {
        UserSpace userSpace = getOrCreateSpaceLocked(userId);
        synchronized (userSpace.mStack) {
            userSpace.mStack.startActivityLocked(userId, intent, null, null, null, -1, -1, null);
        }
    }

    /**
     * Starts an activity with full AMS parameters in the virtual environment.
     *
     * @param userId       the virtual user ID
     * @param intent       the intent to start
     * @param resolvedType the MIME type of the intent
     * @param resultTo     the token of the calling activity; may be null
     * @param resultWho    the identifier for the result recipient
     * @param requestCode  the request code for result delivery; -1 if not used
     * @param flags        additional start flags
     * @param options      activity options bundle; may be null
     * @return 0 on success
     * @throws RemoteException if the remote call fails
     */
    @Override
    public int startActivityAms(int userId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, Bundle options) throws RemoteException {
        UserSpace space = getOrCreateSpaceLocked(userId);
        synchronized (space.mStack) {
            return space.mStack.startActivityLocked(userId, intent, resolvedType, resultTo, resultWho, requestCode, flags, options);
        }
    }

    /**
     * Starts multiple activities in sequence in the virtual environment.
     *
     * @param userId       the virtual user ID
     * @param intent       array of intents to start
     * @param resolvedType array of MIME types for each intent
     * @param resultTo     the token of the calling activity; may be null
     * @param options      activity options bundle; may be null
     * @return 0 on success
     * @throws RemoteException if the remote call fails
     */
    @Override
    public int startActivities(int userId, Intent[] intent, String[] resolvedType, IBinder resultTo, Bundle options) throws RemoteException {
        UserSpace space = getOrCreateSpaceLocked(userId);
        synchronized (space.mStack) {
            return space.mStack.startActivitiesLocked(userId, intent, resolvedType, resultTo, options);
        }
    }

    private UserSpace getOrCreateSpaceLocked(int userId) {
        synchronized (mUserSpace) {
            UserSpace userSpace = mUserSpace.get(userId);
            if (userSpace != null)
                return userSpace;
            userSpace = new UserSpace();
            mUserSpace.put(userId, userSpace);
            return userSpace;
        }
    }

    /**
     * Initializes the broadcast manager on system ready.
     */
    @Override
    public void systemReady() {
        mBroadcastManager.startup();
    }
}
