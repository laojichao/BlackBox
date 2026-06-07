package top.niunaijun.blackbox.app.dispatcher;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;

import java.util.HashMap;
import java.util.Map;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.entity.ServiceRecord;
import top.niunaijun.blackbox.entity.UnbindRecord;
import top.niunaijun.blackbox.proxy.record.ProxyServiceRecord;

import static android.app.Service.START_NOT_STICKY;


/**
 * Dispatcher that manages the lifecycle of virtual {@link Service} instances running
 * inside the BlackBox virtual environment.
 * <p>
 * Acts as an intermediary between the host's proxy services (which handle actual Android
 * service lifecycle) and virtual application services. It handles binding, starting,
 * stopping, unbinding, and destroying virtual services. Service instances are cached
 * by their {@link Intent.FilterComparison} key and are lazily created on first access.
 * <p>
 * This class also forwards system callbacks such as configuration changes and
 * memory pressure events to all active virtual services.
 *
 * @author Milk
 */
public class AppServiceDispatcher {
    public static final String TAG = "AppServiceDispatcher";

    private static final AppServiceDispatcher sServiceDispatcher = new AppServiceDispatcher();

    private Map<Intent.FilterComparison, ServiceRecord> mService = new HashMap<>();

    /**
     * Returns the singleton instance of the service dispatcher.
     *
     * @return the global {@code AppServiceDispatcher} instance
     */
    public static AppServiceDispatcher get() {
        return sServiceDispatcher;
    }

    private final Handler mHandler = BlackBoxCore.get().getHandler();

