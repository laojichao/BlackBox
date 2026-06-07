package top.niunaijun.blackbox.fake.frameworks;

import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.ParameterizedType;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.utils.Reflector;

/**
 * Abstract base class for all BlackBox system service managers. Handles automatic
 * connection management to the service binder, including lazy initialization,
 * liveness checking via ping, and automatic reconnection on binder death.
 *
 * @param <Service> the IInterface type of the remote service
 */
public abstract class BlackManager<Service extends IInterface> {
    public static final String TAG = "BlackManager";

    private Service mService;

    /**
     * Returns the service name used to look up the binder from the ServiceManager.
     *
     * @return the service name string
     */
    protected abstract String getServiceName();

    /**
     * Returns the remote service proxy, reconnecting if the current binder is dead
     * or unavailable. Registers a death recipient to clear the cached proxy on failure.
     *
     * @return the service proxy, or null if connection fails
     */
    public Service getService() {
        if (mService != null && mService.asBinder().pingBinder() && mService.asBinder().isBinderAlive()) {
            return mService;
        }
        try {
            mService = Reflector.on(getTClass().getName() + "$Stub").method("asInterface", IBinder.class)
                    .call(BlackBoxCore.get().getService(getServiceName()));
            mService.asBinder().linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    mService.asBinder().unlinkToDeath(this, 0);
                    mService = null;
                }
            }, 0);
            return getService();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private Class<Service> getTClass() {
        return (Class<Service>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
