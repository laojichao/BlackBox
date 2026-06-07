package top.niunaijun.blackbox.core.system;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.IBActivityThread;
import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.notification.BNotificationManagerService;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.core.system.user.BUserHandle;
import top.niunaijun.blackbox.entity.AppConfig;
import top.niunaijun.blackbox.proxy.ProxyManifest;
import top.niunaijun.blackbox.utils.FileUtils;
import top.niunaijun.blackbox.utils.Slog;
import top.niunaijun.blackbox.utils.compat.ApplicationThreadCompat;
import top.niunaijun.blackbox.utils.compat.BundleCompat;
import top.niunaijun.blackbox.utils.provider.ProviderCall;

/**
 * Manages virtual application processes inside the BlackBox environment.
 *
 * <p>Tracks every running virtual process as a {@link ProcessRecord},
 * handles process creation (by launching a stub host process and
 * attaching the virtual app thread via IPC), restarts, and clean-up.
 * Process-to-user mappings are keyed by a composite virtual UID
 * ({@code buid}) derived from the virtual user id and the package's
 * app id.</p>
 *
 * <p>This service also provides utility methods for looking up
 * processes by PID or package name, and for killing all processes
 * belonging to a specific package or user.</p>
 */
public class BProcessManagerService implements ISystemService {
    public static final String TAG = "BProcessManager";

    public static BProcessManagerService sBProcessManagerService = new BProcessManagerService();

    /** Map from virtual UID to (processName -> ProcessRecord). */
    private final Map<Integer, Map<String, ProcessRecord>> mProcessMap = new HashMap<>();
    /** Flat list of all active process records. */
    private final List<ProcessRecord> mPidsSelfLocked = new ArrayList<>();
    /** Lock guarding {@link #mProcessMap} and {@link #mPidsSelfLocked}. */
    private final Object mProcessLock = new Object();

    /**
     * Returns the singleton instance.
     *
     * @return the {@code BProcessManagerService} instance
     */
    public static BProcessManagerService get() {
        return sBProcessManagerService;
    }

    /**
     * Starts (or reuses) a virtual application process.
     *
     * <p>If {@code bpid} is {@code -1} a free stub PID slot is allocated.
     * If a process with the same name already exists and is fully
     * initialized, the existing record is returned.  Otherwise a new stub
     * host process is launched, the virtual app is initialized in it via
     * IPC, and a fresh {@link ProcessRecord} is registered.</p>
     *
     * @param packageName  the virtual app's package name
     * @param processName  the target process name
     * @param userId       virtual user id
     * @param bpid         pre-allocated stub PID, or {@code -1} for auto
     * @param callingPid   PID of the process that initiated the start
     * @return the {@link ProcessRecord} for the running process, or
     *         {@code null} on failure
     */
    public ProcessRecord startProcessLocked(String packageName, String processName, int userId, int bpid, int callingPid) {
        ApplicationInfo info = BPackageManagerService.get().getApplicationInfo(packageName, 0, userId);
        if (info == null)
            return null;
        ProcessRecord app;
        int buid = BUserHandle.getUid(userId, BPackageManagerService.get().getAppId(packageName));
        synchronized (mProcessLock) {
            Map<String, ProcessRecord> bProcess = mProcessMap.get(buid);

            if (bProcess == null) {
                bProcess = new HashMap<>();
            }
            if (bpid == -1) {
                app = bProcess.get(processName);
                if (app != null) {
                    if (app.initLock != null) {
                        app.initLock.block();
                    }
                    if (app.bActivityThread != null) {
                        return app;
                    }
                }
                bpid = getUsingBPidL();
                Slog.d(TAG, "init bUid = " + buid + ", bPid = " + bpid);
            }
            if (bpid == -1) {
                throw new RuntimeException("No processes available");
            }
            app = new ProcessRecord(info, processName);
            app.uid = Process.myUid();
            app.bpid = bpid;
            app.buid = BPackageManagerService.get().getAppId(packageName);
            app.callingBUid = getBUidByPidOrPackageName(callingPid, packageName);
            app.userId = userId;

            bProcess.put(processName, app);
            mPidsSelfLocked.add(app);

            mProcessMap.put(buid, bProcess);
            if (!initAppProcessL(app)) {
                //init process fail
                bProcess.remove(processName);
                mPidsSelfLocked.remove(app);
                app = null;
            } else {
                app.pid = getPid(BlackBoxCore.getContext(), ProxyManifest.getProcessName(app.bpid));
            }
        }
        return app;
    }

