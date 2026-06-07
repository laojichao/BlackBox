package top.niunaijun.blackbox.entity;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Parcelable configuration data for an application running inside a BlackBox virtual environment.
 * <p>
 * This class holds the runtime identity and process information for an app instance,
 * including its virtual package name, process name, and various UID/PID mappings used
 * to isolate and manage the app within the virtual container.
 * </p>
 */
public class AppConfig implements Parcelable {
    /** Preference key used to store this configuration in the BlackBox client. */
    public static final String KEY = "BlackBox_client_config";

    /** The package name of the application in the virtual environment. */
    public String packageName;

    /** The process name assigned to the application within the virtual environment. */
    public String processName;

    /** The BlackBox virtual process ID for this application instance. */
    public int bpid;

    /** The BlackBox virtual UID for this application instance. */
    public int buid;

    /** The actual Linux UID assigned to the application process on the host system. */
    public int uid;

    /** The virtual user ID under which this application is installed in BlackBox. */
    public int userId;

    /** The BlackBox virtual UID of the caller that initiated this application's launch. */
    public int callingBUid;

    /** An IPC binder token used to identify and communicate with this application's process. */
    public IBinder token;

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
        dest.writeString(this.packageName);
        dest.writeString(this.processName);
        dest.writeInt(this.bpid);
        dest.writeInt(this.buid);
        dest.writeInt(this.uid);
        dest.writeInt(this.userId);
        dest.writeInt(this.callingBUid);
        dest.writeStrongBinder(token);
    }

    /**
     * Default constructor for creating an empty {@link AppConfig}.
     */
    public AppConfig() {
    }

    /**
     * Constructs an {@link AppConfig} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read the configuration from
     */
    protected AppConfig(Parcel in) {
        this.packageName = in.readString();
        this.processName = in.readString();
        this.bpid = in.readInt();
        this.buid = in.readInt();
        this.uid = in.readInt();
        this.userId = in.readInt();
        this.callingBUid = in.readInt();
        this.token = in.readStrongBinder();
    }

    public static final Parcelable.Creator<AppConfig> CREATOR = new Parcelable.Creator<AppConfig>() {
        @Override
        public AppConfig createFromParcel(Parcel source) {
            return new AppConfig(source);
        }

        @Override
        public AppConfig[] newArray(int size) {
            return new AppConfig[size];
        }
    };
}
