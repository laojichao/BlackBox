package black.com.android.internal.telephony;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.telephony.ISub.
 * AIDL interface for the subscription management system service (dual SIM).
 */
@BClassName("com.android.internal.telephony.ISub")
public interface ISub {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("com.android.internal.telephony.ISub$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the ISub proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
