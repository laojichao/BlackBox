package black.android.telephony;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.telephony.TelephonyManager fields and methods.
 * Provides access to the subscriber info service and phone sub info binder.
 */
@BClassName("android.telephony.TelephonyManager")
public interface TelephonyManager {

    /** Get the IPhoneSubInfo service for accessing subscriber details. */
    @BStaticMethod
    Object getSubscriberInfoService();

    /** Whether the service handle cache is enabled. */
    @BStaticField
    boolean sServiceHandleCacheEnabled();

    /** Cached IPhoneSubInfo binder interface. */
    @BStaticField
    IInterface sIPhoneSubInfo();
}
