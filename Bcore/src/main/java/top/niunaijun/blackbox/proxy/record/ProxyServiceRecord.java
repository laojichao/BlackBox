package top.niunaijun.blackbox.proxy.record;

import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.IBinder;

import top.niunaijun.blackbox.utils.compat.BundleCompat;

/**
 * Carries the state needed to run a virtual app's Service through a proxy stub.
 * Stores the service Intent, service metadata, token binder, user ID, and start ID.
 * Serialized into the proxy Service's Intent via {@link #saveStub} and deserialized
 * via {@link #create} to bridge between the proxy component and the real virtual Service.
 *
 * @author Milk
 */
public class ProxyServiceRecord {
    /** The target Intent to pass to the virtual Service. */
    public Intent mServiceIntent;
    /** Metadata for the target service (name, package, permissions, etc.). */
    public ServiceInfo mServiceInfo;
    /** Binder token identifying this service instance in the server process. */
    public IBinder mToken;
    /** The virtual user ID under which the service runs. */
    public int mUserId;
    /** The start ID for this particular service start request. */
    public int mStartId;

    /**
     * Constructs a new ProxyServiceRecord.
     *
     * @param serviceIntent the target Intent for the virtual Service
     * @param serviceInfo   the target service's metadata
     * @param token         the binder token for the service instance
     * @param userId        the virtual user ID
     * @param startId       the start ID for this request
     */
    public ProxyServiceRecord(Intent serviceIntent, ServiceInfo serviceInfo, IBinder token, int userId, int startId) {
        mServiceIntent = serviceIntent;
        mServiceInfo = serviceInfo;
        mUserId = userId;
        mStartId = startId;
        mToken = token;
    }

    /**
     * Serializes the service record data into the proxy Service's Intent.
     *
     * @param shadow      the proxy stub Intent to store data into
     * @param target      the target Intent for the virtual Service
     * @param serviceInfo the target service's metadata
     * @param token       the binder token for the service instance
     * @param userId      the virtual user ID
     * @param startId     the start ID for this request
     */
    public static void saveStub(Intent shadow, Intent target, ServiceInfo serviceInfo, IBinder token, int userId, int startId) {
        shadow.putExtra("_B_|_target_", target);
        shadow.putExtra("_B_|_service_info_", serviceInfo);
        shadow.putExtra("_B_|_user_id_", userId);
        shadow.putExtra("_B_|_start_id_", startId);
        BundleCompat.putBinder(shadow, "_B_|_token_", token);
    }

    /**
     * Deserializes a {@link ProxyServiceRecord} from a proxy Service's Intent.
     *
     * @param intent the Intent received by the proxy Service
     * @return a populated {@link ProxyServiceRecord}
     */
    public static ProxyServiceRecord create(Intent intent) {
        Intent target = intent.getParcelableExtra("_B_|_target_");
        ServiceInfo serviceInfo = intent.getParcelableExtra("_B_|_service_info_");
        int userId = intent.getIntExtra("_B_|_user_id_", 0);
        int startId = intent.getIntExtra("_B_|_start_id_", 0);
        IBinder token = BundleCompat.getBinder(intent, "_B_|_token_");
        return new ProxyServiceRecord(target, serviceInfo, token, userId, startId);
    }
}
