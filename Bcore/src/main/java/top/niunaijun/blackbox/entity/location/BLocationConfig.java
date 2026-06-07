package top.niunaijun.blackbox.entity.location;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Parcelable configuration for virtual location spoofing within the BlackBox environment.
 * <p>
 * Bundles a complete location spoofing profile including the spoofing pattern mode,
 * the primary cell tower, all available cell towers, neighboring cell info, and
 * the target geographic location. Used by the virtual location service to apply
 * consistent GPS and cell-based location spoofing.
 * </p>
 *
 * @see BLocation
 * @see BCell
 */
public class BLocationConfig implements Parcelable {

    /** The spoofing pattern mode that controls how location data is applied. */
    public int pattern;

    /** The primary cell tower data to report to the application. */
    public BCell cell;

    /** The complete list of cell towers available in the spoofed location area. */
    public List<BCell> allCell;

    /** The list of neighboring cell tower info surrounding the spoofed location. */
    public List<BCell> neighboringCellInfo;

    /** The spoofed geographic location (latitude, longitude, etc.). */
    public BLocation location;

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Default constructor for creating an empty {@link BLocationConfig}.
     */
    public BLocationConfig() {
    }

    /**
     * Constructs a {@link BLocationConfig} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    public BLocationConfig(Parcel in) {
        refresh(in);
    }

    /**
     * Reinitializes this configuration's fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    public void refresh(Parcel in) {
        this.pattern = in.readInt();
        this.cell = in.readParcelable(BCell.class.getClassLoader());
        this.allCell = in.createTypedArrayList(BCell.CREATOR);
        this.neighboringCellInfo = in.createTypedArrayList(BCell.CREATOR);
        this.location = in.readParcelable(BLocation.class.getClassLoader());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pattern);
        dest.writeParcelable(this.cell, flags);
        dest.writeTypedList(this.allCell);
        dest.writeTypedList(this.neighboringCellInfo);
        dest.writeParcelable(this.location, flags);
    }

    public static final Creator<BLocationConfig> CREATOR = new Creator<BLocationConfig>() {
        @Override
        public BLocationConfig createFromParcel(Parcel source) {
            return new BLocationConfig(source);
        }

        @Override
        public BLocationConfig[] newArray(int size) {
            return new BLocationConfig[size];
        }
    };
}
