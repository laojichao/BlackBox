package top.niunaijun.blackbox.core.system.location;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.AtomicFile;
import android.util.SparseArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import black.android.location.BRILocationListener;
import black.android.location.BRILocationListenerStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.ISystemService;
import top.niunaijun.blackbox.entity.location.BCell;
import top.niunaijun.blackbox.entity.location.BLocation;
import top.niunaijun.blackbox.entity.location.BLocationConfig;
import top.niunaijun.blackbox.fake.frameworks.BLocationManager;
import top.niunaijun.blackbox.utils.CloseUtils;
import top.niunaijun.blackbox.utils.FileUtils;
import top.niunaijun.blackbox.utils.Slog;

/**
 * Virtual location manager service for the BlackBox virtual environment.
 * <p>
 * Intercepts and manages location-related requests within virtual containers, supporting
 * three operating modes: per-package custom location, global shared location, and disabled.
 * Manages GPS location, cell tower information, and neighboring cell data for virtual apps.
 * Persists location configurations to disk and supports real-time location update delivery
 * to registered listeners via a background thread pool.
 * </p>
 *
 * <p>Location modes:
 * <ul>
 *   <li>{@code OWN_MODE} - each virtual package has its own independent location config</li>
 *   <li>{@code GLOBAL_MODE} - all virtual packages share a single global location config</li>
 *   <li>{@code CLOSE_MODE} - location spoofing is disabled, returns {@code null}</li>
 * </ul>
 * </p>
 */
public class BLocationManagerService extends IBLocationManagerService.Stub implements ISystemService {
    /** Logging tag for this service. */
    public static final String TAG = "BLocationManagerService";

    private static final BLocationManagerService sService = new BLocationManagerService();
    /** Per-user, per-package location configuration map. Keyed by userId. */
    private final SparseArray<HashMap<String, BLocationConfig>> mLocationConfigs = new SparseArray<>();
    /** Global (shared) location configuration used in GLOBAL_MODE. */
    private final BLocationConfig mGlobalConfig = new BLocationConfig();
    /** Active location listener registrations keyed by their binder token. */
    private final Map<IBinder, LocationRecord> mLocationListeners = new HashMap<>();
    /** Thread pool for delivering location updates to registered listeners. */
    private final Executor mThreadPool = Executors.newCachedThreadPool();

    /**
     * Returns the singleton instance of this service.
     *
     * @return the global {@link BLocationManagerService} instance
     */
    public static BLocationManagerService get() {
        return sService;
    }

    private BLocationConfig getOrCreateConfig(int userId, String pkg) {
        synchronized (mLocationConfigs) {
            HashMap<String, BLocationConfig> pkgs = mLocationConfigs.get(userId);
            if (pkgs == null) {
                pkgs = new HashMap<>();
                mLocationConfigs.put(userId, pkgs);
            }
            BLocationConfig config = pkgs.get(pkg);
            if (config == null) {
                config = new BLocationConfig();
                config.pattern = BLocationManager.CLOSE_MODE;
                pkgs.put(pkg, config);
            }
            return config;
        }
    }

    /**
     * Retrieves the location operating mode for a specific virtual package.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name of the virtual application
     * @return the location mode constant (e.g., {@code OWN_MODE}, {@code GLOBAL_MODE}, {@code CLOSE_MODE})
     */
    public int getPattern(int userId, String pkg) {
        synchronized (mLocationConfigs) {
            BLocationConfig config = getOrCreateConfig(userId, pkg);
            return config.pattern;
        }
    }

    /**
     * Sets the location operating mode for a specific virtual package.
     *
     * @param userId  the virtual user ID
     * @param pkg     the package name of the virtual application
     * @param pattern the location mode to set (e.g., {@code OWN_MODE}, {@code GLOBAL_MODE}, {@code CLOSE_MODE})
     */
    @Override
    public void setPattern(int userId, String pkg, int pattern) {
        synchronized (mLocationConfigs) {
            getOrCreateConfig(userId, pkg).pattern = pattern;
            save();
        }
    }

