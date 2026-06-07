package top.niunaijun.blackbox.entity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks the runtime state of a bound or started {@link Service} within the BlackBox virtual environment.
 * <p>
 * This record maintains the service instance, its active bind connections (keyed by {@link Intent}),
 * the current start ID, and whether a rebind is in progress. Each connection is represented by a
 * {@link BoundInfo} that holds the {@link IBinder} and a reference count for that binding.
 * </p>
 */
public class ServiceRecord {
    /** The live Service instance this record is tracking. */
    private Service mService;

    /** Map of intent-based connection keys to their binding information. */
    private Map<Intent.FilterComparison, BoundInfo> mBounds = new HashMap<>();

    /** Whether the service is currently in a rebind state after all clients unbound. */
    private boolean rebind;

    /** The most recent start ID assigned via {@code startService}, used for safe stop delivery. */
    private int mStartId;

    /**
     * Holds binding information for a single {@link Intent}-based connection to the service.
     * <p>
     * Each {@link BoundInfo} tracks the {@link IBinder} returned to the client and
     * a thread-safe count of how many clients are currently bound via this intent.
     * </p>
     */
    public class BoundInfo {
        /** The binder handle returned to clients for this connection. */
        private IBinder mIBinder;

        /** Thread-safe count of active bind operations for this connection. */
        private AtomicInteger mBindCount = new AtomicInteger(0);

        /**
         * Atomically increments the bind count and returns the new value.
         *
         * @return the bind count after incrementing
         */
        public int incrementAndGetBindCount() {
            return mBindCount.incrementAndGet();
        }

        /**
         * Atomically decrements the bind count and returns the new value.
         *
         * @return the bind count after decrementing
         */
        public int decrementAndGetBindCount() {
            return mBindCount.decrementAndGet();
        }

        /**
         * Returns the binder handle for this connection.
         *
         * @return the {@link IBinder} for this binding
         */
        public IBinder getIBinder() {
            return mIBinder;
        }

        /**
         * Sets the binder handle for this connection.
         *
         * @param IBinder the {@link IBinder} to associate with this binding
         */
        public void setIBinder(IBinder IBinder) {
            mIBinder = IBinder;
        }
    }

    /**
     * Returns the most recent start ID for this service.
     *
     * @return the current start ID
     */
    public int getStartId() {
        return mStartId;
    }

    /**
     * Sets the start ID for this service.
     *
     * @param startId the start ID to set
     */
    public void setStartId(int startId) {
        mStartId = startId;
    }

    /**
     * Returns the tracked {@link Service} instance.
     *
     * @return the service instance
     */
    public Service getService() {
        return mService;
    }

    /**
     * Sets the {@link Service} instance to track.
     *
     * @param service the service instance
     */
    public void setService(Service service) {
        mService = service;
    }

    /**
     * Retrieves the {@link IBinder} associated with the given intent's connection.
     * Creates a new {@link BoundInfo} if none exists for the intent.
     *
     * @param intent the intent identifying the bind connection
     * @return the binder for this connection, or {@code null} if not yet set
     */
    public IBinder getBinder(Intent intent) {
        BoundInfo boundInfo = getOrCreateBoundInfo(intent);
        return boundInfo.getIBinder();
    }

    /**
     * Checks whether a binder has been set for the given intent's connection.
     *
     * @param intent the intent identifying the bind connection
     * @return {@code true} if a binder exists for this intent
     */
    public boolean hasBinder(Intent intent) {
        BoundInfo boundInfo = getOrCreateBoundInfo(intent);
        return boundInfo.getIBinder() != null;
    }

    /**
     * Registers a binder for the given intent and links a death recipient that
     * automatically removes the connection when the remote process dies.
     *
     * @param intent  the intent identifying the bind connection
     * @param iBinder the binder handle to associate with this connection
     */
    public void addBinder(Intent intent, final IBinder iBinder) {
        final Intent.FilterComparison filterComparison = new Intent.FilterComparison(intent);
        BoundInfo boundInfo = getOrCreateBoundInfo(intent);
        if (boundInfo == null) {
            boundInfo = new BoundInfo();
            mBounds.put(filterComparison, boundInfo);
        }
        boundInfo.setIBinder(iBinder);
        try {
            iBinder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    iBinder.unlinkToDeath(this, 0);
                    mBounds.remove(filterComparison);
                }
            }, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Atomically increments the bind count for the connection identified by the given intent.
     *
     * @param intent the intent identifying the bind connection
     * @return the bind count after incrementing
     */
    public int incrementAndGetBindCount(Intent intent) {
        BoundInfo boundInfo = getOrCreateBoundInfo(intent);
        return boundInfo.incrementAndGetBindCount();
    }

    /**
     * Decrements the bind count for the connection identified by the given intent.
     *
     * @param intent the intent identifying the bind connection
     * @return {@code true} if the bind count reached zero (or no connection existed),
     *         indicating the service may be eligible for unbinding; {@code false} otherwise
     */
    public boolean decreaseConnectionCount(Intent intent) {
        Intent.FilterComparison filterComparison = new Intent.FilterComparison(intent);
        BoundInfo boundInfo = mBounds.get(filterComparison);
        if (boundInfo == null)
            return true;
        int i = boundInfo.decrementAndGetBindCount();
        if (i <= 0) {
//            mBounds.remove(filterComparison);
            return true;
        }
        return false;
    }

    /**
     * Returns the existing {@link BoundInfo} for the given intent, or creates and registers
     * a new one if none exists.
     *
     * @param intent the intent identifying the bind connection
     * @return the bound information for this intent, never {@code null}
     */
    public BoundInfo getOrCreateBoundInfo(Intent intent) {
        Intent.FilterComparison filterComparison = new Intent.FilterComparison(intent);
        BoundInfo boundInfo = mBounds.get(filterComparison);
        if (boundInfo == null) {
            boundInfo = new BoundInfo();
            mBounds.put(filterComparison, boundInfo);
        }
        return boundInfo;
    }

    /**
     * Returns whether this service is in a rebind state.
     *
     * @return {@code true} if the service is rebound after all clients had previously unbound
     */
    public boolean isRebind() {
        return rebind;
    }

    /**
     * Sets the rebind state for this service.
     *
     * @param rebind {@code true} to mark the service as being in a rebind state
     */
    public void setRebind(boolean rebind) {
        this.rebind = rebind;
    }
}
