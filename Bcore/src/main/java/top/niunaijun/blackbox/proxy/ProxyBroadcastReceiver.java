package top.niunaijun.blackbox.proxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.entity.am.PendingResultData;
import top.niunaijun.blackbox.proxy.record.ProxyBroadcastRecord;

/**
 * Proxy BroadcastReceiver that intercepts broadcast intents on behalf of virtual apps.
 * Extracts the real broadcast data from {@link ProxyBroadcastRecord} and schedules it
 * through {@link top.niunaijun.blackbox.fake.frameworks.BActivityManager} so that the
 * virtual app's registered receivers are invoked within the virtual environment.
 *
 * @author BlackBox
 */
public class ProxyBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "ProxyBroadcastReceiver";

    /**
     * Receives a broadcast intent, extracts the proxy record, and schedules the real
     * broadcast delivery to virtual app receivers via the activity manager.
     *
     * @param context the Context in which the receiver is running
     * @param intent  the Intent being received, carrying the proxy broadcast record
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setExtrasClassLoader(context.getClassLoader());
        ProxyBroadcastRecord record = ProxyBroadcastRecord.create(intent);
        if (record.mIntent == null) {
            return;
        }
        PendingResult pendingResult = goAsync();
        try {
            BlackBoxCore.getBActivityManager().scheduleBroadcastReceiver(record.mIntent, new PendingResultData(pendingResult), record.mUserId);
        } catch (RemoteException e) {
            pendingResult.finish();
        }
    }
}