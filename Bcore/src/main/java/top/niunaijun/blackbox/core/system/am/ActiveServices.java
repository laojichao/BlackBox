package top.niunaijun.blackbox.core.system.am;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.IEmpty;
import top.niunaijun.blackbox.core.system.BProcessManagerService;
import top.niunaijun.blackbox.core.system.ProcessRecord;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.entity.UnbindRecord;
import top.niunaijun.blackbox.entity.am.RunningServiceInfo;
import top.niunaijun.blackbox.proxy.ProxyManifest;
import top.niunaijun.blackbox.proxy.record.ProxyServiceRecord;

/**
 * Virtual implementation of the Android {@link android.app.ActivityManager} service management.
 * Manages the lifecycle of started and bound services within the virtual environment, including
 * starting, stopping, binding, and unbinding services.
 *
 * <p>Intercepts real Android service intents and redirects them through proxy stub services
 * ({@link top.niunaijun.blackbox.proxy.ProxyManifest}) so that virtual app services execute
 * inside the host process. Tracks running service state via {@link RunningServiceRecord} and
 * connected (bound) service state via {@link ConnectedServiceRecord}.</p>
 */
@SuppressLint("NewApi")
public class ActiveServices {
    public static final String TAG = "ActiveServices";

    private final Map<Intent.FilterComparison, RunningServiceRecord> mRunningServiceRecords = new HashMap<>();
    private final Map<IBinder, RunningServiceRecord> mRunningTokens = new HashMap<>();
    private final Map<IBinder, ConnectedServiceRecord> mConnectedServices = new HashMap<>();

