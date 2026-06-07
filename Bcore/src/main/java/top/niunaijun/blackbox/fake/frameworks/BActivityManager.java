package top.niunaijun.blackbox.fake.frameworks;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.am.IBActivityManagerService;
import top.niunaijun.blackbox.entity.AppConfig;
import top.niunaijun.blackbox.entity.UnbindRecord;
import top.niunaijun.blackbox.entity.am.PendingResultData;
import top.niunaijun.blackbox.entity.am.RunningAppProcessInfo;
import top.niunaijun.blackbox.entity.am.RunningServiceInfo;

/**
 * Client-side manager for activity, service, and broadcast operations within the
 * virtual environment. Provides a facade over {@link IBActivityManagerService} for
 * process initialization, activity/service lifecycle management, broadcast dispatching,
 * and content provider access.
 */
public class BActivityManager extends BlackManager<IBActivityManagerService> {
    private static final BActivityManager sActivityManager = new BActivityManager();

    /**
     * Returns the singleton instance of {@link BActivityManager}.
     *
     * @return the singleton BActivityManager instance
     */
    public static BActivityManager get() {
        return sActivityManager;
    }

    @Override
    protected String getServiceName() {
        return ServiceManager.ACTIVITY_MANAGER;
    }

    /**
     * Initializes a virtual process for the given package.
     *
     * @param packageName the package name to initialize
     * @param processName the process name
     * @param userId      the virtual user ID
     * @return the AppConfig for the initialized process, or null on failure
     */
    public AppConfig initProcess(String packageName, String processName, int userId) {
        try {
            return getService().initProcess(packageName, processName, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Restarts a virtual process for the given package.
     *
     * @param packageName the package name to restart
     * @param processName the process name
     * @param userId      the virtual user ID
     */
    public void restartProcess(String packageName, String processName, int userId) {
        try {
            getService().restartProcess(packageName, processName, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts an activity within the virtual environment.
     *
     * @param intent the Intent describing the activity to start
     * @param userId the virtual user ID
     */
    public void startActivity(Intent intent, int userId) {
        try {
            getService().startActivity(intent, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts an activity through the AMS proxy with full parameters.
     *
     * @param userId        the virtual user ID
     * @param intent        the Intent to start
     * @param resolvedType  the resolved MIME type
     * @param resultTo      the IBinder token of the calling activity
     * @param resultWho     the caller identifier
     * @param requestCode   the request code for result delivery
     * @param flags         the Intent flags
     * @param options       additional options bundle
     * @return the result code, or -1 on failure
     */
    public int startActivityAms(int userId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, Bundle options) {
        try {
            return getService().startActivityAms(userId, intent, resolvedType, resultTo, resultWho, requestCode, flags, options);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Starts multiple activities at once.
     *
     * @param userId       the virtual user ID
     * @param intent       the Intents to start
     * @param resolvedType the resolved MIME types
     * @param resultTo     the IBinder token of the calling activity
     * @param options      additional options bundle
     * @return the result code, or -1 on failure
     */
    public int startActivities(int userId, Intent[] intent, String[] resolvedType, IBinder resultTo, Bundle options) {
        try {
            return getService().startActivities(userId, intent, resolvedType, resultTo, options);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Starts a service within the virtual environment.
     *
     * @param intent           the Intent describing the service to start
     * @param resolvedType     the resolved MIME type
     * @param requireForeground whether foreground execution is required
     * @param userId           the virtual user ID
     * @return the ComponentName of the started service, or null on failure
     */
    public ComponentName startService(Intent intent, String resolvedType, boolean requireForeground, int userId) {
        try {
            return getService().startService(intent, resolvedType, requireForeground, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Stops a service within the virtual environment.
     *
     * @param intent       the Intent describing the service to stop
     * @param resolvedType the resolved MIME type
     * @param userId       the virtual user ID
     * @return the result code, or -1 on failure
     */
    public int stopService(Intent intent, String resolvedType, int userId) {
        try {
            return getService().stopService(intent, resolvedType, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Binds to a service within the virtual environment.
     *
     * @param service      the Intent describing the service to bind to
     * @param binder       the connection binder token
     * @param resolvedType the resolved MIME type
     * @param userId       the virtual user ID
     * @return the Intent for the bound service, or null on failure
     */
    public Intent bindService(Intent service, IBinder binder, String resolvedType, int userId) {
        try {
            return getService().bindService(service, binder, resolvedType, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Unbinds a service connection within the virtual environment.
     *
     * @param binder the connection binder token
     * @param userId the virtual user ID
     */
    public void unbindService(IBinder binder, int userId) {
        try {
            getService().unbindService(binder, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops a service by its token.
     *
     * @param componentName the service component
     * @param token         the service token
     * @param userId        the virtual user ID
     */
    public void stopServiceToken(ComponentName componentName, IBinder token, int userId) {
        try {
            getService().stopServiceToken(componentName, token, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies the service manager that a service has received a start command.
     *
     * @param proxyIntent the proxy intent for the service
     * @param userId      the virtual user ID
     */
    public void onStartCommand(Intent proxyIntent, int userId) {
        try {
            getService().onStartCommand(proxyIntent, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies the service manager that a service has been unbound.
     *
     * @param proxyIntent the proxy intent for the service
     * @param userId      the virtual user ID
     * @return the UnbindRecord for the unbound service, or null on failure
     */
    public UnbindRecord onServiceUnbind(Intent proxyIntent, int userId) {
        try {
            return getService().onServiceUnbind(proxyIntent, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Notifies the service manager that a service has been destroyed.
     *
     * @param proxyIntent the proxy intent for the service
     * @param userId      the virtual user ID
     */
    public void onServiceDestroy(Intent proxyIntent, int userId) {
        try {
            getService().onServiceDestroy(proxyIntent, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Acquires a content provider client binder for the given provider info.
     *
     * @param providerInfo the provider info
     * @return the IBinder of the content provider client, or null on failure
     */
    public IBinder acquireContentProviderClient(ProviderInfo providerInfo) {
        try {
            return getService().acquireContentProviderClient(providerInfo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sends a broadcast within the virtual environment.
     *
     * @param intent       the broadcast Intent
     * @param resolvedType the resolved MIME type
     * @param userId       the virtual user ID
     * @return the processed Intent, or null on failure
     */
    public Intent sendBroadcast(Intent intent, String resolvedType, int userId) {
        try {
            return getService().sendBroadcast(intent, resolvedType, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Peeks at the binder of a running service.
     *
     * @param intent       the Intent describing the service
     * @param resolvedType the resolved MIME type
     * @param userId       the virtual user ID
     * @return the service IBinder, or null on failure
     */
    public IBinder peekService(Intent intent, String resolvedType, int userId) {
        try {
            return getService().peekService(intent, resolvedType, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Notifies the service manager that an activity has been created.
     *
     * @param taskId        the task ID
     * @param token         the activity IBinder token
     * @param activityRecord the activity record IBinder
     */
    public void onActivityCreated(int taskId, IBinder token, IBinder activityRecord) {
        try {
            getService().onActivityCreated(taskId, token, activityRecord);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies the service manager that an activity has been resumed. Includes a
     * workaround for WeChat focus issues.
     *
     * @param token the activity IBinder token
     */
    public void onActivityResumed(IBinder token) {
        try {
            // Fix https://github.com/FBlackBox/BlackBox/issues/28
            if ("com.tencent.mm".equals(BActivityThread.getAppPackageName())) {
                Activity activityByToken = BActivityThread.getActivityByToken(token);
                if (activityByToken != null) {
                    activityByToken.getWindow().getDecorView().clearFocus();
                }
            }
        } catch (Throwable ignored) {
        }
        try {
            getService().onActivityResumed(token);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies the service manager that an activity has been destroyed.
     *
     * @param token the activity IBinder token
     */
    public void onActivityDestroyed(IBinder token) {
        try {
            getService().onActivityDestroyed(token);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies the service manager that an activity is being finished.
     *
     * @param token the activity IBinder token
     */
    public void onFinishActivity(IBinder token) {
        try {
            getService().onFinishActivity(token);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns information about running app processes.
     *
     * @param callerPackage the calling package name
     * @param userId        the virtual user ID
     * @return the RunningAppProcessInfo, or null on failure
     * @throws RemoteException if the remote call fails
     */
    public RunningAppProcessInfo getRunningAppProcesses(String callerPackage, int userId) throws RemoteException {
        try {
            return getService().getRunningAppProcesses(callerPackage, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns information about running services.
     *
     * @param callerPackage the calling package name
     * @param userId        the virtual user ID
     * @return the RunningServiceInfo, or null on failure
     * @throws RemoteException if the remote call fails
     */
    public RunningServiceInfo getRunningServices(String callerPackage, int userId) throws RemoteException {
        try {
            return getService().getRunningServices(callerPackage, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Schedules a broadcast receiver execution.
     *
     * @param intent          the broadcast Intent
     * @param pendingResultData the pending result data
     * @param userId          the virtual user ID
     * @throws RemoteException if the remote call fails
     */
    public void scheduleBroadcastReceiver(Intent intent, PendingResultData pendingResultData, int userId) throws RemoteException {
        getService().scheduleBroadcastReceiver(intent, pendingResultData, userId);
    }

    /**
     * Finishes a broadcast and delivers its pending result data.
     *
     * @param data the PendingResultData to finish
     */
    public void finishBroadcast(PendingResultData data) {
        try {
            getService().finishBroadcast(data);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the calling package name for the given activity token.
     *
     * @param token  the activity IBinder token
     * @param userId the virtual user ID
     * @return the calling package name, or null on failure
     */
    public String getCallingPackage(IBinder token, int userId) {
        try {
            return getService().getCallingPackage(token, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the calling activity component for the given activity token.
     *
     * @param token  the activity IBinder token
     * @param userId the virtual user ID
     * @return the calling Activity ComponentName, or null on failure
     */
    public ComponentName getCallingActivity(IBinder token, int userId) {
        try {
            return getService().getCallingActivity(token, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Registers an intent sender for the given target binder.
     *
     * @param target      the target IBinder
     * @param packageName the package name owning the sender
     * @param uid         the UID of the package
     */
    public void getIntentSender(IBinder target, String packageName, int uid) {
        try {
            getService().getIntentSender(target, packageName, uid, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the package name associated with an intent sender.
     *
     * @param target the intent sender IBinder
     * @return the package name, or null on failure
     */
    public String getPackageForIntentSender(IBinder target) {
        try {
            return getService().getPackageForIntentSender(target, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the UID associated with an intent sender.
     *
     * @param target the intent sender IBinder
     * @return the UID, or -1 on failure
     */
    public int getUidForIntentSender(IBinder target) {
        try {
            return getService().getUidForIntentSender(target, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
