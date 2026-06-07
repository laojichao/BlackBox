package black.com.android.internal.telephony;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.telephony.ITelephony.
 * AIDL interface for the telephony system service (call management, SIM state).
 */
@BClassName("com.android.internal.telephony.ITelephony")
public interface ITelephony {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("com.android.internal.telephony.ITelephony$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the ITelephony proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
