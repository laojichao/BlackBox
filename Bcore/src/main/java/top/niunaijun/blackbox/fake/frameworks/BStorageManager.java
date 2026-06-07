package top.niunaijun.blackbox.fake.frameworks;

import android.net.Uri;
import android.os.RemoteException;
import android.os.storage.StorageVolume;

import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.os.IBStorageManagerService;

/**
 * Client-side manager for storage operations within the virtual environment. Provides
 * a facade over {@link IBStorageManagerService} for volume listing and file URI generation.
 */
public class BStorageManager extends BlackManager<IBStorageManagerService> {
    private static final BStorageManager sStorageManager = new BStorageManager();

    /**
     * Returns the singleton instance of {@link BStorageManager}.
     *
     * @return the singleton BStorageManager instance
     */
    public static BStorageManager get() {
        return sStorageManager;
    }

    @Override
    protected String getServiceName() {
        return ServiceManager.STORAGE_MANAGER;
    }

    /**
     * Returns the list of storage volumes for the given UID and package.
     *
     * @param uid         the process UID
     * @param packageName the package name
     * @param flags       additional flags
     * @param userId      the virtual user ID
     * @return an array of StorageVolume, or an empty array on failure
     */
    public StorageVolume[] getVolumeList(int uid, String packageName, int flags, int userId) {
        try {
            return getService().getVolumeList(uid, packageName, flags, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new StorageVolume[]{};
    }

    /**
     * Returns a content URI for the given file path.
     *
     * @param file the absolute file path
     * @return the content Uri, or null on failure
     */
    public Uri getUriForFile(String file) {
        try {
            return getService().getUriForFile(file);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
