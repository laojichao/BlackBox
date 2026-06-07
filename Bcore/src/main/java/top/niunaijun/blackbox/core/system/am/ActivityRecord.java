package top.niunaijun.blackbox.core.system.am;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Binder;
import android.os.IBinder;

import top.niunaijun.blackbox.core.system.ProcessRecord;


/**
 * Represents a single activity instance within the virtual environment's back stack.
 * Virtualizes the internal ActivityRecord of the Android {@link android.app.ActivityManager},
 * tracking the activity's intent, component, token, lifecycle state, and the hosting process.
 *
 * <p>Extends {@link Binder} so it can be passed across IPC as an IBinder token, enabling
 * the virtual activity manager to identify activities by their token reference.</p>
 */
public class ActivityRecord extends Binder {
    /** The task (back stack) this activity belongs to. */
    public TaskRecord task;
    /** The IBinder token identifying this activity instance. */
    public IBinder token;
    /** The token of the activity that started this one (for result delivery). */
    public IBinder resultTo;
    /** The resolved activity info from the package manager. */
    public ActivityInfo info;
    /** The component name (package + class) of this activity. */
    public ComponentName component;
    /** The original launch intent for this activity. */
    public Intent intent;
    /** The virtual user ID this activity runs under. */
    public int userId;
    /** Whether this activity has been finished and should be removed from the stack. */
    public boolean finished;
    /** The hosting process record for this activity. */
    public ProcessRecord processRecord;

    /**
     * Factory method that creates a new {@link ActivityRecord} from the given intent and info.
     *
     * @param intent   the launch intent
     * @param info     the resolved activity info
     * @param resultTo the token of the calling activity (for {@code startActivityForResult}); may be null
     * @param userId   the virtual user ID
     * @return a new ActivityRecord with the fields populated
     */
    public static ActivityRecord create(Intent intent, ActivityInfo info, IBinder resultTo, int userId) {
        ActivityRecord record = new ActivityRecord();
        record.intent = intent;
        record.info = info;
        record.component = new ComponentName(info.packageName, info.name);
        record.resultTo = resultTo;
        record.userId = userId;
        return record;
    }


}
