package black.com.android.internal.telephony;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.telephony.IMms.
 * AIDL interface for the MMS (multimedia messaging) system service.
 */
@BClassName("com.android.internal.telephony.IMms")
public interface IMms {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("com.android.internal.telephony.IMms$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IMms proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