    /**
     * Returns the next available stub process ID that is not currently
     * occupied by a running host process.
     *
     * @return a free PID index, or {@code -1} if all slots are exhausted
     */
    private int getUsingBPidL() {
        ActivityManager manager = (ActivityManager) BlackBoxCore.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
        Set<Integer> usingPs = new HashSet<>();
        for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
            int i = parseBPid(runningAppProcess.processName);
            usingPs.add(i);
        }
        for (int i = 0; i < ProxyManifest.FREE_COUNT; i++) {
            if (usingPs.contains(i)) {
                continue;
            }
            return i;
        }
        return -1;
    }

    /**
     * Restarts a virtual app process in the caller's host process.
     *
     * <p>If the calling PID is not already tracked as a virtual process,
     * its stub PID slot is reused to start the specified package.</p>
     *
     * @param packageName  the virtual app's package name
     * @param processName  the target process name
     * @param userId       virtual user id
     */
    public void restartAppProcess(String packageName, String processName, int userId) {
        synchronized (mProcessLock) {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            ProcessRecord app;
            synchronized (mProcessLock) {
                app = findProcessByPid(callingPid);
            }
            if (app == null) {
                String stubProcessName = getProcessName(BlackBoxCore.getContext(), callingPid);
                int bpid = parseBPid(stubProcessName);
                startProcessLocked(packageName, processName, userId, bpid, callingPid);
            }
        }
    }

    /**
     * Extracts the stub PID index from a host process name.
     *
     * @param stubProcessName the process name (e.g. {@code com.host:p0})
     * @return the numeric index, or {@code -1} on parse failure
     */
    private int parseBPid(String stubProcessName) {
        String prefix;
        if (stubProcessName == null) {
            return -1;
        } else {
            prefix = BlackBoxCore.getHostPkg() + ":p";
        }
        if (stubProcessName.startsWith(prefix)) {
            try {
                return Integer.parseInt(stubProcessName.substring(prefix.length()));
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return -1;
    }

    /**
     * Initializes a newly-created process by sending the virtual app
     * configuration over IPC and waiting for the client thread binder
     * to be returned.
     *
     * @param record the process record to initialize
     * @return {@code true} if initialization succeeded
     */
    private boolean initAppProcessL(ProcessRecord record) {
        Log.d(TAG, "initProcess: " + record.processName);
        AppConfig appConfig = record.getClientConfig();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConfig.KEY, appConfig);
        Bundle init = ProviderCall.callSafely(record.getProviderAuthority(), "_Black_|_init_process_", null, bundle);
        IBinder appThread = BundleCompat.getBinder(init, "_Black_|_client_");
        if (appThread == null || !appThread.isBinderAlive()) {
            return false;
        }
        attachClientL(record, appThread);

        createProc(record);
        return true;
    }

    /**
     * Attaches the client-side {@link IBActivityThread} binder to the
     * process record, registers a death recipient, and opens the
     * initialization lock.
     *
     * @param app       the process record
     * @param appThread the binder returned by the virtual app process
     */
    private void attachClientL(final ProcessRecord app, final IBinder appThread) {
        IBActivityThread activityThread = IBActivityThread.Stub.asInterface(appThread);
        if (activityThread == null) {
            app.kill();
            return;
        }
        try {
            appThread.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    Log.d(TAG, "App Died: " + app.processName);
                    appThread.unlinkToDeath(this, 0);
                    onProcessDie(app);
                }
            }, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        app.bActivityThread = activityThread;
        try {
            app.appThread = ApplicationThreadCompat.asInterface(activityThread.getActivityThread());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        app.initLock.open();
    }

    /**
     * Called when a virtual process dies unexpectedly.  Removes the
     * process from all tracking structures, cleans up its proc entry,
     * and deletes any package-level notifications.
     *
     * @param record the dead process record
     */
    public void onProcessDie(ProcessRecord record) {
        synchronized (mProcessLock) {
            record.kill();
            Map<String, ProcessRecord> process = mProcessMap.get(record.buid);
            if (process != null) {
                process.remove(record.processName);
                if (process.isEmpty()) {
                    mProcessMap.remove(record.buid);
                }
            }
            mPidsSelfLocked.remove(record);

            removeProc(record);
            BNotificationManagerService.get().deletePackageNotification(record.getPackageName(), record.userId);
        }
    }

    /**
     * Looks up a process record by package name, process name, and user.
     *
     * @param packageName  the virtual app's package name
     * @param processName  the process name
     * @param userId       virtual user id
     * @return the matching {@link ProcessRecord}, or {@code null} if not found
     */
    public ProcessRecord findProcessRecord(String packageName, String processName, int userId) {
        synchronized (mProcessLock) {
            int appId = BPackageManagerService.get().getAppId(packageName);
            int buid = BUserHandle.getUid(userId, appId);
            Map<String, ProcessRecord> processRecordMap = mProcessMap.get(buid);
            if (processRecordMap == null)
                return null;
            return processRecordMap.get(processName);
        }
    }

    /**
     * Kills all processes belonging to the given package across all users.
     *
     * @param packageName the package whose processes should be killed
     */
    public void killAllByPackageName(String packageName) {
        synchronized (mProcessLock) {
            synchronized (mPidsSelfLocked) {
                List<ProcessRecord> tmp = new ArrayList<>(mPidsSelfLocked);
                int appId = BPackageManagerService.get().getAppId(packageName);
                for (ProcessRecord processRecord : mPidsSelfLocked) {
                    int appId1 = BUserHandle.getAppId(processRecord.buid);
                    if (appId == appId1) {
                        mProcessMap.remove(processRecord.buid);
                        tmp.remove(processRecord);
                        processRecord.kill();
                    }
                }
                mPidsSelfLocked.clear();
                mPidsSelfLocked.addAll(tmp);
            }
        }
    }

    /**
     * Kills all processes of the given package for a specific virtual user.
     *
     * @param packageName the package name
     * @param userId      virtual user id
     */
    public void killPackageAsUser(String packageName, int userId) {
        synchronized (mProcessLock) {
            int buid = BUserHandle.getUid(userId, BPackageManagerService.get().getAppId(packageName));
            Map<String, ProcessRecord> process = mProcessMap.get(buid);
            if (process == null)
                return;
            for (ProcessRecord value : process.values()) {
                value.kill();
                mPidsSelfLocked.remove(value);
            }
            mProcessMap.remove(buid);
        }
    }

    /**
     * Returns a snapshot of all processes for the given package and user.
     *
     * @param packageName the package name
     * @param userId      virtual user id
     * @return list of matching {@link ProcessRecord} objects (never {@code null})
     */
    public List<ProcessRecord> getPackageProcessAsUser(String packageName, int userId) {
        synchronized (mProcessLock) {
            int buid = BUserHandle.getUid(userId, BPackageManagerService.get().getAppId(packageName));
            Map<String, ProcessRecord> process = mProcessMap.get(buid);
            if (process == null)
                return new ArrayList<>();
            return new ArrayList<>(process.values());
        }
    }

    /**
     * Resolves the virtual app-id from a caller's PID.  Falls back to
     * the package name if the PID is not tracked.
     *
     * @param pid         the caller's host PID
     * @param packageName fallback package name
     * @return the virtual app-id
     */
    public int getBUidByPidOrPackageName(int pid, String packageName) {
        synchronized (mProcessLock) {
            ProcessRecord callingProcess = BProcessManagerService.get().findProcessByPid(pid);
            if (callingProcess == null) {
                return BPackageManagerService.get().getAppId(packageName);
            }
            return BUserHandle.getAppId(callingProcess.buid);
        }
    }

    /**
     * Returns the virtual user id for the given calling PID.
     *
     * @param callingPid the host PID to look up
     * @return the virtual user id, or {@code 0} if the PID is unknown
     */
    public int getUserIdByCallingPid(int callingPid) {
        synchronized (mProcessLock) {
            ProcessRecord callingProcess = BProcessManagerService.get().findProcessByPid(callingPid);
            if (callingProcess == null) {
                return 0;
            }
            return callingProcess.userId;
        }
    }

    /**
     * Finds a process record by its host PID.
     *
     * @param pid the host PID
     * @return the matching {@link ProcessRecord}, or {@code null} if not found
     */
    public ProcessRecord findProcessByPid(int pid) {
        synchronized (mPidsSelfLocked) {
            for (ProcessRecord processRecord : mPidsSelfLocked) {
                if (processRecord.pid == pid)
                    return processRecord;
            }
            return null;
        }
    }

    /**
     * Retrieves the host process name for a given PID from the
     * ActivityManager.
     *
     * @param context the application context
     * @param pid     the host PID
     * @return the process name
     * @throws RuntimeException if the PID is not found among running processes
     */
    private static String getProcessName(Context context, int pid) {
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == pid) {
                processName = info.processName;
                break;
            }
        }
        if (processName == null) {
            throw new RuntimeException("processName = null");
        }
        return processName;
    }

    /**
     * Resolves the host PID for a given process name.
     *
     * @param context     the application context
     * @param processName the process name to look up
     * @return the PID, or {@code -1} if not found
     */
    public static int getPid(Context context, String processName) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
                if (runningAppProcess.processName.equals(processName)) {
                    return runningAppProcess.pid;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Writes the process name into a virtual {@code /proc/<pid>/cmdline}
     * file so that native reads of {@code /proc/self/cmdline} return the
     * expected virtual process name.
     *
     * @param record the process record
     */
    private static void createProc(ProcessRecord record) {
        File cmdline = new File(BEnvironment.getProcDir(record.bpid), "cmdline");
        try {
            FileUtils.writeToFile(record.processName.getBytes(), cmdline);
        } catch (IOException ignored) {
        }
    }

    /**
     * Removes the virtual {@code /proc/<pid>} directory for a dead process.
     *
     * @param record the process record
     */
    private static void removeProc(ProcessRecord record) {
        FileUtils.deleteDir(BEnvironment.getProcDir(record.bpid));
    }

    /**
     * Called when the system is ready.  Cleans up stale proc directories
     * left over from previous sessions.
     */
    @Override
    public void systemReady() {
        FileUtils.deleteDir(BEnvironment.getProcDir());
    }
}
