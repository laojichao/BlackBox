package top.niunaijun.blackbox.proxy;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.res.Configuration;

import top.niunaijun.blackbox.app.dispatcher.AppJobServiceDispatcher;

/**
 * Proxy JobService stub that intercepts job scheduling callbacks and delegates them
 * to {@link AppJobServiceDispatcher} for execution within the virtual environment.
 * Multiple static inner classes (P0-P49) provide distinct manifest entries for
 * concurrent virtual job services.
 *
 * @author Milk
 */
public class ProxyJobService extends JobService {
    public static final String TAG = "StubJobService";

    /**
     * Called when the system determines that the job should start. Delegates to the
     * app job service dispatcher.
     *
     * @param params the parameters for this job
     * @return {@code true} if the job is still running asynchronously
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        return AppJobServiceDispatcher.get().onStartJob(params);
    }

    /**
     * Called when the system determines that the job should stop. Delegates to the
     * app job service dispatcher.
     *
     * @param params the parameters for this job
     * @return {@code true} if the job should be rescheduled
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        return AppJobServiceDispatcher.get().onStopJob(params);
    }

    /**
     * Called when the service is started. Returns {@link #START_NOT_STICKY} to indicate
     * the service should not be restarted after being killed.
     *
     * @param intent  the Intent used to start the service
     * @param flags   additional data about the start request
     * @param startId a unique integer representing this specific start request
     * @return {@link #START_NOT_STICKY}
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    /**
     * Called when the service is being destroyed. Notifies the app job service dispatcher.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        AppJobServiceDispatcher.get().onDestroy();
    }

    /**
     * Called when the device configuration changes. Delegates to the dispatcher.
     *
     * @param newConfig the new device configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AppJobServiceDispatcher.get().onConfigurationChanged(newConfig);
    }

    /**
     * Called when the system is running low on memory. Delegates to the dispatcher.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        AppJobServiceDispatcher.get().onLowMemory();
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
        AppJobServiceDispatcher.get().onTrimMemory(level);
    }

    public static class P0 extends ProxyJobService {

    }

    public static class P1 extends ProxyJobService {

    }

    public static class P2 extends ProxyJobService {

    }

    public static class P3 extends ProxyJobService {

    }

    public static class P4 extends ProxyJobService {

    }

    public static class P5 extends ProxyJobService {

    }

    public static class P6 extends ProxyJobService {

    }

    public static class P7 extends ProxyJobService {

    }

    public static class P8 extends ProxyJobService {

    }

    public static class P9 extends ProxyJobService {

    }

    public static class P10 extends ProxyJobService {

    }

    public static class P11 extends ProxyJobService {

    }

    public static class P12 extends ProxyJobService {

    }

    public static class P13 extends ProxyJobService {

    }

    public static class P14 extends ProxyJobService {

    }

    public static class P15 extends ProxyJobService {

    }

    public static class P16 extends ProxyJobService {

    }

    public static class P17 extends ProxyJobService {

    }

    public static class P18 extends ProxyJobService {

    }

    public static class P19 extends ProxyJobService {

    }

    public static class P20 extends ProxyJobService {

    }

    public static class P21 extends ProxyJobService {

    }

    public static class P22 extends ProxyJobService {

    }

    public static class P23 extends ProxyJobService {

    }

    public static class P24 extends ProxyJobService {

    }

    public static class P25 extends ProxyJobService {

    }

    public static class P26 extends ProxyJobService {

    }

    public static class P27 extends ProxyJobService {

    }

    public static class P28 extends ProxyJobService {

    }

    public static class P29 extends ProxyJobService {

    }

    public static class P30 extends ProxyJobService {

    }

    public static class P31 extends ProxyJobService {

    }

    public static class P32 extends ProxyJobService {

    }

    public static class P33 extends ProxyJobService {

    }

    public static class P34 extends ProxyJobService {

    }

    public static class P35 extends ProxyJobService {

    }

    public static class P36 extends ProxyJobService {

    }

    public static class P37 extends ProxyJobService {

    }

    public static class P38 extends ProxyJobService {

    }

    public static class P39 extends ProxyJobService {

    }

    public static class P40 extends ProxyJobService {

    }

    public static class P41 extends ProxyJobService {

    }

    public static class P42 extends ProxyJobService {

    }

    public static class P43 extends ProxyJobService {

    }

    public static class P44 extends ProxyJobService {

    }

    public static class P45 extends ProxyJobService {

    }

    public static class P46 extends ProxyJobService {

    }

    public static class P47 extends ProxyJobService {

    }

    public static class P48 extends ProxyJobService {

    }

    public static class P49 extends ProxyJobService {

    }
}
