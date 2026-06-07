package top.niunaijun.blackbox.fake.frameworks;

import android.app.job.JobInfo;
import android.os.RemoteException;

import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.am.IBJobManagerService;
import top.niunaijun.blackbox.entity.JobRecord;

/**
 * Client-side manager for job scheduling within the virtual environment. Provides
 * a facade over {@link IBJobManagerService} for scheduling, querying, and canceling
 * jobs scoped to the current virtual user.
 */
public class BJobManager extends BlackManager<IBJobManagerService> {
    private static final BJobManager sJobManager = new BJobManager();

    /**
     * Returns the singleton instance of {@link BJobManager}.
     *
     * @return the singleton BJobManager instance
     */
    public static BJobManager get() {
        return sJobManager;
    }

    @Override
    protected String getServiceName() {
        return ServiceManager.JOB_MANAGER;
    }

    /**
     * Schedules a job within the virtual environment.
     *
     * @param info the JobInfo to schedule
     * @return the scheduled JobInfo with updated constraints, or null on failure
     */
    public JobInfo schedule(JobInfo info) {
        try {
            return getService().schedule(info, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Queries a job record by process name and job ID.
     *
     * @param processName the process name that owns the job
     * @param jobId       the job ID to query
     * @return the JobRecord, or null on failure
     */
    public JobRecord queryJobRecord(String processName, int jobId) {
        try {
            return getService().queryJobRecord(processName, jobId, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cancels all jobs for the given process.
     *
     * @param processName the process name whose jobs should be canceled
     */
    public void cancelAll(String processName) {
        try {
            getService().cancelAll(processName, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancels a specific job by process name and job ID.
     *
     * @param processName the process name that owns the job
     * @param jobId       the job ID to cancel
     * @return the result code, or -1 on failure
     */
    public int cancel(String processName, int jobId) {
        try {
            return getService().cancel(processName, jobId, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
