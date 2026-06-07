package top.niunaijun.blackbox.entity;

import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable record that tracks the unbinding state of a service component within
 * the BlackBox virtual environment.
 * <p>
 * Stores the remaining bind count, the start ID of the service, and the
 * {@link ComponentName} that identifies which service was unbound. Used to determine
 * when a service should be fully stopped after all clients have unbound.
 * </p>
 */
public class UnbindRecord implements Parcelable {
    /** The number of remaining active bindings for the tracked service. */
    private int mBindCount;

    /** The start ID of the service at the time the unbind was recorded. */
    private int mStartId;

    /** The component name of the service that was unbound. */
    private ComponentName mComponentName;

    /**
     * Returns the start ID associated with this unbind record.
     *
     * @return the start ID of the service
     */
    public int getStartId() {
        return mStartId;
    }

    /**
     * Sets the start ID for this unbind record.
     *
     * @param startId the start ID to set
     */
    public void setStartId(int startId) {
        mStartId = startId;
    }

    /**
     * Returns the remaining bind count for the tracked service.
     *
     * @return the number of active bindings remaining
     */
    public int getBindCount() {
        return mBindCount;
    }

    /**
     * Sets the bind count for this unbind record.
     *
     * @param bindCount the number of active bindings
     */
    public void setBindCount(int bindCount) {
        mBindCount = bindCount;
    }

    /**
     * Returns the {@link ComponentName} of the unbound service.
     *
     * @return the component name of the service
     */
    public ComponentName getComponentName() {
        return mComponentName;
    }

    /**
     * Sets the {@link ComponentName} for this unbind record.
     *
     * @param componentName the component name to set
     */
    public void setComponentName(ComponentName componentName) {
        mComponentName = componentName;
    }

    /**
     * Returns the Parcelable {@link Creator} for {@link UnbindRecord}.
     *
     * @return the CREATOR instance
     */
    public static Creator<UnbindRecord> getCREATOR() {
        return CREATOR;
    }

    /**
     * Default constructor for creating an empty {@link UnbindRecord}.
     */
    public UnbindRecord() {
    }

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
        dest.writeInt(this.mBindCount);
        dest.writeInt(this.mStartId);
        dest.writeParcelable(this.mComponentName, flags);
    }

    /**
     * Constructs an {@link UnbindRecord} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read the unbind record from
     */
    protected UnbindRecord(Parcel in) {
        this.mBindCount = in.readInt();
        this.mStartId = in.readInt();
        this.mComponentName = in.readParcelable(ComponentName.class.getClassLoader());
    }

    public static final Creator<UnbindRecord> CREATOR = new Creator<UnbindRecord>() {
        @Override
        public UnbindRecord createFromParcel(Parcel source) {
            return new UnbindRecord(source);
        }

        @Override
        public UnbindRecord[] newArray(int size) {
            return new UnbindRecord[size];
        }
    };
}
