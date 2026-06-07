package black.android.net;

import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.net.NetworkInfo constructors and fields.
 * Allows creating NetworkInfo instances with specific type, subtype, name, and extra info.
 */
@BClassName("android.net.NetworkInfo")
public interface NetworkInfo {
    /**
     * Construct a NetworkInfo with full parameters.
     */
    @BConstructor
    NetworkInfo _new(int int0, int int1, String String2, String String3);

    /**
     * Construct a NetworkInfo with only the network type.
     */
    @BConstructor
    NetworkInfo _new(int int0);

    /** The detailed connection state. */
    @BField
    DetailedState mDetailedState();

    /** Whether the network is currently available. */
    @BField
    boolean mIsAvailable();

    /** The network type constant (e.g., ConnectivityManager.TYPE_WIFI). */
    @BField
    int mNetworkType();

    /** The general connection state. */
    @BField
    State mState();

    /** The human-readable network type name (e.g., "WIFI"). */
    @BField
    String mTypeName();
}