    /**
     * Handles a bind request from the host's proxy service. Retrieves or creates the
     * virtual service, increments the bind count, and returns the service's IBinder.
     * If the service already has a cached binder, it is returned directly; on rebind
     * scenarios, {@link Service#onRebind(Intent)} is called.
     *
     * @param proxyIntent the intent from the host proxy service containing the virtual
     *                    service metadata via {@link ProxyServiceRecord}
     * @return the {@link IBinder} returned by the virtual service's {@code onBind()},
     *         or {@code null} if the intent/service info is invalid or binding failed
     */
    public IBinder onBind(Intent proxyIntent) {
        ProxyServiceRecord serviceRecord = ProxyServiceRecord.create(proxyIntent);
        Intent intent = serviceRecord.mServiceIntent;
        ServiceInfo serviceInfo = serviceRecord.mServiceInfo;

        if (intent == null || serviceInfo == null)
            return null;

//        Log.d(TAG, "onBind: " + component.toString());

        Service service = getOrCreateService(serviceRecord);
        if (service == null)
            return null;
        intent.setExtrasClassLoader(service.getClassLoader());

        ServiceRecord record = findRecord(intent);
        record.incrementAndGetBindCount(intent);
        if (record.hasBinder(intent)) {
            if (record.isRebind()) {
                service.onRebind(intent);
                record.setRebind(false);
            }
            return record.getBinder(intent);
        }

        try {
            IBinder iBinder = service.onBind(intent);
            record.addBinder(intent, iBinder);
            return iBinder;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Handles a start command from the host's proxy service. Retrieves or creates the
     * virtual service, sets the start ID, and delegates to {@link Service#onStartCommand(Intent, int, int)}.
     *
     * @param proxyIntent the intent from the host proxy service containing the virtual
     *                    service metadata via {@link ProxyServiceRecord}
     * @param flags       start flags as passed to {@code onStartCommand}
     * @param startId     the unique start identifier for this command
     * @return the service start compatibility constant (e.g., {@code START_STICKY}),
     *         or {@link Service#START_NOT_STICKY} if the service could not be started
     */
    public int onStartCommand(Intent proxyIntent, int flags, int startId) {
        ProxyServiceRecord stubRecord = ProxyServiceRecord.create(proxyIntent);
        if (stubRecord.mServiceIntent == null || stubRecord.mServiceInfo == null) {
            return START_NOT_STICKY;
        }

//        Log.d(TAG, "onStartCommand: " + component.toString());
        Service service = getOrCreateService(stubRecord);
        if (service == null)
            return START_NOT_STICKY;
        stubRecord.mServiceIntent.setExtrasClassLoader(service.getClassLoader());
        ServiceRecord record = findRecord(stubRecord.mServiceIntent);
        record.setStartId(stubRecord.mStartId);
        try {
            int i = service.onStartCommand(stubRecord.mServiceIntent, flags, stubRecord.mStartId);
            BlackBoxCore.getBActivityManager().onStartCommand(proxyIntent, stubRecord.mUserId);
            return i;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    /**
     * Destroys all active virtual services and clears the service cache.
     * Each service's {@code onDestroy()} is called; any exceptions are caught and logged.
     */
    public void onDestroy() {
        if (mService.size() > 0) {
            for (ServiceRecord record : mService.values()) {
                try {
                    record.getService().onDestroy();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        mService.clear();
//        Log.d(TAG, "onDestroy: ");
    }

    /**
     * Forwards a configuration change event to all active virtual services.
     *
     * @param newConfig the new device configuration
     */
    public void onConfigurationChanged(Configuration newConfig) {
        if (mService.size() > 0) {
            for (ServiceRecord record : mService.values()) {
                try {
                    record.getService().onConfigurationChanged(newConfig);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
//        Log.d(TAG, "onConfigurationChanged");
    }

    /**
     * Forwards a low-memory event to all active virtual services.
     */
    public void onLowMemory() {
        if (mService.size() > 0) {
            for (ServiceRecord record : mService.values()) {
                try {
                    record.getService().onLowMemory();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
//        Log.d(TAG, "onLowMemory");
    }

    /**
     * Forwards a trim-memory event to all active virtual services.
     *
     * @param level the memory trim level, as defined in {@link android.content.ComponentCallbacks2}
     */
    public void onTrimMemory(int level) {
        if (mService.size() > 0) {
            for (ServiceRecord record : mService.values()) {
                try {
                    record.getService().onTrimMemory(level);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        // Log.d(TAG, "onTrimMemory");
    }

    /**
     * Handles an unbind request from the host's proxy service. Decreases the connection
     * count and, if no more clients are bound and no pending start commands remain,
     * destroys the virtual service and notifies the activity manager.
     *
     * @param proxyIntent the intent from the host proxy service containing the virtual
     *                    service metadata via {@link ProxyServiceRecord}
     * @return {@code true} if the service indicated it wishes to be rebound in the future
     *         via {@link Service#onUnbind(Intent)}; always returns {@code false} in the
     *         current implementation
     */
    public boolean onUnbind(Intent proxyIntent) {
        ProxyServiceRecord stubRecord = ProxyServiceRecord.create(proxyIntent);
        if (stubRecord.mServiceIntent == null || stubRecord.mServiceInfo == null) {
            return false;
        }
        Intent intent = stubRecord.mServiceIntent;

        try {
            UnbindRecord unbindRecord = BlackBoxCore.getBActivityManager().onServiceUnbind(proxyIntent, BActivityThread.getUserId());
            if (unbindRecord == null)
                return false;

            Service service = getOrCreateService(stubRecord);
            if (service == null)
                return false;

            stubRecord.mServiceIntent.setExtrasClassLoader(service.getClassLoader());

            ServiceRecord record = findRecord(intent);

            boolean destroy = unbindRecord.getStartId() == 0;
            if (destroy || record.decreaseConnectionCount(intent)) {
                boolean b = service.onUnbind(intent);
                if (destroy) {
                    service.onDestroy();
                    BlackBoxCore.getBActivityManager().onServiceDestroy(proxyIntent, BActivityThread.getUserId());
                    mService.remove(new Intent.FilterComparison(intent));
                }
                record.setRebind(true);
//                Log.d(TAG, "onUnbind：" + stubRecord.mServiceIntent.getComponent().toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the cached {@link IBinder} for a previously bound virtual service without
     * triggering a new bind. Used for peeking at a service's binder.
     *
     * @param intent the intent identifying the virtual service
     * @return the cached {@link IBinder}, or {@code null} if no record exists
     */
    public IBinder peekService(Intent intent) {
        ServiceRecord record = findRecord(intent);
        if (record == null) {
            return null;
        }
        return record.getBinder(intent);
    }

    /**
     * Stops a virtual service by calling its {@code onDestroy()} on the main handler
     * thread and notifying the activity manager. The service is removed from the cache.
     * Does nothing if the intent is {@code null}, the record is not found, or the
     * service has not been started (startId is zero).
     *
     * @param intent the intent identifying the virtual service to stop
     */
    public void stopService(Intent intent) {
        if (intent == null)
            return;
        ServiceRecord record = findRecord(intent);
        if (record == null)
            return;
        if (record.getService() != null) {
            boolean destroy = record.getStartId() > 0;
            try {
                if (destroy) {
                    mHandler.post(() -> record.getService().onDestroy());
                    BlackBoxCore.getBActivityManager().onServiceDestroy(intent, BActivityThread.getUserId());
                    mService.remove(new Intent.FilterComparison(intent));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private ServiceRecord findRecord(Intent intent) {
        return mService.get(new Intent.FilterComparison(intent));
    }

    private Service getOrCreateService(ProxyServiceRecord proxyServiceRecord) {
        Intent intent = proxyServiceRecord.mServiceIntent;
        ServiceInfo serviceInfo = proxyServiceRecord.mServiceInfo;
        IBinder token = proxyServiceRecord.mToken;

        ServiceRecord record = findRecord(intent);
        if (record != null && record.getService() != null) {
            return record.getService();
        }
        Service service = BActivityThread.currentActivityThread().createService(serviceInfo, token);
        if (service == null)
            return null;
        record = new ServiceRecord();
        record.setService(service);
        mService.put(new Intent.FilterComparison(intent), record);
        return service;
    }
}