    /**
     * Sets the primary cell tower information for a specific virtual package.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name of the virtual application
     * @param cell   the cell tower data to associate with this package
     */
    @Override
    public void setCell(int userId, String pkg, BCell cell) {
        synchronized (mLocationConfigs) {
            getOrCreateConfig(userId, pkg).cell = cell;
            save();
        }
    }

    /**
     * Sets the full list of cell towers for a specific virtual package.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name of the virtual application
     * @param cells  the list of cell tower data
     */
    @Override
    public void setAllCell(int userId, String pkg, List<BCell> cells) {
        synchronized (mLocationConfigs) {
            getOrCreateConfig(userId, pkg).allCell = cells;
            save();
        }
    }

    /**
     * Sets the neighboring cell tower list for a specific virtual package.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name of the virtual application
     * @param cells  the list of neighboring cell tower data
     */
    @Override
    public void setNeighboringCell(int userId, String pkg, List<BCell> cells) {
        synchronized (mLocationConfigs) {
            getOrCreateConfig(userId, pkg).allCell = cells;
            save();
        }
    }

    /**
     * Retrieves the neighboring cell tower list for a specific virtual package.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name of the virtual application
     * @return the list of neighboring cell tower data, or {@code null} if not configured
     */
    @Override
    public List<BCell> getNeighboringCell(int userId, String pkg) {
        synchronized (mLocationConfigs) {
            return getOrCreateConfig(userId, pkg).allCell;
        }
    }

    /**
     * Sets the global (shared) cell tower information used in GLOBAL_MODE.
     *
     * @param cell the cell tower data for the global configuration
     */
    @Override
    public void setGlobalCell(BCell cell) {
        synchronized (mGlobalConfig) {
            mGlobalConfig.cell = cell;
            save();
        }
    }

    /**
     * Sets the global (shared) full cell tower list used in GLOBAL_MODE.
     *
     * @param cells the list of cell tower data for the global configuration
     */
    @Override
    public void setGlobalAllCell(List<BCell> cells) {
        synchronized (mGlobalConfig) {
            mGlobalConfig.allCell = cells;
            save();
        }
    }

    /**
     * Sets the global (shared) neighboring cell tower list used in GLOBAL_MODE.
     *
     * @param cells the list of neighboring cell tower data for the global configuration
     */
    @Override
    public void setGlobalNeighboringCell(List<BCell> cells) {
        synchronized (mGlobalConfig) {
            mGlobalConfig.neighboringCellInfo = cells;
            save();
        }
    }

    /**
     * Retrieves the global (shared) neighboring cell tower list.
     *
     * @return the list of neighboring cell tower data from the global configuration
     */
    @Override
    public List<BCell> getGlobalNeighboringCell() {
        synchronized (mGlobalConfig) {
            return mGlobalConfig.neighboringCellInfo;
        }
    }

    /**
     * Retrieves the cell tower information for a virtual package based on its location mode.
     * Returns the per-package cell in OWN_MODE, the global cell in GLOBAL_MODE, or {@code null}
     * in CLOSE_MODE.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name of the virtual application
     * @return the cell tower data according to the active mode, or {@code null} if disabled
     */
    @Override
    public BCell getCell(int userId, String pkg) {
        BLocationConfig config = getOrCreateConfig(userId, pkg);
        switch (config.pattern) {
            case BLocationManager.OWN_MODE:
                return config.cell;
            case BLocationManager.GLOBAL_MODE:
                return mGlobalConfig.cell;
            case BLocationManager.CLOSE_MODE:
            default:
                return null;
        }
    }

