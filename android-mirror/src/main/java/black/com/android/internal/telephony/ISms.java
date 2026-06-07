package black.com.android.internal.telephony;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.telephony.ISms.
 * AIDL interface for the SMS (short message service) system service.
 */
@BClassName("com.android.internal.telephony.ISms")
public interface ISms {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("com.android.internal.telephony.ISms$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the ISms proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
