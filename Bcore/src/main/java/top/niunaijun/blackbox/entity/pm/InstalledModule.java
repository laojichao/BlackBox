package top.niunaijun.blackbox.entity.pm;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.system.user.BUserHandle;

/**
 * Parcelable representation of an installed Xposed module within the BlackBox
 * virtual environment.
 * <p>
 * Stores the module's package name, display name, description, main entry class,
 * and enabled/disabled state. Provides convenience methods to retrieve the module's
 * {@link ApplicationInfo} and {@link PackageInfo} from the virtual package manager.
 * </p>
 */
public class InstalledModule implements Parcelable {
    /** The package name of the Xposed module. */
    public String packageName;

    /** The human-readable display name of the Xposed module. */
    public String name;

    /** A short description of the Xposed module's functionality. */
    public String desc;

    /** The fully qualified class name of the module's main entry point. */
    public String main;

    /** Whether this module is currently enabled in the virtual environment. */
    public boolean enable;

    /**
     * Default constructor for creating an empty {@link InstalledModule}.
     */
    public InstalledModule() {
    }


    /**
     * Returns the {@link ApplicationInfo} for this module from the virtual package manager.
     *
     * @return the application info, or {@code null} if the module is not found
     */
    public ApplicationInfo getApplication() {
        return BlackBoxCore.getBPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA, BUserHandle.USER_XPOSED);
    }

    /**
     * Returns the {@link PackageInfo} for this module from the virtual package manager.
     *
     * @return the package info, or {@code null} if the module is not found
     */
    public PackageInfo getPackageInfo() {
        return BlackBoxCore.getBPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA, BUserHandle.USER_XPOSED);
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
        dest.writeString(this.packageName);
        dest.writeString(this.name);
        dest.writeString(this.desc);
        dest.writeString(this.main);
        dest.writeByte(this.enable ? (byte) 1 : (byte) 0);
    }

    /**
     * Constructs an {@link InstalledModule} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    protected InstalledModule(Parcel in) {
        this.packageName = in.readString();
        this.name = in.readString();
        this.desc = in.readString();
        this.main = in.readString();
        this.enable = in.readByte() != 0;
    }

    public static final Creator<InstalledModule> CREATOR = new Creator<InstalledModule>() {
        @Override
        public InstalledModule createFromParcel(Parcel source) {
            return new InstalledModule(source);
        }

        @Override
        public InstalledModule[] newArray(int size) {
            return new InstalledModule[size];
        }
    };
}
