package black.com.android.internal.telephony;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.telephony.IPhoneSubInfo.
 * AIDL interface for accessing phone subscriber information (IMEI, IMSI, etc.).
 */
@BClassName("com.android.internal.telephony.IPhoneSubInfo")
public interface IPhoneSubInfo {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("com.android.internal.telephony.IPhoneSubInfo$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IPhoneSubInfo proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
