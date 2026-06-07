package top.niunaijun.blackbox.proxy.record;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.IBinder;

import top.niunaijun.blackbox.utils.compat.BundleCompat;

/**
 * Carries the state needed to launch a virtual app's Activity through a proxy stub.
 * Stores the target Intent, activity metadata, user ID, and the remote activity record
 * binder. Serialized into proxy Activity intents via {@link #saveStub} and deserialized
 * via {@link #create} to bridge between the proxy component and the real virtual Activity.
 *
 * @author Milk
 */
public class ProxyActivityRecord {
    /** The virtual user ID under which the activity runs. */
    public int mUserId;
    /** Metadata for the target activity (label, theme, launch mode, etc.). */
    public ActivityInfo mActivityInfo;
    /** The Intent that will launch the real virtual Activity. */
    public Intent mTarget;
    /** Binder handle to the remote ActivityRecord in the server process. */
    public IBinder mActivityRecord;

    /**
     * Constructs a new ProxyActivityRecord.
     *
     * @param userId       the virtual user ID
     * @param activityInfo the target activity's metadata
     * @param target       the Intent to launch the real virtual Activity
     * @param activityRecord the binder for the remote ActivityRecord
     */
    public ProxyActivityRecord(int userId, ActivityInfo activityInfo, Intent target, IBinder activityRecord) {
        mUserId = userId;
        mActivityInfo = activityInfo;
        mTarget = target;
        mActivityRecord = activityRecord;
    }

    /**
     * Serializes the activity record data into the proxy intent's extras so it can be
     * reconstructed when the proxy Activity is launched.
     *
     * @param shadow         the proxy stub Intent to store data into
     * @param target         the target Intent for the real virtual Activity
     * @param activityInfo   the target activity's metadata
     * @param activityRecord the binder for the remote ActivityRecord
     * @param userId         the virtual user ID
     */
    public static void saveStub(Intent shadow, Intent target, ActivityInfo activityInfo, IBinder activityRecord, int userId) {
        shadow.putExtra("_B_|_user_id_", userId);
        shadow.putExtra("_B_|_activity_info_", activityInfo);
        shadow.putExtra("_B_|_target_", target);
        BundleCompat.putBinder(shadow, "_B_|_activity_record_v_", activityRecord);
    }

    /**
     * Deserializes a {@link ProxyActivityRecord} from a proxy Activity's launch Intent.
     *
     * @param intent the Intent received by the proxy Activity
     * @return a populated {@link ProxyActivityRecord}
     */
    public static ProxyActivityRecord create(Intent intent) {
        int userId = intent.getIntExtra("_B_|_user_id_", 0);
        ActivityInfo activityInfo = intent.getParcelableExtra("_B_|_activity_info_");
        Intent target = intent.getParcelableExtra("_B_|_target_");
        IBinder activityRecord = BundleCompat.getBinder(intent, "_B_|_activity_record_v_");
        return new ProxyActivityRecord(userId, activityInfo, target, activityRecord);
    }
}
