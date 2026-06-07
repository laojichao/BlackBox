package black.android.content;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;

/**
 * Mirror of hidden android.content.SyncAdapterType constructor.
 * Allows creating sync adapter type descriptors via reflection.
 */
@BClassName("android.content.SyncAdapterType")
public interface SyncAdapterType {
    /** Creates a new SyncAdapterType with authority, account type, and configuration flags. */
    @BConstructor
    SyncAdapterType _new(String String0, String String1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, String String6);
}
