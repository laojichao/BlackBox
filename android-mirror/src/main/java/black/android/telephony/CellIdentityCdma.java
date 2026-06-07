package black.android.telephony;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.telephony.CellIdentityCdma.
 * Allows constructing and accessing CDMA cell tower identity fields.
 */
@BClassName("android.telephony.CellIdentityCdma")
public interface CellIdentityCdma {
    /** Construct a new empty CellIdentityCdma. */
    @BConstructor
    CellIdentityCdma _new();

    /** The CDMA base station ID. */
    @BField
    int mBasestationId();

    /** The CDMA network ID. */
    @BField
    int mNetworkId();

    /** The CDMA system ID. */
    @BField
    int mSystemId();
}
