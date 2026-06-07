package black.android.telephony;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.telephony.CellIdentityGsm.
 * Allows constructing and accessing GSM cell tower identity fields.
 */
@BClassName("android.telephony.CellIdentityGsm")
public interface CellIdentityGsm {
    /** Construct a new empty CellIdentityGsm. */
    @BConstructor
    CellIdentityGsm _new();

    /** The GSM Cell ID. */
    @BField
    int mCid();

    /** The GSM Location Area Code. */
    @BField
    int mLac();

    /** The Mobile Country Code. */
    @BField
    int mMcc();

    /** The Mobile Network Code. */
    @BField
    int mMnc();
}
