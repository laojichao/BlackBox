package black.android.app;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Reflection mirror for {@code android.app.ActivityManager}.
 * Exposes hidden static constants related to activity start results
 * and intent resolution status codes.
 */
@BClassName("android.app.ActivityManager")
public interface ActivityManager {
    /** Start result: the intent could not be resolved to an activity. */
    @BStaticField
    int START_INTENT_NOT_RESOLVED();

    /** Start result: the target activity belongs to a different user. */
    @BStaticField
    int START_NOT_CURRENT_USER_ACTIVITY();

    /** Start result: the activity was started successfully. */
    @BStaticField
    int START_SUCCESS();

    /** Start result: an existing task was brought to the foreground. */
    @BStaticField
    int START_TASK_TO_FRONT();
}
