package black.android.telephony;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.telephony.CellSignalStrengthGsm.
 * Allows constructing and accessing GSM signal strength measurements.
 */
@BClassName("android.telephony.CellSignalStrengthGsm")
public interface CellSignalStrengthGsm {
    /** Construct a new empty CellSignalStrengthGsm. */
    @BConstructor
    CellSignalStrengthGsm _new();

    /** Bit error rate (0-7, 99 = unknown). */
    @BField
    int mBitErrorRate();

    /** Signal strength as ASU (0-31, 99 = unknown). */
    @BField
    int mSignalStrength();
}