    /**
     * Starts a service within the virtual environment. Resolves the target service, starts
     * the hosting process if needed, and launches a proxy stub service in the host.
     *
     * @param intent             the intent identifying the service to start
     * @param resolvedType       the MIME type of the intent
     * @param requireForeground whether to start as a foreground service
     * @param userId             the virtual user ID
     */
    public void startService(Intent intent, String resolvedType, boolean requireForeground, int userId) {
        ResolveInfo resolveInfo = resolveService(intent, resolvedType, userId);
        if (resolveInfo == null)
            return;
//            throw new RuntimeException("resolveService service exception");
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        ProcessRecord processRecord = BProcessManagerService.get().startProcessLocked(serviceInfo.packageName, serviceInfo.processName, userId, -1, Binder.getCallingPid());
        if (processRecord == null) {
            throw new RuntimeException("Unable to create " + serviceInfo.name);
        }
        RunningServiceRecord runningServiceRecord = getOrCreateRunningServiceRecord(intent);
        runningServiceRecord.mServiceInfo = serviceInfo;

        runningServiceRecord.getAndIncrementStartId();
        final Intent stubServiceIntent = createStubServiceIntent(intent, serviceInfo, processRecord, runningServiceRecord);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BlackBoxCore.getContext().startService(stubServiceIntent);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Stops a previously started service. If the service still has active bindings, it is not stopped.
     *
     * @param intent       the intent identifying the service to stop
     * @param resolvedType the MIME type of the intent
     * @param userId       the virtual user ID
     * @return 0 in all cases (matching Android API contract)
     */
    public int stopService(Intent intent, String resolvedType, int userId) {
//        ResolveInfo resolveInfo = resolveService(intent, resolvedType, userId);
        synchronized (mRunningServiceRecords) {
            RunningServiceRecord runningServiceRecord = findRunningServiceRecord(intent);
            if (runningServiceRecord == null) {
                return 0;
            }
            if (runningServiceRecord.mBindCount.get() > 0) {
                Log.d(TAG, "There are also connections");
                return 0;
            }
            runningServiceRecord.mStartId.set(0);
            ResolveInfo resolveInfo = resolveService(intent, resolvedType, userId);
            if (resolveInfo == null)
                return 0;
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            ProcessRecord processRecord = BProcessManagerService.get().startProcessLocked(serviceInfo.packageName, serviceInfo.processName, userId, -1, Binder.getCallingPid());
            if (processRecord == null) {
                return 0;
            }
            try {
                processRecord.bActivityThread.stopService(intent);
            } catch (RemoteException ignored) {
            }
        }
        return 0;
    }

    /**
     * Binds to a service within the virtual environment. Tracks connection state and returns
     * a proxy stub intent that the caller uses to interact with the real service.
     *
     * @param intent       the intent identifying the service to bind
     * @param binder       the caller's IBinder for death tracking; may be null
     * @param resolvedType the MIME type of the intent
     * @param userId       the virtual user ID
     * @return the proxy stub intent for the bound service
     */
    public Intent bindService(Intent intent, final IBinder binder, String resolvedType, int userId) {
        ResolveInfo resolveInfo = resolveService(intent, resolvedType, userId);
        if (resolveInfo == null)
            return intent;
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        ProcessRecord processRecord = BProcessManagerService.get().startProcessLocked(
                serviceInfo.packageName,
                serviceInfo.processName,
                userId,
                -1, Binder.getCallingPid());

        if (processRecord == null) {
            throw new RuntimeException("Unable to create " + serviceInfo.name);
        }

        RunningServiceRecord runningServiceRecord;
        synchronized (mRunningServiceRecords) {
            runningServiceRecord = getOrCreateRunningServiceRecord(intent);
            runningServiceRecord.mServiceInfo = serviceInfo;

            if (binder != null) {
                ConnectedServiceRecord connectedService = mConnectedServices.get(binder);
                boolean isBound = false;
                if (connectedService != null) {
                    isBound = true;
                } else {
                    connectedService = new ConnectedServiceRecord();
                    try {
                        binder.linkToDeath(new IBinder.DeathRecipient() {
                            @Override
                            public void binderDied() {
                                binder.unlinkToDeath(this, 0);
                                mConnectedServices.remove(binder);
                            }
                        }, 0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    connectedService.mIBinder = binder;
                    connectedService.mIntent = intent;
                    mConnectedServices.put(binder, connectedService);
                }

                if (!isBound) {
                    runningServiceRecord.incrementBindCountAndGet();
                }
                runningServiceRecord.mConnectedServiceRecord = connectedService;
            }
        }
        return createStubServiceIntent(intent, serviceInfo, processRecord, runningServiceRecord);
    }

    /**
     * Unbinds a previously bound service by removing the connection record.
     *
     * @param binder the caller's IBinder that was used during bind
     * @param userId the virtual user ID
     */
    public void unbindService(IBinder binder, int userId) {
        ConnectedServiceRecord connectedService = mConnectedServices.get(binder);
        if (connectedService == null) {
            return;
        }
        RunningServiceRecord runningServiceRecord = getOrCreateRunningServiceRecord(connectedService.mIntent);
        runningServiceRecord.mConnectedServiceRecord = null;
        runningServiceRecord.mBindCount.decrementAndGet();
        mConnectedServices.remove(binder);
    }

    /**
     * Stops a service identified by its component name and token.
     *
     * @param className the component name of the service
     * @param token     the service's IBinder token
     * @param userId    the virtual user ID
     */
    public void stopServiceToken(ComponentName className, IBinder token, int userId) {
        RunningServiceRecord runningServiceByToken = findRunningServiceByToken(token);
        if (runningServiceByToken != null) {
            stopService(runningServiceByToken.mIntent, null, userId);
        }
    }

    /**
     * Callback invoked when a proxy service receives onStartCommand.
     *
     * @param proxyIntent the proxy intent delivered to the stub service
     * @param userId      the virtual user ID
     */
    public void onStartCommand(Intent proxyIntent, int userId) {
    }

    /**
     * Callback invoked when a proxy service is destroyed. Cleans up the running service record.
     *
     * @param proxyIntent the proxy intent delivered to the stub service
     * @param userId      the virtual user ID
     */
    public void onServiceDestroy(Intent proxyIntent, int userId) {
        if (proxyIntent == null)
            return;
        ProxyServiceRecord proxyServiceRecord = ProxyServiceRecord.create(proxyIntent);
        if (proxyServiceRecord.mServiceIntent != null) {
            proxyIntent = proxyServiceRecord.mServiceIntent;
        }
        RunningServiceRecord remove = mRunningServiceRecords.remove(new Intent.FilterComparison(proxyIntent));
        if (remove != null) {
            mRunningTokens.remove(remove);
        }
    }

    /**
     * Callback invoked when a bound service connection is unbound from the proxy side.
     * Returns an {@link UnbindRecord} containing the current bind count and start ID.
     *
     * @param proxyIntent the proxy intent identifying the service
     * @param userId      the virtual user ID
     * @return the unbind record with service state, or null if the service is not found
     * @throws RemoteException if the remote call fails
     */
    public UnbindRecord onServiceUnbind(Intent proxyIntent, int userId) throws RemoteException {
        if (proxyIntent == null)
            return null;
        ProxyServiceRecord proxyServiceRecord = ProxyServiceRecord.create(proxyIntent);
        ComponentName component = proxyServiceRecord.mServiceIntent.getComponent();

        RunningServiceRecord runningServiceRecord = findRunningServiceRecord(proxyServiceRecord.mServiceIntent);
        if (runningServiceRecord == null)
            return null;
        UnbindRecord record = new UnbindRecord();
        record.setComponentName(component);
        record.setBindCount(runningServiceRecord.mBindCount.get());
        record.setStartId(runningServiceRecord.mStartId.get());
        return record;
    }

    private Intent createStubServiceIntent(Intent targetIntent, ServiceInfo serviceInfo, ProcessRecord processRecord, RunningServiceRecord runningServiceRecord) {
        Intent stub = new Intent();
        ComponentName stubComp = new ComponentName(BlackBoxCore.getHostPkg(), ProxyManifest.getProxyService(processRecord.bpid));
        stub.setComponent(stubComp);
        stub.setAction(UUID.randomUUID().toString());
        ProxyServiceRecord.saveStub(stub, targetIntent, serviceInfo, runningServiceRecord, processRecord.userId, runningServiceRecord.mStartId.get());
        return stub;
    }

    private RunningServiceRecord getOrCreateRunningServiceRecord(Intent intent) {
        RunningServiceRecord runningServiceRecord = findRunningServiceRecord(intent);
        if (runningServiceRecord == null) {
            runningServiceRecord = new RunningServiceRecord();
            runningServiceRecord.mIntent = intent;
            mRunningServiceRecords.put(new Intent.FilterComparison(intent), runningServiceRecord);
            mRunningTokens.put(runningServiceRecord, runningServiceRecord);
        }
        return runningServiceRecord;
    }

    private RunningServiceRecord findRunningServiceRecord(Intent intent) {
        return mRunningServiceRecords.get(new Intent.FilterComparison(intent));
    }

    private RunningServiceRecord findRunningServiceByToken(IBinder token) {
        return mRunningTokens.get(token);
    }

    /**
     * Retrieves information about currently running virtual services for the given package.
     * Correlates virtual service records with the host system's running service info.
     *
     * @param callerPackage the package name to query services for
     * @param userId        the virtual user ID
     * @return a {@link RunningServiceInfo} containing the list of running services
     */
    public RunningServiceInfo getRunningServiceInfo(String callerPackage, int userId) {
        ActivityManager manager = (ActivityManager)
                BlackBoxCore.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        Map<Integer, ActivityManager.RunningServiceInfo> serviceInfoMap = new HashMap<>();
        for (ActivityManager.RunningServiceInfo runningService : runningServices) {
            serviceInfoMap.put(runningService.pid, runningService);
        }

        RunningServiceInfo info = new RunningServiceInfo();
        for (RunningServiceRecord value : mRunningServiceRecords.values()) {
            ServiceInfo serviceInfo = value.mServiceInfo;
            ProcessRecord processRecord = BProcessManagerService.get().findProcessRecord(callerPackage, serviceInfo.processName, userId);
            if (processRecord == null)
                continue;
            ActivityManager.RunningServiceInfo runningServiceInfo = serviceInfoMap.get(processRecord.pid);
            if (runningServiceInfo != null) {
                runningServiceInfo.process = processRecord.processName;
                runningServiceInfo.service = new ComponentName(serviceInfo.packageName, serviceInfo.name);
                info.mRunningServiceInfoList.add(runningServiceInfo);
            }
        }
        return info;
    }

    /**
     * Returns the IBinder of a running service without binding to it.
     *
     * @param intent       the intent identifying the service
     * @param resolvedType the MIME type of the intent
     * @param userId       the virtual user ID
     * @return the service's IBinder, or null if not running
     */
    public IBinder peekService(Intent intent, String resolvedType, int userId) {
        ResolveInfo resolveInfo = resolveService(intent, resolvedType, userId);
        if (resolveInfo == null)
            return null;
        ProcessRecord processRecord =
                BProcessManagerService.get().findProcessRecord(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.processName, userId);
        if (processRecord == null)
            return null;
        try {
            return processRecord.bActivityThread.peekService(intent);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ResolveInfo resolveService(Intent intent, String resolvedType, int userId) {
        return BPackageManagerService.get().resolveService(intent, 0, resolvedType, userId);
    }

    /**
     * Tracks the state of a single running service within the virtual environment.
     * Holds the service info, intent, start ID counter, and bind count.
     * Extends {@link IEmpty.Stub} so it can be used as an IBinder token.
     */
    public static class RunningServiceRecord extends IEmpty.Stub {
        // onStartCommand startId
        private final AtomicInteger mStartId = new AtomicInteger(1);
        private final AtomicInteger mBindCount = new AtomicInteger(0);
        // 正在连接的服务
        private ConnectedServiceRecord mConnectedServiceRecord;

        private ServiceInfo mServiceInfo;
        private Intent mIntent;

        /**
         * Atomically increments the start ID and returns the previous value.
         *
         * @return the start ID before increment
         */
        public int getAndIncrementStartId() {
            return mStartId.getAndIncrement();
        }

        /**
         * Atomically decrements the bind count and returns the new value.
         *
         * @return the bind count after decrement
         */
        public int decrementBindCountAndGet() {
            return mBindCount.decrementAndGet();
        }

        /**
         * Atomically increments the bind count and returns the new value.
         *
         * @return the bind count after increment
         */
        public int incrementBindCountAndGet() {
            return mBindCount.incrementAndGet();
        }
    }

    /**
     * Tracks a client-side service connection (binding). Holds the caller's IBinder
     * and the intent used to bind, enabling proper unbind and death tracking.
     */
    public static class ConnectedServiceRecord {
        private IBinder mIBinder;
        private Intent mIntent;
    }
}
