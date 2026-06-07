package top.niunaijun.blackbox.proxy;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.dispatcher.AppServiceDispatcher;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Proxy Service stub that intercepts Android Service lifecycle callbacks and delegates
 * them to {@link AppServiceDispatcher} for execution within the virtual environment.
 * Handles bind, start, destroy, configuration changes, and memory events. Multiple
 * static inner classes (P0-P49) provide distinct manifest entries for concurrent
 * virtual services.
 *
 * @author Milk
 */
public class ProxyService extends Service {
    public static final String TAG = "StubService";

    /**
     * Called when a client binds to this service. Delegates to the app service dispatcher.
     *
     * @param intent the Intent used to bind to this service
     * @return the IBinder for client communication, or {@code null}
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return AppServiceDispatcher.get().onBind(intent);
    }

    /**
     * Called when the service is started via {@code startService}. Delegates to the
     * app service dispatcher.
     *
     * @param intent  the Intent used to start the service
     * @param flags   additional data about the start request
     * @param startId a unique integer representing this specific start request
     * @return {@link #START_NOT_STICKY}
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppServiceDispatcher.get().onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    /**
     * Called when the service is being destroyed. Notifies the app service dispatcher.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        AppServiceDispatcher.get().onDestroy();
    }

    /**
     * Called when the device configuration changes. Delegates to the dispatcher.
     *
     * @param newConfig the new device configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AppServiceDispatcher.get().onConfigurationChanged(newConfig);
    }

    /**
     * Called when the system is running low on memory. Delegates to the dispatcher.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        AppServiceDispatcher.get().onLowMemory();
    }

    /**
     * Called when the operating system determines it is a good time to trim memory.
     * Delegates to the dispatcher.
     *
     * @param level the context of the trim, indicating how much memory to reclaim
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        AppServiceDispatcher.get().onTrimMemory(level);
    }

    /**
     * Called when all clients have unbound from the service. Delegates to the dispatcher.
     *
     * @param intent the Intent used to bind to this service
     * @return {@code false} indicating {@link #onBind} should be called for future clients
     */
    @Override
    public boolean onUnbind(Intent intent) {
        AppServiceDispatcher.get().onUnbind(intent);
        return false;
    }

    /**
     * Shows a foreground notification to keep the service alive. Required on Android O+
     * for foreground services.
     */
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getPackageName() + ".blackbox_proxy")
                .setPriority(NotificationCompat.PRIORITY_MAX);
        if (BuildCompat.isOreo()) {
            startForeground(BlackBoxCore.getHostPkg().hashCode(), builder.build());
        }
    }

    public static class P0 extends ProxyService {

    }

    public static class P1 extends ProxyService {

    }

    public static class P2 extends ProxyService {

    }

    public static class P3 extends ProxyService {

    }

    public static class P4 extends ProxyService {

    }

    public static class P5 extends ProxyService {

    }

    public static class P6 extends ProxyService {

    }

    public static class P7 extends ProxyService {

    }

    public static class P8 extends ProxyService {

    }

    public static class P9 extends ProxyService {

    }

    public static class P10 extends ProxyService {

    }

    public static class P11 extends ProxyService {

    }

    public static class P12 extends ProxyService {

    }

    public static class P13 extends ProxyService {

    }

    public static class P14 extends ProxyService {

    }

    public static class P15 extends ProxyService {

    }

    public static class P16 extends ProxyService {

    }

    public static class P17 extends ProxyService {

    }

    public static class P18 extends ProxyService {

    }

    public static class P19 extends ProxyService {

    }

    public static class P20 extends ProxyService {

    }

    public static class P21 extends ProxyService {

    }

    public static class P22 extends ProxyService {

    }

    public static class P23 extends ProxyService {

    }

    public static class P24 extends ProxyService {

    }

    public static class P25 extends ProxyService {

    }

    public static class P26 extends ProxyService {

    }

    public static class P27 extends ProxyService {

    }

    public static class P28 extends ProxyService {

    }

    public static class P29 extends ProxyService {

    }

    public static class P30 extends ProxyService {

    }

    public static class P31 extends ProxyService {

    }

    public static class P32 extends ProxyService {

    }

    public static class P33 extends ProxyService {

    }

    public static class P34 extends ProxyService {

    }

    public static class P35 extends ProxyService {

    }

    public static class P36 extends ProxyService {

    }

    public static class P37 extends ProxyService {

    }

    public static class P38 extends ProxyService {

    }

    public static class P39 extends ProxyService {

    }

    public static class P40 extends ProxyService {

    }

    public static class P41 extends ProxyService {

    }

    public static class P42 extends ProxyService {

    }

    public static class P43 extends ProxyService {

    }

    public static class P44 extends ProxyService {

    }

    public static class P45 extends ProxyService {

    }

    public static class P46 extends ProxyService {

    }

    public static class P47 extends ProxyService {

    }

    public static class P48 extends ProxyService {

    }

    public static class P49 extends ProxyService {

    }
}
