package black.android.content;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;

/**
 * Mirror of hidden android.content.SyncAdapterType constructor for Android N (API 24+).
 * Includes an additional user-facing name parameter.
 */
@BClassName("android.content.SyncAdapterType")
public interface SyncAdapterTypeN {
    /** Creates a new SyncAdapterType with user-facing name and configuration flags. */
    @BConstructor
    SyncAdapterTypeN _new(String String0, String String1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, String String6, String String7);
}
