package top.niunaijun.blackbox.utils.compat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import black.android.os.BRBundle;

/**
 * Compatibility wrapper for {@link android.os.Bundle} IBinder operations.
 * <p>
 * The public {@code Bundle.getBinder()} / {@code Bundle.putBinder()} methods were added
 * in API 18 (Jelly Bean MR2). On earlier versions, this class falls back to the hidden
 * {@code getIBinder()} / {@code putIBinder()} methods via reflection. Also provides
 * Intent-based helpers that wrap an IBinder inside a Bundle extra for cross-process transport.
 */
public class BundleCompat {
    /**
     * Retrieves an {@link IBinder} from a Bundle by key.
     * <p>
     * Uses the public API on API 18+ and the hidden {@code getIBinder} method on older versions.
     *
     * @param bundle the bundle to retrieve the binder from
     * @param key    the key associated with the binder
     * @return the IBinder stored under the given key, or null
     */
    public static IBinder getBinder(Bundle bundle, String key) {
        if (Build.VERSION.SDK_INT >= 18) {
            return bundle.getBinder(key);
        } else {
            return BRBundle.get(bundle).getIBinder(key);
        }
    }

    /**
     * Stores an {@link IBinder} into a Bundle by key.
     * <p>
     * Uses the public API on API 18+ and the hidden {@code putIBinder} method on older versions.
     *
     * @param bundle the bundle to store the binder in
     * @param key    the key to associate with the binder
     * @param value  the IBinder to store
     */
    public static void putBinder(Bundle bundle, String key, IBinder value) {
        if (Build.VERSION.SDK_INT >= 18) {
            bundle.putBinder(key, value);
        } else {
            BRBundle.get(bundle).putIBinder(key, value);
        }
    }

    /**
     * Stores an {@link IBinder} into an Intent by wrapping it in a Bundle extra.
     * <p>
     * The binder is placed into a new Bundle under the key "binder", and the Bundle
     * is stored as a bundle extra in the Intent under the specified key.
     *
     * @param intent the Intent to store the binder in
     * @param key    the extra key for the wrapping Bundle
     * @param value  the IBinder to store
     */
    public static void putBinder(Intent intent, String key, IBinder value) {
        Bundle bundle = new Bundle();
        putBinder(bundle, "binder", value);
        intent.putExtra(key, bundle);
    }

    /**
     * Retrieves an {@link IBinder} from an Intent's bundle extra.
     * <p>
     * Expects the IBinder to have been stored via {@link #putBinder(Intent, String, IBinder)}
     * inside a Bundle extra under the given key.
     *
     * @param intent the Intent to retrieve the binder from
     * @param key    the extra key of the wrapping Bundle
     * @return the IBinder stored inside the bundle extra, or null if not found
     */
    public static IBinder getBinder(Intent intent, String key) {
        Bundle bundle = intent.getBundleExtra(key);
        if (bundle != null) {
            return getBinder(bundle, "binder");
        }
        return null;
    }

//    public static void clearParcelledData(Bundle bundle) {
//        Parcel obtain = Parcel.obtain();
//        obtain.writeInt(0);
//        obtain.setDataPosition(0);
//        Parcel parcel;
//        if (BaseBundle.TYPE != null) {
//            parcel = BaseBundle.mParcelledData.get(bundle);
//            if (parcel != null) {
//                parcel.recycle();
//            }
//            BaseBundle.mParcelledData.set(bundle, obtain);
//        } else if (BundleICS.TYPE != null) {
//            parcel = BundleICS.mParcelledData.get(bundle);
//            if (parcel != null) {
//                parcel.recycle();
//            }
//            BundleICS.mParcelledData.set(bundle, obtain);
//        }
//    }
}
