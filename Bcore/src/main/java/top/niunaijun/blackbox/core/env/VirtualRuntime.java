package top.niunaijun.blackbox.core.env;

import android.content.pm.ApplicationInfo;

import black.android.ddm.BRDdmHandleAppName;
import black.android.os.BRProcess;

/**
 * Holds the runtime identity (process name and initial package name) of
 * the virtual application running inside the BlackBox environment.
 *
 * <p>When a virtual app is launched, {@link #setupRuntime} is called once
 * to record the process name and package name, and to propagate the
 * process name into the Dalvik/ART VM internals so that calls such as
 * {@code ActivityThread.getProcessName()} return the expected value.</p>
 */
public class VirtualRuntime {

    /** The package name of the initially-launched virtual app. */
    private static String sInitialPackageName;
    /** The virtual process name (e.g. {@code com.example.app:virtual}). */
    private static String sProcessName;

    /**
     * Returns the virtual process name set during {@link #setupRuntime}.
     *
     * @return the process name, or {@code null} if the runtime has not
     *         been initialized yet
     */
    public static String getProcessName() {
        return sProcessName;
    }

    /**
     * Returns the initial package name of the virtual application.
     *
     * @return the package name from the {@link ApplicationInfo}, or
     *         {@code null} if the runtime has not been initialized yet
     */
    public static String getInitialPackageName() {
        return sInitialPackageName;
    }

    /**
     * Initializes the virtual runtime with the given process name and
     * application info.  This method is idempotent -- subsequent calls
     * are ignored once the runtime has been set up.
     *
     * <p>In addition to recording the names, it patches the ART
     * {@code Process} argv[0] and the DDM app-name tag so that all
     * runtime introspection APIs return the virtual process name.</p>
     *
     * @param processName the virtual process name
     * @param appInfo     the application info of the virtual app
     */
    public static void setupRuntime(String processName, ApplicationInfo appInfo) {
        if (sProcessName != null) {
            return;
        }
        sInitialPackageName = appInfo.packageName;
        sProcessName = processName;
        BRProcess.get().setArgV0(processName);
        BRDdmHandleAppName.get().setAppName(processName, 0);
    }
}
