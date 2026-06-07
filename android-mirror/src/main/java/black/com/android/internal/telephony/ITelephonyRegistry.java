package black.com.android.internal.telephony;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.telephony.ITelephonyRegistry.
 * AIDL interface for the telephony event registry system service.
 */
@BClassName("com.android.internal.telephony.ITelephonyRegistry")
public interface ITelephonyRegistry {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("com.android.internal.telephony.ITelephonyRegistry$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the ITelephonyRegistry proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
