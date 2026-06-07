package black.android.os.storage;

import java.io.File;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.os.storage.StorageVolume fields.
 * Provides access to internal path fields of a storage volume.
 */
@BClassName("android.os.storage.StorageVolume")
public interface StorageVolume {
    /**
     * The internal (primary) path of the storage volume.
     */
    @BField
    File mInternalPath();

    /**
     * The public mount path of the storage volume.
     */
    @BField
    File mPath();
}
