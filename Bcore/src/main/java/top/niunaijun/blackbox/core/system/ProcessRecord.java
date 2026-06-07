package top.niunaijun.blackbox.core.system;

import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.ConditionVariable;
import android.os.IInterface;
import android.os.Process;

import java.util.Arrays;

import top.niunaijun.blackbox.core.IBActivityThread;
import top.niunaijun.blackbox.entity.AppConfig;
import top.niunaijun.blackbox.proxy.ProxyManifest;

/**
 * Represents a single virtual application process managed by
 * {@link BProcessManagerService}.
 *
 * <p>Extends {@link Binder} so that it can be passed as a token through
 * IPC bundles.  Holds the process's identity (UIDs, PID, user id), the
 * remote {@link IBActivityThread} reference, and an
 * {@link ConditionVariable} used as a one-shot initialization barrier
 * that callers can block on until the process is fully attached.</p>
 *
 * <p>Key identity fields:</p>
 * <ul>
 *   <li>{@link #buid} -- virtual app-id (package-level)</li>
 *   <li>{@link #bpid} -- stub host PID slot index</li>
 *   <li>{@link #userId} -- virtual user id</li>
 *   <li>{@link #callingBUid} -- virtual app-id of the caller that
 *       started this process</li>
 * </ul>
 */
public class ProcessRecord extends Binder {
    /** The application info for this package. */
    public final ApplicationInfo info;
    /** The virtual process name (e.g. {@code com.example.app:virtual}). */
    final public String processName;
    /** Remote binder to the virtual app's activity thread. */
    public IBActivityThread bActivityThread;
    /** Remote binder to the ApplicationThread proxy. */
    public IInterface appThread;
    /** Real host UID (same for all virtual processes on this device). */
    public int uid;
    /** Real host PID of the stub process. */
    public int pid;
    /** Virtual app-id for this package (package-level identity). */
    public int buid;
    /** Stub PID slot index (used to name the host process). */
    public int bpid;
    /** Virtual app-id of the process that initiated this one. */
    public int callingBUid;
    /** Virtual user id under which this process runs. */
    public int userId;

    /**
     * One-shot barrier that blocks until the remote app thread has
     * been attached.  Opened by {@link BProcessManagerService}.
     */
    public ConditionVariable initLock = new ConditionVariable();

    /**
     * Creates a new process record.
     *
     * @param info        the application info of the virtual app
     * @param processName the target process name
     */
    public ProcessRecord(ApplicationInfo info, String processName) {
        this.info = info;
        this.processName = processName;
    }

    /**
     * Returns the virtual app-id of the process that started this one.
     *
     * @return the caller's virtual app-id
     */
    public int getCallingBUid() {
        return callingBUid;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{processName, pid, buid, bpid, uid, pid, userId});
    }

    /**
     * Returns the ContentProvider authority of the stub proxy used to
     * communicate with this process.
     *
     * @return the provider authority string
     */
    public String getProviderAuthority() {
        return ProxyManifest.getProxyAuthorities(bpid);
    }

    /**
     * Builds an {@link AppConfig} snapshot from this record's current
     * state, suitable for passing to the virtual app process over IPC.
     *
     * @return a new {@link AppConfig} populated with this record's fields
     */
    public AppConfig getClientConfig() {
        AppConfig config = new AppConfig();
        config.packageName = info.packageName;
        config.processName = processName;
        config.bpid = bpid;
        config.buid = buid;
        config.uid = uid;
        config.callingBUid = callingBUid;
        config.userId = userId;
        config.token = this;
        return config;
    }

    /**
     * Kills the underlying host process if a valid PID is available.
     */
    public void kill() {
        if (pid > 0) {
            try {
                Process.killProcess(pid);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the package name from the stored application info.
     *
     * @return the package name
     */
    public String getPackageName() {
        return info.packageName;
    }
}
