package top.niunaijun.blackbox.core.system.am;

import android.os.IBinder;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-user container for virtual activity manager state within the virtual environment.
 * Holds the independent subsystems that manage a single virtual user's component lifecycle:
 * activity stack, active services, and pending intent records.
 *
 * <p>Used by {@link BActivityManagerService} to isolate state between different virtual users,
 * ensuring that activities, services, and intents from one user do not interfere with another.</p>
 */
public class UserSpace {
    /** Manages started and bound services for this virtual user. */
    public final ActiveServices mActiveServices = new ActiveServices();
    /** Manages the activity back stack and task records for this virtual user. */
    public final ActivityStack mStack = new ActivityStack();
    /** Maps IBinder tokens to pending intent sender records for this virtual user. */
    public final Map<IBinder, PendingIntentRecord> mIntentSenderRecords = new HashMap<>();
}
