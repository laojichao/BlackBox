package black.com.android.internal.telecom;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.telecom.ITelecomService.
 * AIDL interface for the telecom system service.
 */
@BClassName("com.android.internal.telecom.ITelecomService")
public interface ITelecomService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("com.android.internal.telecom.ITelecomService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the ITelecomService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
