package top.niunaijun.blackbox.fake.frameworks;

import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.location.IBLocationManagerService;
import top.niunaijun.blackbox.entity.location.BCell;
import top.niunaijun.blackbox.entity.location.BLocation;

/**
 * Client-side manager for location spoofing within the virtual environment. Provides
 * a facade over {@link IBLocationManagerService} for setting and retrieving fake GPS
 * locations, cell tower data, and managing per-app or global location spoofing patterns.
 */
public class BLocationManager extends BlackManager<IBLocationManagerService> {
    private static final BLocationManager sLocationManager = new BLocationManager();

    /** Location spoofing is disabled. */
    public static final int CLOSE_MODE = 0;
    /** Location spoofing applies globally to all apps. */
    public static final int GLOBAL_MODE = 1;
    /** Location spoofing applies only to the specific app. */
    public static final int OWN_MODE = 2;

    /**
     * Returns the singleton instance of {@link BLocationManager}.
     *
     * @return the singleton BLocationManager instance
     */
    public static BLocationManager get() {
        return sLocationManager;
    }

    @Override
    protected String getServiceName() {
        return ServiceManager.LOCATION_MANAGER;
    }

    /**
     * Checks whether fake location is enabled for the current app.
     *
     * @return true if location spoofing is active (not CLOSE_MODE)
     */
    public static boolean isFakeLocationEnable() {
        return get().getPattern(BActivityThread.getUserId(), BActivityThread.getAppPackageName()) != CLOSE_MODE;
    }

    /**
     * Disables fake location for the given user and package.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name
     */
    public static void disableFakeLocation(int userId,String pkg){
        get().setPattern(userId,pkg,CLOSE_MODE);
    }

    /**
     * Sets the location spoofing pattern for a given user and package.
     *
     * @param userId  the virtual user ID
     * @param pkg     the package name
     * @param pattern the spoofing pattern (CLOSE_MODE, GLOBAL_MODE, or OWN_MODE)
     */
    public void setPattern(int userId, String pkg, int pattern) {
        try {
            getService().setPattern(userId, pkg, pattern);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the location spoofing pattern for a given user and package.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name
     * @return the spoofing pattern (CLOSE_MODE, GLOBAL_MODE, or OWN_MODE)
     */
    public int getPattern(int userId, String pkg) {
        try {
            return getService().getPattern(userId, pkg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return CLOSE_MODE;
    }

    /**
     * Sets a single cell tower for location spoofing.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name
     * @param cell   the BCell to set
     */
    public void setCell(int userId, String pkg, BCell cell) {
        try {
            getService().setCell(userId, pkg, cell);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets all cell towers for location spoofing.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name
     * @param cells  the list of BCell objects to set
     */
    public void setAllCell(int userId, String pkg, List<BCell> cells) {
        try {
            getService().setAllCell(userId, pkg, cells);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets neighboring cell towers for a given user and package.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name
     * @return the list of neighboring BCell objects, or null on failure
     */
    public List<BCell> getNeighboringCell(int userId, String pkg) {
        try {
            return getService().getNeighboringCell(userId, pkg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets globally configured neighboring cell towers.
     *
     * @return the list of global neighboring BCell objects, or null on failure
     */
    public List<BCell> getGlobalNeighboringCell() {
        try {
            return getService().getGlobalNeighboringCell();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets neighboring cell towers for a given user and package.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name
     * @param cells  the list of BCell objects to set as neighbors
     */
    public void setNeighboringCell(int userId, String pkg, List<BCell> cells) {
        try {
            getService().setNeighboringCell(userId, pkg, cells);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets a global cell tower for location spoofing.
     *
     * @param cell the BCell to set globally
     */
    public void setGlobalCell(BCell cell) {
        try {
            getService().setGlobalCell(cell);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets all global cell towers for location spoofing.
     *
     * @param cells the list of BCell objects to set globally
     */
    public void setGlobalAllCell(List<BCell> cells) {
        try {
            getService().setGlobalAllCell(cells);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets global neighboring cell towers for location spoofing.
     *
     * @param cells the list of BCell objects to set as global neighbors
     */
    public void setGlobalNeighboringCell(List<BCell> cells) {
        try {
            getService().setGlobalNeighboringCell(cells);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the primary cell tower for a given user and package.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name
     * @return the BCell, or null on failure
     */
    public BCell getCell(int userId, String pkg) {
        try {
            return getService().getCell(userId, pkg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets all cell towers for a given user and package.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name
     * @return the list of BCell objects, or an empty list on failure
     */
    public List<BCell> getAllCell(int userId, String pkg) {
        try {
            return getService().getAllCell(userId, pkg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Sets a fake GPS location for a given user and package.
     *
     * @param userId   the virtual user ID
     * @param pkg      the package name
     * @param location the BLocation to set
     */
    public void setLocation(int userId, String pkg, BLocation location) {
        try {
            getService().setLocation(userId, pkg, location);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the fake GPS location for a given user and package.
     *
     * @param userId the virtual user ID
     * @param pkg    the package name
     * @return the BLocation, or null on failure
     */
    public BLocation getLocation(int userId, String pkg) {
        try {
            return getService().getLocation(userId, pkg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets a global fake GPS location.
     *
     * @param location the BLocation to set globally
     */
    public void setGlobalLocation(BLocation location) {
        try {
            getService().setGlobalLocation(location);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the global fake GPS location.
     *
     * @return the global BLocation, or null on failure
     */
    public BLocation getGlobalLocation() {
        try {
            return getService().getGlobalLocation();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Registers for location updates for the current app.
     *
     * @param listener the IBinder listener to receive updates
     */
    public void requestLocationUpdates(IBinder listener) {
        try {
            getService().requestLocationUpdates(listener, BActivityThread.getAppPackageName(), BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a previously registered location update listener.
     *
     * @param listener the IBinder listener to remove
     */
    public void removeUpdates(IBinder listener) {
        try {
            getService().removeUpdates(listener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
