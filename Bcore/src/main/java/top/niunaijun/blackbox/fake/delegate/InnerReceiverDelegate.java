package top.niunaijun.blackbox.fake.delegate;

import android.content.IIntentReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import black.android.content.BRIIntentReceiver;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.proxy.record.ProxyBroadcastRecord;

/**
 * Delegate that wraps an {@link IIntentReceiver} to intercept broadcast delivery
 * within the virtual environment. Manages a cache of delegate instances keyed by
 * binder identity and properly unregisters when the original receiver dies.
 * Extracts the original intent from {@link ProxyBroadcastRecord} before forwarding.
 */
public class InnerReceiverDelegate extends IIntentReceiver.Stub {
    public static final String TAG = "InnerReceiverDelegate";

    private static final Map<IBinder, InnerReceiverDelegate> sInnerReceiverDelegate = new HashMap<>();
    private final WeakReference<IIntentReceiver> mIntentReceiver;

    /**
     * Private constructor to create a delegate for the given receiver.
     *
     * @param iIntentReceiver the original intent receiver to wrap
     */
    private InnerReceiverDelegate(IIntentReceiver iIntentReceiver) {
        this.mIntentReceiver = new WeakReference<>(iIntentReceiver);
    }

    /**
     * Retrieves the existing delegate for the given binder, or null if none exists.
     *
     * @param iBinder the binder token to look up
     * @return the associated InnerReceiverDelegate, or null
     */
    public static InnerReceiverDelegate getDelegate(IBinder iBinder) {
        return sInnerReceiverDelegate.get(iBinder);
    }

    /**
     * Creates or retrieves a proxy delegate for the given intent receiver. If the
     * receiver is already a delegate, it is returned as-is. A death recipient is
     * registered to clean up the delegate when the receiver's binder dies.
     *
     * @param base the original IIntentReceiver to proxy
     * @return the proxy delegate wrapping the receiver
     */
    public static IIntentReceiver createProxy(IIntentReceiver base) {
        if (base instanceof InnerReceiverDelegate) {
            return base;
        }
        final IBinder iBinder = base.asBinder();
        InnerReceiverDelegate delegate = sInnerReceiverDelegate.get(iBinder);
        if (delegate == null) {
            try {
                iBinder.linkToDeath(new DeathRecipient() {
                    @Override
                    public void binderDied() {
                        sInnerReceiverDelegate.remove(iBinder);
                        iBinder.unlinkToDeath(this, 0);
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            delegate = new InnerReceiverDelegate(base);
            sInnerReceiverDelegate.put(iBinder, delegate);
        }
        return delegate;
    }

    /**
     * Receives a broadcast intent, extracts the original intent from the proxy record,
     * sets the correct class loader, and forwards it to the wrapped receiver.
     *
     * @param intent       the received broadcast intent
     * @param resultCode   the result code
     * @param data         the result data string
     * @param extras       the extras bundle
     * @param ordered      whether the broadcast is ordered
     * @param sticky       whether the broadcast is sticky
     * @param sendingUser  the user ID of the sender
     * @throws RemoteException if the remote receiver fails
     */
    @Override
    public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) throws RemoteException {
        intent.setExtrasClassLoader(BActivityThread.getApplication().getClassLoader());
        ProxyBroadcastRecord proxyBroadcastRecord = ProxyBroadcastRecord.create(intent);
        Intent perIntent;
        if (proxyBroadcastRecord.mIntent != null) {
            proxyBroadcastRecord.mIntent.setExtrasClassLoader(BActivityThread.getApplication().getClassLoader());
            perIntent = proxyBroadcastRecord.mIntent;
        } else {
            perIntent = intent;
        }
        IIntentReceiver iIntentReceiver = mIntentReceiver.get();
        if (iIntentReceiver != null) {
            BRIIntentReceiver.get(iIntentReceiver).performReceive(perIntent, resultCode, data, extras, ordered, sticky, sendingUser);
        }
    }
}
