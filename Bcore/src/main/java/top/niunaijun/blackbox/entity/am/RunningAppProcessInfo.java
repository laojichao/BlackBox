package top.niunaijun.blackbox.entity.am;

import android.app.ActivityManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Parcelable wrapper around a list of {@link ActivityManager.RunningAppProcessInfo} entries
 * within the BlackBox virtual environment.
 * <p>
 * Used to transport the set of running virtual processes across IPC boundaries,
 * enabling the host to query and manage the virtual process list.
 * </p>
 */
public class RunningAppProcessInfo implements Parcelable {
    /** The list of running application process information entries. */
    public List<ActivityManager.RunningAppProcessInfo> mAppProcessInfoList;

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
        dest.writeTypedList(this.mAppProcessInfoList);
    }

    /**
     * Populates this instance's fields from the given {@link Parcel}.
     *
     * @param source the Parcel to read from
     */
    public void readFromParcel(Parcel source) {
        this.mAppProcessInfoList = source.createTypedArrayList(ActivityManager.RunningAppProcessInfo.CREATOR);
    }

    /**
     * Default constructor that initializes an empty process info list.
     */
    public RunningAppProcessInfo() {
        mAppProcessInfoList = new ArrayList<>();
    }

    /**
     * Constructs a {@link RunningAppProcessInfo} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    protected RunningAppProcessInfo(Parcel in) {
        this.mAppProcessInfoList = in.createTypedArrayList(ActivityManager.RunningAppProcessInfo.CREATOR);
    }

    public static final Parcelable.Creator<RunningAppProcessInfo> CREATOR = new Parcelable.Creator<RunningAppProcessInfo>() {
        @Override
        public RunningAppProcessInfo createFromParcel(Parcel source) {
            return new RunningAppProcessInfo(source);
        }

        @Override
        public RunningAppProcessInfo[] newArray(int size) {
            return new RunningAppProcessInfo[size];
        }
    };
}
