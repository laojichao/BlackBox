package top.niunaijun.blackbox.core.system.os;

import android.net.Uri;
import android.os.Process;
import android.os.RemoteException;
import android.os.storage.StorageVolume;

import java.io.File;

import black.android.os.storage.BRStorageManager;
import black.android.os.storage.BRStorageVolume;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.ISystemService;
import top.niunaijun.blackbox.core.system.user.BUserHandle;
import top.niunaijun.blackbox.fake.provider.FileProvider;
import top.niunaijun.blackbox.proxy.ProxyManifest;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Virtual storage manager service for the BlackBox virtual environment.
 * <p>
 * Intercepts storage-related requests from virtual apps and remaps them so that each
 * virtual user perceives its own isolated external storage directory. Also provides a
 * virtual {@link android.content.ContentProvider} URI via {@link FileProvider} for
 * secure file sharing between virtual apps and the host.
 * </p>
 */
public class BStorageManagerService extends IBStorageManagerService.Stub implements ISystemService {
    private static final BStorageManagerService sService = new BStorageManagerService();

    /**
     * Returns the singleton instance of this service.
     *
     * @return the global {@link BStorageManagerService} instance
     */
    public static BStorageManagerService get() {
        return sService;
    }

    /** Default constructor. */
    public BStorageManagerService() {
    }

    /**
     * Returns the list of storage volumes visible to a virtual app, with each volume's
     * path remapped to the virtual user's external storage directory.
     *
     * @param uid         the calling UID
     * @param packageName the virtual package name
     * @param flags       flags passed to the underlying storage query
     * @param userId      the virtual user ID
     * @return an array of {@link StorageVolume} objects with remapped paths, or
     *         {@code null} if the underlying storage query fails
     * @throws RemoteException if remote binder communication fails
     */
    @Override
    public StorageVolume[] getVolumeList(int uid, String packageName, int flags, int userId) throws RemoteException {
        if (BRStorageManager.get().getVolumeList(0, 0) == null) {
            return null;
        }
        try {
            StorageVolume[] storageVolumes = BRStorageManager.get().getVolumeList(BUserHandle.getUserId(Process.myUid()), 0);
            if (storageVolumes == null)
                return null;
            for (StorageVolume storageVolume : storageVolumes) {
                BRStorageVolume.get(storageVolume)._set_mPath(BEnvironment.getExternalUserDir(userId));
                if (BuildCompat.isPie()) {
                    BRStorageVolume.get(storageVolume)._set_mInternalPath(BEnvironment.getExternalUserDir(userId));
                }
            }
            return storageVolumes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a content URI for the given file path, suitable for sharing via the virtual
     * {@link FileProvider}.
     *
     * @param file the absolute file path to convert
     * @return a content {@link Uri} for the file
     * @throws RemoteException if remote binder communication fails
     */
    @Override
    public Uri getUriForFile(String file) throws RemoteException {
        return FileProvider.getUriForFile(BlackBoxCore.getContext(), ProxyManifest.getProxyFileProvider(), new File(file));
    }

    /**
     * Called when the system is ready. No additional initialization is required for this service.
     */
    @Override
    public void systemReady() {

    }
}
