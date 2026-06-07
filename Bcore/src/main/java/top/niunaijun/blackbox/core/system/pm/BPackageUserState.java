package top.niunaijun.blackbox.core.system.pm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Tracks the per-user state of a package within the virtual environment.
 *
 * <p>Each virtual user can independently control whether a package appears as
 * installed, stopped, or hidden. This class is serializable via {@link Parcel}
 * and is stored as part of {@link BPackageSettings}.</p>
 *
 * @see BPackageSettings
 */
public class BPackageUserState implements Parcelable {
    /** Whether the package is installed for this user. Defaults to false. */
    public boolean installed;
    /** Whether the package is in a stopped state. Defaults to true. */
    public boolean stopped;
    /** Whether the package is hidden from the launcher. Defaults to false. */
    public boolean hidden;

    /**
     * Constructs a default user state with installed=false, stopped=true, hidden=false.
     */
    public BPackageUserState() {
        this.installed = false;
        this.stopped = true;
        this.hidden = false;
    }

    /**
     * Factory method that creates a user state with installed=true.
     *
     * @return a new BPackageUserState with installed set to true
     */
    public static BPackageUserState create() {
        BPackageUserState state = new BPackageUserState();
        state.installed = true;
        return state;
    }

    /**
     * Returns a bitmask indicating the set of special object types
     * marshaled by this Parcelable.
     *
     * @return 0, indicating no special objects
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flattens this user state into a Parcel.
     *
     * @param dest  the Parcel in which the object should be written
     * @param flags additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.installed ? (byte) 1 : (byte) 0);
        dest.writeByte(this.stopped ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hidden ? (byte) 1 : (byte) 0);
    }

    /**
     * Restores a user state from a previously serialized Parcel.
     *
     * @param in the Parcel containing serialized user state data
     */
    protected BPackageUserState(Parcel in) {
        this.installed = in.readByte() != 0;
        this.stopped = in.readByte() != 0;
        this.hidden = in.readByte() != 0;
    }

    /**
     * Constructs a defensive copy of an existing user state.
     *
     * @param state the user state to copy from
     */
    public BPackageUserState(BPackageUserState state) {
        this.installed = state.installed;
        this.stopped = state.stopped;
        this.hidden = state.hidden;
    }

    public static final Parcelable.Creator<BPackageUserState> CREATOR = new Parcelable.Creator<BPackageUserState>() {
        @Override
        public BPackageUserState createFromParcel(Parcel source) {
            return new BPackageUserState(source);
        }

        @Override
        public BPackageUserState[] newArray(int size) {
            return new BPackageUserState[size];
        }
    };
}
