package black.android.content;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.content.AttributionSourceState.
 * Parcelable state holding the package name and UID of an attribution source (API 31+).
 */
@BClassName("android.content.AttributionSourceState")
public interface AttributionSourceState {
    /** The package name of the attribution source. */
    @BField
    String packageName();

    /** The UID of the attribution source process. */
    @BField
    int uid();
}
