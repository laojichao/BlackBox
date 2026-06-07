package black.android.app;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.ActivityTaskManager} (API 29+).
 * Provides access to the IActivityTaskManager service singleton that
 * handles activity task management, introduced when activity management
 * was separated from ActivityManagerService.
 */
@BClassName("android.app.ActivityTaskManager")
public interface ActivityTaskManager {

    /** Returns the IActivityTaskManager service interface instance. */
    @BStaticMethod
    Object getService();

    /** Returns the hidden static {@code IActivityTaskManagerSingleton} field. */
    @BStaticField
    Object IActivityTaskManagerSingleton();
}
