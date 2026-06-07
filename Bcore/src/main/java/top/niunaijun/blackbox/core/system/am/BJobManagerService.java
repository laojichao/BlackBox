package top.niunaijun.blackbox.core.system.am;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.RemoteException;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import black.android.app.job.BRJobInfo;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.system.BProcessManagerService;
import top.niunaijun.blackbox.core.system.ISystemService;
import top.niunaijun.blackbox.core.system.ProcessRecord;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.entity.JobRecord;
import top.niunaijun.blackbox.proxy.ProxyManifest;

/**
 * Virtual implementation of the Android {@link android.app.job.JobScheduler} system service.
 * Manages scheduled jobs within the virtual environment by intercepting job scheduling requests
 * and redirecting them through proxy job services in the host process.
 *
 * <p>Replaces the target service component in each {@link JobInfo} with a proxy stub
 * ({@link top.niunaijun.blackbox.proxy.ProxyManifest}) so that the host's JobScheduler
 * dispatches to the correct proxy, which in turn invokes the virtual app's real job service.</p>
 */
public class BJobManagerService extends IBJobManagerService.Stub implements ISystemService {
    private static final BJobManagerService sService = new BJobManagerService();

    // process_jobId
    private final Map<String, JobRecord> mJobRecords = new HashMap<>();

    /**
     * Returns the singleton instance of this service.
     *
     * @return the global BJobManagerService instance
     */
    public static BJobManagerService get() {
        return sService;
    }

    /**
     * Schedules a job in the virtual environment. Resolves the target service, ensures its
     * process is running, and rewrites the {@link JobInfo} to point to a proxy job service.
     *
     * @param info   the job scheduling info from the virtual app
     * @param userId the virtual user ID
     * @return the modified {@link JobInfo} with the proxy service component
     * @throws RemoteException if the remote call fails
     */
    @Override
    public JobInfo schedule(JobInfo info, int userId) throws RemoteException {
        ComponentName componentName = info.getService();
        Intent intent = new Intent();
        intent.setComponent(componentName);
        ResolveInfo resolveInfo = BPackageManagerService.get().resolveService(intent, PackageManager.GET_META_DATA, null, userId);
        if (resolveInfo == null) {
            return info;
        }
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        ProcessRecord processRecord = BProcessManagerService.get().findProcessRecord(serviceInfo.packageName, serviceInfo.processName, userId);
        if (processRecord == null) {
            processRecord = BProcessManagerService.get().
                    startProcessLocked(serviceInfo.packageName, serviceInfo.processName, userId, -1, Binder.getCallingPid());
            if (processRecord == null) {
                throw new RuntimeException(
                        "Unable to create Process " + serviceInfo.processName);
            }
        }
        return scheduleJob(processRecord, info, serviceInfo);
    }

    /**
     * Queries the stored job record for a given process and job ID.
     *
     * @param processName the process name that owns the job
     * @param jobId       the job ID
     * @param userId      the virtual user ID
     * @return the {@link JobRecord}, or null if not found
     * @throws RemoteException if the remote call fails
     */
    @Override
    public JobRecord queryJobRecord(String processName, int jobId, int userId) throws RemoteException {
        return mJobRecords.get(formatKey(processName, jobId));
    }

    /**
     * Stores a job record and rewrites the {@link JobInfo}'s service component to point
     * to the proxy job service for the given process.
     *
     * @param processRecord the hosting process
     * @param info          the original job info
     * @param serviceInfo   the resolved service info
     * @return the modified {@link JobInfo} with the proxy service component
     */
    public JobInfo scheduleJob(ProcessRecord processRecord, JobInfo info, ServiceInfo serviceInfo) {
        JobRecord jobRecord = new JobRecord();
        jobRecord.mJobInfo = info;
        jobRecord.mServiceInfo = serviceInfo;

        mJobRecords.put(formatKey(processRecord.processName, info.getId()), jobRecord);
        BRJobInfo.get(info)._set_service(new ComponentName(BlackBoxCore.getHostPkg(), ProxyManifest.getProxyJobService(processRecord.bpid)));
        return info;
    }

    /**
     * Cancels all jobs belonging to the given process.
     *
     * @param processName the process name whose jobs to cancel
     * @param userId      the virtual user ID
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void cancelAll(String processName, int userId) throws RemoteException {
        if (TextUtils.isEmpty(processName)) return;
        for (String key : mJobRecords.keySet()) {
            if (key.startsWith(processName + "_")) {
                JobRecord jobRecord = mJobRecords.get(key);
                // todo
            }
        }
    }

    /**
     * Cancels a specific job by process name and job ID.
     *
     * @param processName the process name that owns the job
     * @param jobId       the job ID to cancel
     * @param userId      the virtual user ID
     * @return the cancelled job ID
     * @throws RemoteException if the remote call fails
     */
    @Override
    public int cancel(String processName, int jobId, int userId) throws RemoteException {
        return jobId;
    }

    private String formatKey(String processName, int jobId) {
        return processName + "_" + jobId;
    }

    @Override
    public void systemReady() {

    }
}
