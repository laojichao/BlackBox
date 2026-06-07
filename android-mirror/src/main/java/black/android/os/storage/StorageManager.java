package black.android.os.storage;

import android.os.storage.StorageVolume;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.storage.StorageManager static methods.
 * Provides access to the hidden getVolumeList method.
 */
@BClassName("android.os.storage.StorageManager")
public interface StorageManager {
    /**
     * Get the list of storage volumes for the given user and flags.
     */
    @BStaticMethod
    StorageVolume[] getVolumeList(int int0, int int1);
}
