package top.niunaijun.blackbox.fake.delegate;

import android.app.IServiceConnection;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.HashMap;
import java.util.Map;

import black.android.app.BRIServiceConnectionO;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Delegate that wraps an {@link IServiceConnection} to intercept service connection
 * callbacks within the virtual environment. Manages a cache of delegate instances
 * keyed by binder identity and handles API differences between Android Oreo and
 * earlier versions. Properly cleans up when the original connection's binder dies.
 */
public class ServiceConnectionDelegate extends IServiceConnection.Stub {
    private static final Map<IBinder, ServiceConnectionDelegate> sServiceConnectDelegate = new HashMap<>();
    private final IServiceConnection mConn;
    private final ComponentName mComponentName;

    /**
     * Private constructor to create a delegate for the given service connection.
     *
     * @param mConn           the original service connection to wrap
     * @param targetComponent the component name of the target service
     */
    private ServiceConnectionDelegate(IServiceConnection mConn, ComponentName targetComponent) {
        this.mConn = mConn;
        this.mComponentName = targetComponent;
    }

    /**
     * Retrieves the existing delegate for the given binder, or null if none exists.
     *
     * @param iBinder the binder token to look up
     * @return the associated ServiceConnectionDelegate, or null
     */
    public static ServiceConnectionDelegate getDelegate(IBinder iBinder) {
        return sServiceConnectDelegate.get(iBinder);
    }

    /**
     * Creates or retrieves a proxy delegate for the given service connection. A death
     * recipient is registered to clean up the delegate when the connection's binder dies.
     *
     * @param base   the original IServiceConnection to proxy
     * @param intent the Intent used to bind the service, used to extract the ComponentName
     * @return the proxy delegate wrapping the service connection
     */
    public static IServiceConnection createProxy(IServiceConnection base, Intent intent) {
        final IBinder iBinder = base.asBinder();
        ServiceConnectionDelegate delegate = sServiceConnectDelegate.get(iBinder);
        if (delegate == null) {
            try {
                iBinder.linkToDeath(new IBinder.DeathRecipient() {
                    @Override
                    public void binderDied() {
                        sServiceConnectDelegate.remove(iBinder);
                        iBinder.unlinkToDeath(this, 0);
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            delegate = new ServiceConnectionDelegate(base, intent.getComponent());
            sServiceConnectDelegate.put(iBinder, delegate);
        }
        return delegate;
    }

    /**
     * Called when a service connection is established. Delegates to the three-argument
     * overload with dead=false.
     *
     * @param name    the ComponentName of the connected service
     * @param service the IBinder of the connected service
     * @throws RemoteException if the remote call fails
     */
    @Override
    public void connected(ComponentName name, IBinder service) throws RemoteException {
        connected(name, service, false);
    }

    /**
     * Called when a service connection is established, with support for indicating
     * whether the service is dead. Handles API differences between Oreo and earlier.
     *
     * @param name    the ComponentName of the connected service
     * @param service the IBinder of the connected service
     * @param dead    whether the service connection is dead
     * @throws RemoteException if the remote call fails
     */
    public void connected(ComponentName name, IBinder service, boolean dead) throws RemoteException {
        if (BuildCompat.isOreo()) {
            BRIServiceConnectionO.get(mConn).connected(mComponentName, service, dead);
        } else {
            mConn.connected(name, service);
        }
    }
}
