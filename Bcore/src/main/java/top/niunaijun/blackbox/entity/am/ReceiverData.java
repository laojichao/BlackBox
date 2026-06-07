package top.niunaijun.blackbox.entity.am;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable data container that bundles the information needed to dispatch a broadcast
 * to a {@link android.content.BroadcastReceiver} within the BlackBox virtual environment.
 * <p>
 * Combines the incoming {@link Intent}, the receiver's {@link ActivityInfo}, and an
 * optional {@link PendingResultData} snapshot so the broadcast can be forwarded
 * across process boundaries.
 * </p>
 */
public class ReceiverData implements Parcelable {
    /** The broadcast intent to deliver to the receiver. */
    public Intent intent;

    /** The activity info metadata of the target broadcast receiver component. */
    public ActivityInfo activityInfo;

    /** The captured pending result data, or {@code null} if the broadcast does not require a result. */
    public PendingResultData data;

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
        dest.writeParcelable(this.intent, flags);
        dest.writeParcelable(this.activityInfo, flags);
        dest.writeParcelable(this.data, flags);
    }

    /**
     * Populates this instance's fields from the given {@link Parcel}.
     *
     * @param source the Parcel to read from
     */
    public void readFromParcel(Parcel source) {
        this.intent = source.readParcelable(Intent.class.getClassLoader());
        this.activityInfo = source.readParcelable(ActivityInfo.class.getClassLoader());
        this.data = source.readParcelable(PendingResultData.class.getClassLoader());
    }

    /**
     * Default constructor for creating an empty {@link ReceiverData}.
     */
    public ReceiverData() {
    }

    /**
     * Constructs a {@link ReceiverData} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    protected ReceiverData(Parcel in) {
        this.intent = in.readParcelable(Intent.class.getClassLoader());
        this.activityInfo = in.readParcelable(ActivityInfo.class.getClassLoader());
        this.data = in.readParcelable(PendingResultData.class.getClassLoader());
    }

    public static final Parcelable.Creator<ReceiverData> CREATOR = new Parcelable.Creator<ReceiverData>() {
        @Override
        public ReceiverData createFromParcel(Parcel source) {
            return new ReceiverData(source);
        }

        @Override
        public ReceiverData[] newArray(int size) {
            return new ReceiverData[size];
        }
    };
}
