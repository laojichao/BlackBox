package black.android.telephony;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.telephony.CellSignalStrengthCdma.
 * Allows constructing and accessing CDMA signal strength measurements.
 */
@BClassName("android.telephony.CellSignalStrengthCdma")
public interface CellSignalStrengthCdma {
    /** Construct a new empty CellSignalStrengthCdma. */
    @BConstructor
    CellSignalStrengthCdma _new();

    /** CDMA signal strength in dBm. */
    @BField
    int mCdmaDbm();

    /** CDMA Ec/Io in dB*10. */
    @BField
    int mCdmaEcio();

    /** EVDO signal strength in dBm. */
    @BField
    int mEvdoDbm();

    /** EVDO Ec/Io in dB*10. */
    @BField
    int mEvdoEcio();

    /** EVDO signal-to-noise ratio. */
    @BField
    int mEvdoSnr();
}
