package top.niunaijun.blackbox.proxy.record;

import android.content.Intent;

/**
 * Carries the state needed to deliver a broadcast to a virtual app through a proxy receiver.
 * Stores the target broadcast Intent and the virtual user ID. Serialized into the proxy
 * receiver's Intent via {@link #saveStub} and deserialized via {@link #create}.
 *
 * @author Milk
 */
public class ProxyBroadcastRecord {
    /** The target broadcast Intent to deliver to the virtual app. */
    public Intent mIntent;
    /** The virtual user ID under which the broadcast is delivered. */
    public int mUserId;

    /**
     * Constructs a new ProxyBroadcastRecord.
     *
     * @param intent the target broadcast Intent
     * @param userId the virtual user ID
     */
    public ProxyBroadcastRecord(Intent intent, int userId) {
        mIntent = intent;
        mUserId = userId;
    }

    /**
     * Serializes the broadcast record data into the proxy receiver's Intent.
     *
     * @param shadow the proxy stub Intent to store data into
     * @param target the target broadcast Intent for the virtual app
     * @param userId the virtual user ID
     */
    public static void saveStub(Intent shadow, Intent target, int userId) {
        shadow.putExtra("_B_|_target_", target);
        shadow.putExtra("_B_|_user_id_", userId);
    }

    /**
     * Deserializes a {@link ProxyBroadcastRecord} from a proxy receiver's Intent.
     *
     * @param intent the Intent received by the proxy BroadcastReceiver
     * @return a populated {@link ProxyBroadcastRecord}
     */
    public static ProxyBroadcastRecord create(Intent intent) {
        Intent target = intent.getParcelableExtra("_B_|_target_");
        int userId = intent.getIntExtra("_B_|_user_id_", 0);
        return new ProxyBroadcastRecord(target, userId);
    }

    /**
     * Returns a string representation of this record for debugging.
     *
     * @return a string containing the intent and user ID
     */
    @Override
    public String toString() {
        return "ProxyBroadcastRecord{" +
                "mIntent=" + mIntent +
                ", mUserId=" + mUserId +
                '}';
    }
}
