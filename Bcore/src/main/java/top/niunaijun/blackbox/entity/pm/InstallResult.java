package top.niunaijun.blackbox.entity.pm;

import android.os.Parcel;
import android.os.Parcelable;

import top.niunaijun.blackbox.utils.Slog;

/**
 * Parcelable result object returned after attempting to install an application
 * into the BlackBox virtual environment.
 * <p>
 * Captures whether the installation succeeded, the package name of the installed
 * application, and an optional error message if the installation failed. Builder-style
 * error methods allow fluent construction of failure results.
 * </p>
 */
public class InstallResult implements Parcelable {
    /** Logging tag for this class. */
    public static final String TAG = "InstallResult";

    /** Whether the installation was successful. Defaults to {@code true}. */
    public boolean success = true;

    /** The package name of the application that was installed (or attempted). */
    public String packageName;

    /** An optional message, typically describing the error if installation failed. */
    public String msg;

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
        dest.writeByte(this.success ? (byte) 1 : (byte) 0);
        dest.writeString(this.packageName);
        dest.writeString(this.msg);
    }

    /**
     * Default constructor for creating a successful {@link InstallResult}.
     */
    public InstallResult() {
    }

    /**
     * Constructs an {@link InstallResult} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    protected InstallResult(Parcel in) {
        this.success = in.readByte() != 0;
        this.packageName = in.readString();
        this.msg = in.readString();
    }

    /**
     * Marks this result as a failure and records the error details.
     *
     * @param packageName the package name that failed to install
     * @param msg         the error message describing the failure
     * @return this instance for method chaining
     */
    public InstallResult installError(String packageName, String msg) {
        this.msg = msg;
        this.success = false;
        this.packageName = packageName;
        Slog.d(TAG, msg);
        return this;
    }

    /**
     * Marks this result as a failure and records the error message.
     *
     * @param msg the error message describing the failure
     * @return this instance for method chaining
     */
    public InstallResult installError(String msg) {
        this.msg = msg;
        this.success = false;
        Slog.d(TAG, msg);
        return this;
    }

    public static final Parcelable.Creator<InstallResult> CREATOR = new Parcelable.Creator<InstallResult>() {
        @Override
        public InstallResult createFromParcel(Parcel source) {
            return new InstallResult(source);
        }

        @Override
        public InstallResult[] newArray(int size) {
            return new InstallResult[size];
        }
    };
}
