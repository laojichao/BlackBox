package black.com.android.internal.os;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.IVibratorService.
 * AIDL interface for the vibrator system service.
 */
@BClassName("android.os.IVibratorService")
public interface IVibratorService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.os.IVibratorService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IVibratorService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
