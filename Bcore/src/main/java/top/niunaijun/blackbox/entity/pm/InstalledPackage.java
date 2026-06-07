package top.niunaijun.blackbox.entity.pm;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Parcelable representation of an application installed within a specific user
 * in the BlackBox virtual environment.
 * <p>
 * Combines the package name with a user ID to uniquely identify an installed
 * application. Provides convenience methods to retrieve the application's
 * {@link ApplicationInfo} and {@link PackageInfo} from the virtual package manager.
 * Equality is based on the package name alone.
 * </p>
 */
public class InstalledPackage implements Parcelable {
    /** The virtual user ID under which the package is installed. */
    public int userId;

    /** The package name of the installed application. */
    public String packageName;

    /**
     * Returns the {@link ApplicationInfo} for this package from the virtual package manager.
     *
     * @return the application info, or {@code null} if the package is not found
     */
    public ApplicationInfo getApplication() {
        return BlackBoxCore.getBPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA, userId);
    }

    /**
     * Returns the {@link PackageInfo} for this package from the virtual package manager.
     *
     * @return the package info, or {@code null} if the package is not found
     */
    public PackageInfo getPackageInfo() {
        return BlackBoxCore.getBPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA, userId);
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
        dest.writeInt(this.userId);
        dest.writeString(this.packageName);
    }

    /**
     * Default constructor for creating an empty {@link InstalledPackage}.
     */
    public InstalledPackage() {
    }

    /**
     * Constructs an {@link InstalledPackage} with the given package name.
     *
     * @param packageName the package name of the installed application
     */
    public InstalledPackage(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Constructs an {@link InstalledPackage} by reading its fields from the given {@link Parcel}.
     *
     * @param in the Parcel to read from
     */
    protected InstalledPackage(Parcel in) {
        this.userId = in.readInt();
        this.packageName = in.readString();
    }

    /**
     * Compares this package with another object for equality based on the package name.
     *
     * @param o the object to compare with
     * @return {@code true} if the other object is an {@link InstalledPackage} with the same package name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstalledPackage that = (InstalledPackage) o;
        return Objects.equals(packageName, that.packageName);
    }

    /**
     * Returns a hash code based on the package name.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(packageName);
    }

    public static final Parcelable.Creator<InstalledPackage> CREATOR = new Parcelable.Creator<InstalledPackage>() {
        @Override
        public InstalledPackage createFromParcel(Parcel source) {
            return new InstalledPackage(source);
        }

        @Override
        public InstalledPackage[] newArray(int size) {
            return new InstalledPackage[size];
        }
    };
}
