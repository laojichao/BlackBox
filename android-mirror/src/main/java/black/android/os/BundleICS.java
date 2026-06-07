package black.android.os;

import android.os.Parcel;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.os.Bundle fields for Ice Cream Sandwich (API 14+).
 * Exposes the internal parcelled data field used in older API levels.
 */
@BClassName("android.os.Bundle")
public interface BundleICS {
    /**
     * The internal Parcel holding serialized bundle data (ICS variant).
     */
    @BField
    Parcel mParcelledData();
}
