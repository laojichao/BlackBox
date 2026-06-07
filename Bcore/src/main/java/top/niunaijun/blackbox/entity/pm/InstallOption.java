package top.niunaijun.blackbox.entity.pm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable options specifying how an application should be installed in the
 * BlackBox virtual environment.
 * <p>
 * Uses a bitmask-based flags field to combine multiple installation options such as
 * install source (system vs. storage), Xposed module support, and URI-based file
 * installation. Factory methods and builder-style modifiers allow fluent configuration.
 * </p>
 */
public class InstallOption implements Parcelable {
    /** Flag indicating the APK should be installed from the system partition. */
    public static final int FLAG_SYSTEM = 1;

    /** Flag indicating the APK should be installed from external storage. */
    public static final int FLAG_STORAGE = 1 << 1;

    /** Flag indicating the package is an Xposed module. */
    public static final int FLAG_XPOSED = 1 << 2;

    /** Flag indicating the install source is a content URI file reference. */
    public static final int FLAG_URI_FILE = 1 << 3;

    /** The combined bitmask of all active installation flags. */
    public int flags = 0;

    /**
     * Creates an {@link InstallOption} configured for system-partition installation.
     *
     * @return a new InstallOption with {@link #FLAG_SYSTEM} set
     */
    public static InstallOption installBySystem() {
        InstallOption installOption = new InstallOption();
        installOption.flags = installOption.flags | FLAG_SYSTEM;
        return installOption;
    }

    /**
     * Creates an {@link InstallOption} configured for storage-based installation.
     *
     * @return a new InstallOption with {@link #FLAG_STORAGE} set
     */
    public static InstallOption installByStorage() {
        InstallOption installOption = new InstallOption();
        installOption.flags = installOption.flags | FLAG_STORAGE;
        return installOption;
    }

    /**
     * Marks this install option as an Xposed module by setting {@link #FLAG_XPOSED}.
     *
     * @return this instance for method chaining
     */
    public InstallOption makeXposed() {
        this.flags |= FLAG_XPOSED;
        return this;
    }

    /**
     * Marks this install option as a URI-based file install by setting {@link #FLAG_URI_FILE}.
     *
     * @return this instance for method chaining
     */
    public InstallOption makeUriFile() {
        this.flags |= FLAG_URI_FILE;
        return this;
    }

    /**
     * Checks whether the given flag is set in this option's bitmask.
     *
     * @param flag the flag to check (e.g., {@link #FLAG_SYSTEM})
     * @return {@code true} if the flag is set
     */
    public boolean isFlag(int flag) {
        return (flags & flag) != 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.flags);
    }

    /**
     * Default constructor for creating an {@link InstallOption} with no flags set.
     */
    public InstallOption() {
    }

    /**
     * Constructs an {@link InstallOption} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    protected InstallOption(Parcel in) {
        this.flags = in.readInt();
    }

    public static final Parcelable.Creator<InstallOption> CREATOR = new Parcelable.Creator<InstallOption>() {
        @Override
        public InstallOption createFromParcel(Parcel source) {
            return new InstallOption(source);
        }

        @Override
        public InstallOption[] newArray(int size) {
            return new InstallOption[size];
        }
    };
}
