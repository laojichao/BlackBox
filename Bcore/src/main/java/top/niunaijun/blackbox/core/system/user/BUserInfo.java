package top.niunaijun.blackbox.core.system.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a virtual user within the BlackBox environment.
 * <p>
 * Each virtual user has a unique numeric ID, a status (enabled or disabled), a display
 * name, and a creation timestamp. Instances are persisted to disk via Parcel serialization
 * and restored on service startup by {@link BUserManagerService}.
 * </p>
 */
public class BUserInfo implements Parcelable {
    /** Unique numeric identifier for this virtual user. */
    public int id;
    /** Current lifecycle status of this user ({@link BUserStatus#ENABLE} or {@link BUserStatus#DISABLE}). */
    public BUserStatus status;
    /** Human-readable display name for this virtual user. */
    public String name;
    /** Epoch-millis timestamp when this virtual user was created. */
    public long createTime;

    /** Default constructor used internally during user creation. */
    BUserInfo() {
    }

    /**
     * Returns the Parcelable contents indicator (always 0).
     *
     * @return 0, as this Parcelable contains no file descriptors
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Serializes this user info to a Parcel.
     *
     * @param dest the Parcel to write to
     * @param flags additional flags (unused)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeString(this.name);
        dest.writeLong(this.createTime);
    }

    /**
     * Reconstructs a {@link BUserInfo} from a previously serialized Parcel.
     *
     * @param in the Parcel to read from
     */
    protected BUserInfo(Parcel in) {
        this.id = in.readInt();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : BUserStatus.values()[tmpStatus];
        this.name = in.readString();
        this.createTime = in.readLong();
    }

    /** Parcelable creator for {@link BUserInfo} instances. */
    public static final Creator<BUserInfo> CREATOR = new Creator<BUserInfo>() {
        @Override
        public BUserInfo createFromParcel(Parcel source) {
            return new BUserInfo(source);
        }

        @Override
        public BUserInfo[] newArray(int size) {
            return new BUserInfo[size];
        }
    };

    /**
     * Returns a human-readable string representation of this user info, including
     * ID, status, name, and creation time.
     *
     * @return a debug-friendly string representation
     */
    @Override
    public String toString() {
        return "BUserInfo{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
