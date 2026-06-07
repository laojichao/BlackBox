package top.niunaijun.blackbox.entity;

import android.app.job.JobInfo;
import android.app.job.JobService;
import android.content.pm.ServiceInfo;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Parcelable record that binds a scheduled {@link JobInfo} to its hosting {@link ServiceInfo}
 * within the BlackBox virtual environment.
 * <p>
 * Used by the virtualized {@link JobScheduler} to track which {@link JobService} is
 * responsible for executing a particular job and the corresponding service metadata.
 * </p>
 */
public class JobRecord implements Parcelable {

    /** The job scheduling constraints and parameters defined for this job. */
    public JobInfo mJobInfo;

    /** Metadata about the service component that hosts and executes this job. */
    public ServiceInfo mServiceInfo;

    /**
     * The live {@link JobService} instance bound to this record.
     * This field is not parcelled as it represents a runtime reference.
     */
    public JobService mJobService;

    /**
     * Default constructor for creating an empty {@link JobRecord}.
     */
    public JobRecord() {
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
        dest.writeParcelable(this.mJobInfo, flags);
        dest.writeParcelable(this.mServiceInfo, flags);
    }

    /**
     * Constructs a {@link JobRecord} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read the job record from
     */
    protected JobRecord(Parcel in) {
        this.mJobInfo = in.readParcelable(JobInfo.class.getClassLoader());
        this.mServiceInfo = in.readParcelable(ServiceInfo.class.getClassLoader());
    }

    public static final Creator<JobRecord> CREATOR = new Creator<JobRecord>() {
        @Override
        public JobRecord createFromParcel(Parcel source) {
            return new JobRecord(source);
        }

        @Override
        public JobRecord[] newArray(int size) {
            return new JobRecord[size];
        }
    };
}
