package top.niunaijun.blackbox.proxy.record;

import android.content.Intent;

/**
 * Carries the state needed to redirect a pending activity result through a proxy stub.
 * Stores the target Intent and the virtual user ID for activities launched via
 * {@code startActivityForResult} or pending intents. Serialized into the proxy
 * activity's Intent via {@link #saveStub} and deserialized via {@link #create}.
 *
 * @author Milk
 */
public class ProxyPendingRecord {
    /** The virtual user ID under which the pending activity runs. */
    public int mUserId;
    /** The Intent that will launch the real virtual Activity. */
    public Intent mTarget;

    /**
     * Constructs a new ProxyPendingRecord.
     *
     * @param target the target Intent for the real virtual Activity
     * @param userId the virtual user ID
     */
    public ProxyPendingRecord(Intent target, int userId) {
        mUserId = userId;
        mTarget = target;
    }

    /**
     * Serializes the pending record data into the proxy activity's Intent.
     *
     * @param shadow the proxy stub Intent to store data into
     * @param target the target Intent for the real virtual Activity
     * @param userId the virtual user ID
     */
    public static void saveStub(Intent shadow, Intent target, int userId) {
        shadow.putExtra("_B_|_P_user_id_", userId);
        shadow.putExtra("_B_|_P_target_", target);
    }

    /**
     * Deserializes a {@link ProxyPendingRecord} from a proxy activity's Intent.
     *
     * @param intent the Intent received by the proxy PendingActivity
     * @return a populated {@link ProxyPendingRecord}
     */
    public static ProxyPendingRecord create(Intent intent) {
        int userId = intent.getIntExtra("_B_|_P_user_id_", 0);
        Intent target = intent.getParcelableExtra("_B_|_P_target_");
        return new ProxyPendingRecord(target, userId);
    }

    /**
     * Returns a string representation of this record for debugging.
     *
     * @return a string containing the user ID and target intent
     */
    @Override
    public String toString() {
        return "ProxyPendingActivityRecord{" +
                "mUserId=" + mUserId +
                ", mTarget=" + mTarget +
                '}';
    }
}
