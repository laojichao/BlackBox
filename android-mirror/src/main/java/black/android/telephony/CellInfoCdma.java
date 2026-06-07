package black.android.telephony;

import android.telephony.CellIdentityCdma;
import android.telephony.CellSignalStrengthCdma;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.telephony.CellInfoCdma.
 * Allows constructing and accessing CDMA cell info with identity and signal strength.
 */
@BClassName("android.telephony.CellInfoCdma")
public interface CellInfoCdma {
    /** Construct a new empty CellInfoCdma. */
    @BConstructor
    CellInfoCdma _new();

    /** The CDMA cell identity. */
    @BField
    CellIdentityCdma mCellIdentityCdma();

    /** The CDMA cell signal strength. */
    @BField
    CellSignalStrengthCdma mCellSignalStrengthCdma();
}
