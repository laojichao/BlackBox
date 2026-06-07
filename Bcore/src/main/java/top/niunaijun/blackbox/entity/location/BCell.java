package top.niunaijun.blackbox.entity.location;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Parcelable representation of a cellular base station (cell tower) used for
 * virtual location spoofing within the BlackBox environment.
 * <p>
 * Encodes the standard cell identity parameters: MCC (Mobile Country Code),
 * MNC (Mobile Network Code), LAC (Location Area Code), CID (Cell Identity),
 * and the radio network type. These fields allow the virtual environment to
 * report fake cell tower information to applications that query
 * {@code TelephonyManager} or {@code CellLocation} APIs.
 * </p>
 *
 * @see BLocation
 * @see BLocationConfig
 */
public class BCell implements Parcelable {
    /**
     * Cell identity fields reference:
     * MCC - Mobile Country Code (e.g., 460 for China).
     * MNC - Mobile Network Code (e.g., 00 for China Mobile, 01 for China Unicom, 11 for China Telecom 4G).
     * LAC/TAC - Location Area Code, range 1-65535.
     * CID/CI - Cell Identity, range 1-65535 for 2G, 1-268435455 for 3G/4G.
     * TYPE - Radio access technology: CDMA, LTE, GSM, or WCDMA.
     */

    /** Mobile Country Code identifying the country of the cell tower (e.g., 460 for China). */
    public int MCC;

    /** Mobile Network Code identifying the carrier network. */
    public int MNC;

    /** Location Area Code (or Tracking Area Code for LTE) identifying the location area. */
    public int LAC;

    /** Cell Identity uniquely identifying the base station within the location area. */
    public int CID;

    /** The radio network type of this cell (use one of the {@code PHONE_TYPE_*} constants). */
    public int TYPE;

    /** Network type is unknown. */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /** Current network is GPRS */
    public static final int NETWORK_TYPE_GPRS = 1;
    /** Current network is EDGE */
    public static final int NETWORK_TYPE_EDGE = 2;
    /** Current network is UMTS */
    public static final int NETWORK_TYPE_UMTS = 3;
    /** Current network is CDMA: Either IS95A or IS95B*/
    public static final int NETWORK_TYPE_CDMA = 4;
    /** Current network is EVDO revision 0*/
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /** Current network is EVDO revision A*/
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /** Current network is 1xRTT*/
    public static final int NETWORK_TYPE_1xRTT = 7;
    /**
     * No phone module
     *
     */
    public static final int PHONE_TYPE_NONE = 0;
    /**
     * GSM phone
     */
    public static final int PHONE_TYPE_GSM = 1;
    /**
     * CDMA phone
     */
    public static final int PHONE_TYPE_CDMA = 2;

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.MCC);
        dest.writeInt(this.MNC);
        dest.writeInt(this.LAC);
        dest.writeInt(this.CID);
        dest.writeInt(this.TYPE);
    }

    /**
     * Default constructor for creating an empty {@link BCell}.
     */
    public  BCell(){}

    /**
     * Constructs a {@link BCell} with GSM phone type and the specified cell identity fields.
     *
     * @param MCC the Mobile Country Code
     * @param MNC the Mobile Network Code
     * @param LAC the Location Area Code
     * @param CID the Cell Identity
     */
    public BCell(int MCC, int MNC, int LAC, int CID) {
        this.TYPE = this.PHONE_TYPE_GSM;
        this.MCC = MCC;
        this.CID = CID;
        this.MNC = MNC;
        this.LAC = LAC;
    }

    /**
     * Constructs a {@link BCell} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    public BCell(Parcel in) {
        this.MCC = in.readInt();
        this.MNC = in.readInt();
        this.LAC = in.readInt();
        this.CID = in.readInt();
        this.TYPE = in.readInt();
    }

    public static final Parcelable.Creator<BCell> CREATOR = new Parcelable.Creator<BCell>() {
        @Override
        public BCell createFromParcel(Parcel source) {
            return new BCell(source);
        }

        @Override
        public BCell[] newArray(int size) {
            return new BCell[size];
        }
    };
}