    /**
     * Retrieves the full cell tower list for a virtual package based on its location mode.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name of the virtual application
     * @return the list of cell tower data according to the active mode, or {@code null} if disabled
     */
    @Override
    public List<BCell> getAllCell(int userId, String pkg) {
        BLocationConfig config = getOrCreateConfig(userId, pkg);
        switch (config.pattern) {
            case BLocationManager.OWN_MODE:
                return config.allCell;
            case BLocationManager.GLOBAL_MODE:
                return mGlobalConfig.allCell;
            case BLocationManager.CLOSE_MODE:
            default:
                return null;
        }
    }

    /**
     * Sets the GPS location for a specific virtual package.
     *
     * @param userId   the virtual user ID
     * @param pkg      the package name of the virtual application
     * @param location the GPS location data to assign
     */
    @Override
    public void setLocation(int userId, String pkg, BLocation location) {
        synchronized (mLocationConfigs) {
            getOrCreateConfig(userId, pkg).location = location;
            save();
        }
    }

    /**
     * Retrieves the GPS location for a virtual package based on its location mode.
     * Returns the per-package location in OWN_MODE, the global location in GLOBAL_MODE,
     * or {@code null} in CLOSE_MODE.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name of the virtual application
     * @return the GPS location according to the active mode, or {@code null} if disabled
     */
    @Override
    public BLocation getLocation(int userId, String pkg) {
        BLocationConfig config = getOrCreateConfig(userId, pkg);
        switch (config.pattern) {
            case BLocationManager.OWN_MODE:
                return config.location;
            case BLocationManager.GLOBAL_MODE:
                return mGlobalConfig.location;
            case BLocationManager.CLOSE_MODE:
            default:
                return null;
        }
    }

    /**
     * Sets the global (shared) GPS location used in GLOBAL_MODE.
     *
     * @param location the GPS location data for the global configuration
     */
    @Override
    public void setGlobalLocation(BLocation location) {
        synchronized (mGlobalConfig) {
            mGlobalConfig.location = location;
            save();
        }
    }

    /**
     * Retrieves the global (shared) GPS location.
     *
     * @return the GPS location from the global configuration
     */
    @Override
    public BLocation getGlobalLocation() {
        synchronized (mGlobalConfig) {
            return mGlobalConfig.location;
        }
    }

    /**
     * Registers a location listener to receive periodic location updates for a virtual package.
     * <p>
     * Links the listener binder to a death recipient so it is automatically removed if the
     * remote process dies. Launches a background task that polls and delivers spoofed
     * location changes to the listener every few seconds.
     * </p>
     *
     * @param listener    the binder of the remote location listener
     * @param packageName the virtual package name requesting updates
     * @param userId      the virtual user ID
     * @throws RemoteException if the remote binder communication fails
     */
    @Override
    public void requestLocationUpdates(IBinder listener, String packageName, int userId) throws RemoteException {
        if (listener == null || !listener.pingBinder()) {
            return;
        }
        if (mLocationListeners.containsKey(listener))
            return;
        listener.linkToDeath(new DeathRecipient() {
            @Override
            public void binderDied() {
                listener.unlinkToDeath(this, 0);
                mLocationListeners.remove(listener);
            }
        }, 0);
        LocationRecord record = new LocationRecord(packageName, userId);
        mLocationListeners.put(listener, record);
        addTask(listener);
    }

    /**
     * Unregisters a location listener so it no longer receives location updates.
     *
     * @param listener the binder of the remote location listener to remove
     * @throws RemoteException if the remote binder communication fails
     */
    @Override
    public void removeUpdates(IBinder listener) throws RemoteException {
        if (listener == null || !listener.pingBinder()) {
            return;
        }
        mLocationListeners.remove(listener);
    }

