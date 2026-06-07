package black.android.telephony;

import android.telephony.CellIdentityGsm;
import android.telephony.CellSignalStrengthGsm;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.telephony.CellInfoGsm.
 * Allows constructing and accessing GSM cell info with identity and signal strength.
 */
@BClassName("android.telephony.CellInfoGsm")
public interface CellInfoGsm {
    /** Construct a new empty CellInfoGsm. */
    @BConstructor
    CellInfoGsm _new();

    /** The GSM cell identity. */
    @BField
    CellIdentityGsm mCellIdentityGsm();

    /** The GSM cell signal strength. */
    @BField
    CellSignalStrengthGsm mCellSignalStrengthGsm();
}
