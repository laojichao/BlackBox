package black.android.os;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.os.Parcel type constants.
 * Provides access to internal Parcelable type discriminator values.
 */
@BClassName("android.os.Parcel")
public interface Parcel {
    /**
     * Type discriminator for a single Parcelable value.
     */
    @BStaticField
    int VAL_PARCELABLE();

    /**
     * Type discriminator for a Parcelable array value.
     */
    @BStaticField
    int VAL_PARCELABLEARRAY();
}
