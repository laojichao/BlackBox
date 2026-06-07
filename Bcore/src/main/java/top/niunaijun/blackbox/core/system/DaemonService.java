package top.niunaijun.blackbox.core.system;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.utils.compat.BuildCompat;


/**
 * Foreground service that keeps the BlackBox virtual engine alive in the
 * background on Android O+.
 *
 * <p>On Android Oreo and above, the service promotes itself to the
 * foreground with a minimal notification to avoid being killed by the
 * system's background execution limits.  It also starts an inner
 * {@link DaemonInnerService} that immediately cancels the notification,
 * effectively hiding it from the user while still satisfying the
 * foreground-service requirement.</p>
 *
 * <p>Uses {@code START_STICKY} so that the system will restart the
 * service if it is killed.</p>
 */
public class DaemonService extends Service {
    public static final String TAG = "DaemonService";
    /** Notification ID derived from the host package name hash. */
    private static final int NOTIFY_ID = BlackBoxCore.getHostPkg().hashCode();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles service start.  Launches the inner helper service and,
     * on Android O+, promotes this service to the foreground.
     *
     * @param intent  the start intent
     * @param flags   additional data about the start request
     * @param startId a unique start request identifier
     * @return {@link #START_STICKY} to keep the service alive
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent innerIntent = new Intent(this, DaemonInnerService.class);
        startService(innerIntent);
        if (BuildCompat.isOreo()) {
            showNotification();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    /**
     * Builds and displays a minimal foreground notification on Android O+.
     */
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getPackageName() + ".blackbox_core")
                .setPriority(NotificationCompat.PRIORITY_MAX);
        startForeground(NOTIFY_ID, builder.build());
    }

    /**
     * Inner helper service that cancels the foreground notification
     * created by {@link DaemonService} and immediately stops itself.
     * This trick keeps the foreground-service requirement satisfied
     * without showing a persistent notification to the user.
     */
    public static class DaemonInnerService extends Service {
        @Override
        public void onCreate() {
            Log.i(TAG, "DaemonInnerService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i(TAG, "DaemonInnerService -> onStartCommand");
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(NOTIFY_ID);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            Log.i(TAG, "DaemonInnerService -> onDestroy");
            super.onDestroy();
        }
    }
}
