package black.android.telephony;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.telephony.NeighboringCellInfo fields.
 * Provides access to neighboring GSM cell tower information.
 */
@BClassName("android.telephony.NeighboringCellInfo")
public interface NeighboringCellInfo {
    /** The Cell ID of the neighboring cell. */
    @BField
    int mCid();

    /** The Location Area Code of the neighboring cell. */
    @BField
    int mLac();

    /** The received signal strength (RSSI) of the neighboring cell. */
    @BField
    int mRssi();
}