    private void addTask(IBinder locationListener) {
        mThreadPool.execute(() -> {
            BLocation lastLocation = null;
            long l = System.currentTimeMillis();
            while (locationListener.pingBinder()) {
                IInterface iInterface = BRILocationListenerStub.get().asInterface(locationListener);
                LocationRecord locationRecord = mLocationListeners.get(locationListener);
                if (locationRecord == null)
                    continue;
                BLocation location = getLocation(locationRecord.userId, locationRecord.packageName);
                if (location == null)
                    continue;
                if (location.equals(lastLocation) && (System.currentTimeMillis() - l) < 3000) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                    continue;
                }
                lastLocation = location;
                l = System.currentTimeMillis();
                BlackBoxCore.get().getHandler().post(() -> BRILocationListener.get(iInterface).onLocationChanged(location.convert2SystemLocation()));
            }
        });
    }

    /**
     * Persists all current location configurations (global and per-user/per-package) to disk
     * using {@link android.util.AtomicFile} for crash-safe writes.
     */
    public void save() {
        synchronized (mGlobalConfig) {
            synchronized (mLocationConfigs) {
                Parcel parcel = Parcel.obtain();
                AtomicFile atomicFile = new AtomicFile(BEnvironment.getFakeLocationConf());
                FileOutputStream fileOutputStream = null;
                try {
                    mGlobalConfig.writeToParcel(parcel, 0);

                    parcel.writeInt(mLocationConfigs.size());
                    for (int i = 0; i < mLocationConfigs.size(); i++) {
                        int tmpUserId = mLocationConfigs.keyAt(i);
                        HashMap<String, BLocationConfig> configArrayMap = mLocationConfigs.valueAt(i);
                        parcel.writeInt(tmpUserId);
                        parcel.writeMap(configArrayMap);
                    }
                    parcel.setDataPosition(0);
                    fileOutputStream = atomicFile.startWrite();
                    FileUtils.writeParcelToOutput(parcel, fileOutputStream);
                    atomicFile.finishWrite(fileOutputStream);
                } catch (Throwable e) {
                    e.printStackTrace();
                    atomicFile.failWrite(fileOutputStream);
                } finally {
                    parcel.recycle();
                    CloseUtils.close(fileOutputStream);
                }
            }
        }
    }

    /**
     * Loads previously persisted location configurations from disk into memory.
     * <p>
     * Deserializes the global config and all per-user/per-package configs from the
     * location configuration file. If the file does not exist or is corrupt, the
     * in-memory state is left unchanged (or cleared on corruption).
     * </p>
     */
    public void loadConfig() {
        Parcel parcel = Parcel.obtain();
        InputStream is = null;
        try {
            File fakeLocationConf = BEnvironment.getFakeLocationConf();
            if (!fakeLocationConf.exists()) {
                return;
            }
            is = new FileInputStream(BEnvironment.getFakeLocationConf());
            byte[] bytes = FileUtils.toByteArray(is);
            parcel.unmarshall(bytes, 0, bytes.length);
            parcel.setDataPosition(0);

            synchronized (mGlobalConfig) {
                mGlobalConfig.refresh(parcel);
            }

            synchronized (mLocationConfigs) {
                mLocationConfigs.clear();
                int size = parcel.readInt();
                for (int i = 0; i < size; i++) {
                    int userId = parcel.readInt();
                    HashMap<String, BLocationConfig> configArrayMap = parcel.readHashMap(BLocationConfig.class.getClassLoader());
                    mLocationConfigs.put(userId, configArrayMap);
                    Slog.d(TAG, "load userId: " + userId + ", config: " + configArrayMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Slog.d(TAG, "bad config");
            FileUtils.deleteDir(BEnvironment.getFakeLocationConf());
        } finally {
            parcel.recycle();
            CloseUtils.close(is);
        }
    }

    /**
     * Called when the system is ready. Loads persisted location configs and restarts
     * background location delivery tasks for any listeners that were registered before
     * the service was initialized.
     */
    @Override
    public void systemReady() {
        loadConfig();
        for (IBinder iBinder : mLocationListeners.keySet()) {
            addTask(iBinder);
        }
    }
}
