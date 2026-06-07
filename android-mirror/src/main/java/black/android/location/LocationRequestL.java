package black.android.location;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.location.LocationRequest fields and methods.
 * Provides access to internal LocationRequest configuration.
 */
@BClassName("android.location.LocationRequest")
public interface LocationRequestL {
    /** Whether this request is hidden from AppOps tracking. */
    @BField
    boolean mHideFromAppOps();

    /** The provider name for this location request. */
    @BField
    String mProvider();

    /** The WorkSource associated with this request. */
    @BField
    Object mWorkSource();

    /** Get the provider name for this location request. */
    @BMethod
    String getProvider();
}
