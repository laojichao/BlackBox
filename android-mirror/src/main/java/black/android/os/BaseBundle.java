package black.android.os;

import android.os.Parcel;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.os.BaseBundle.
 * Provides access to internal BaseBundle fields for parcel manipulation.
 */
@BClassName("android.os.BaseBundle")
public interface BaseBundle {
    /**
     * The internal Parcel holding serialized bundle data.
     */
    @BField
    Parcel mParcelledData();
}
