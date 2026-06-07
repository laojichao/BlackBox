package top.niunaijun.blackbox.app.dispatcher;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.res.Configuration;

import java.util.HashMap;
import java.util.Map;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.entity.JobRecord;

/**
 * Dispatcher that manages the lifecycle of virtual {@link JobService} instances running
 * inside the BlackBox virtual environment.
 * <p>
 * Acts as an intermediary between the host's {@link android.app.job.JobScheduler} and
 * virtual application job services, handling job start/stop callbacks, configuration changes,
 * and memory pressure events. Job service instances are lazily created and cached by job ID.
 *
 * @author Milk
 */
public class AppJobServiceDispatcher {
    private static final AppJobServiceDispatcher sServiceDispatcher = new AppJobServiceDispatcher();
    private final Map<Integer, JobRecord> mJobRecords = new HashMap<>();

    /**
     * Returns the singleton instance of the dispatcher.
     *
     * @return the global {@code AppJobServiceDispatcher} instance
     */
    public static AppJobServiceDispatcher get() {
        return sServiceDispatcher;
    }

    /**
     * Delegates a job start event to the corresponding virtual {@link JobService}.
     * <p>
     * If the service has not been created yet, it is instantiated on demand using the
     * stored {@link JobRecord}.
     *
     * @param params the job parameters containing the job ID and scheduling constraints
     * @return {@code true} if the job is still running and the system should hold a wakelock;
     *         {@code false} if the job finished synchronously or the service was not found
     */
    public boolean onStartJob(JobParameters params) {
        try {
            JobService jobService = getJobService(params.getJobId());
            if (jobService == null)
                return false;
            return jobService.onStartJob(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delegates a job stop event to the corresponding virtual {@link JobService},
     * then destroys and removes the service from the cache.
     *
     * @param params the job parameters containing the job ID
     * @return {@code true} if the job should be rescheduled, {@code false} otherwise;
     *         returns {@code false} if the service was not found
     */
    public boolean onStopJob(JobParameters params) {
        JobService jobService = getJobService(params.getJobId());
        if (jobService == null)
            return false;
        boolean b = jobService.onStopJob(params);
        jobService.onDestroy();
        synchronized (mJobRecords) {
            mJobRecords.remove(params.getJobId());
        }
        return b;
    }

    /**
     * Forwards a configuration change event to all active virtual job services.
     *
     * @param newConfig the new device configuration
     */
    public void onConfigurationChanged(Configuration newConfig) {
        for (JobRecord jobRecord : mJobRecords.values()) {
            if (jobRecord.mJobService != null) {
                jobRecord.mJobService.onConfigurationChanged(newConfig);
            }
        }
    }

    /**
     * Called when the dispatcher is being destroyed. Currently a no-op; job service
     * destruction is handled individually via {@link #onStopJob(JobParameters)}.
     */
    public void onDestroy() {
//        for (JobRecord jobRecord : mJobRecords.values()) {
//            if (jobRecord.mJobService != null) {
//                jobRecord.mJobService.onDestroy();
//            }
//        }
    }

    /**
     * Forwards a low-memory event to all active virtual job services.
     */
    public void onLowMemory() {
        for (JobRecord jobRecord : mJobRecords.values()) {
            if (jobRecord.mJobService != null) {
                jobRecord.mJobService.onLowMemory();
            }
        }
    }

    /**
     * Forwards a trim-memory event to all active virtual job services.
     *
     * @param level the memory trim level, as defined in {@link android.content.ComponentCallbacks2}
     */
    public void onTrimMemory(int level) {
        for (JobRecord jobRecord : mJobRecords.values()) {
            if (jobRecord.mJobService != null) {
                jobRecord.mJobService.onTrimMemory(level);
            }
        }
    }

    /**
     * Returns the virtual {@link JobService} for the given job ID, creating it on demand
     * if it has not been instantiated yet. The service is cached in {@link #mJobRecords}
     * for subsequent lookups.
     *
     * @param jobId the job ID to look up
     * @return the {@link JobService} instance, or {@code null} if creation failed or the
     *         job record was not found
     */
    JobService getJobService(int jobId) {
        synchronized (mJobRecords) {
            JobRecord jobRecord = mJobRecords.get(jobId);
            if (jobRecord != null && jobRecord.mJobService != null) {
                return jobRecord.mJobService;
            }
            try {
                JobRecord record = BlackBoxCore.getBJobManager().queryJobRecord(BActivityThread.getAppProcessName(), jobId);
                record.mJobService = BActivityThread.currentActivityThread().createJobService(record.mServiceInfo);
                if (record.mJobService == null)
                    return null;
                mJobRecords.put(jobId, record);
                return record.mJobService;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }
}
