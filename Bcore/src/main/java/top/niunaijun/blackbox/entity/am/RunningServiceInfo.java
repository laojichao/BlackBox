package top.niunaijun.blackbox.entity.am;

import android.app.ActivityManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Parcelable wrapper around a list of {@link ActivityManager.RunningServiceInfo} entries
 * within the BlackBox virtual environment.
 * <p>
 * Used to transport the set of currently running virtual services across IPC boundaries,
 * enabling the host to query and manage services inside the virtual container.
 * </p>
 */
public class RunningServiceInfo implements Parcelable {
    /** The list of running service information entries. */
    public List<ActivityManager.RunningServiceInfo> mRunningServiceInfoList;

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
        dest.writeTypedList(this.mRunningServiceInfoList);
    }

    /**
     * Populates this instance's fields from the given {@link Parcel}.
     *
     * @param source the Parcel to read from
     */
    public void readFromParcel(Parcel source) {
        this.mRunningServiceInfoList = source.createTypedArrayList(ActivityManager.RunningServiceInfo.CREATOR);
    }

    /**
     * Default constructor that initializes an empty service info list.
     */
    public RunningServiceInfo() {
        mRunningServiceInfoList = new ArrayList<>();
    }

    /**
     * Constructs a {@link RunningServiceInfo} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    protected RunningServiceInfo(Parcel in) {
        this.mRunningServiceInfoList = in.createTypedArrayList(ActivityManager.RunningServiceInfo.CREATOR);
    }

    public static final Parcelable.Creator<RunningServiceInfo> CREATOR = new Parcelable.Creator<RunningServiceInfo>() {
        @Override
        public RunningServiceInfo createFromParcel(Parcel source) {
            return new RunningServiceInfo(source);
        }

        @Override
        public RunningServiceInfo[] newArray(int size) {
            return new RunningServiceInfo[size];
        }
    };
}
