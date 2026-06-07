package android.content.res;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stub for the Android framework's {@code android.content.res.CompatibilityInfo} class.
 *
 * <p>This class is used by {@link android.app.AndroidAppHelper} to register resources with the
 * correct compatibility scaling. The actual implementation is provided by the Android framework
 * at runtime; this stub exists only to allow compile-time access from Xposed module code.
 *
 * @hide
 */
public class CompatibilityInfo implements Parcelable {
	/** @hide */
	@Override
	public int describeContents() {
		throw new UnsupportedOperationException("Stub!");
	}

	/** @hide */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		throw new UnsupportedOperationException("Stub!");
	}

	/** Parcelable creator, provided by the framework at runtime. */
	public static final Creator<CompatibilityInfo> CREATOR = null;
}
