package black.android.os;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.os.DropBoxManager.
 * Provides access to the internal IDropBoxManagerService binder.
 */
@BClassName("android.os.DropBoxManager")
public interface DropBoxManager {
    /**
     * The underlying IDropBoxManagerService binder interface.
     */
    @BField
    IInterface mService();
}
