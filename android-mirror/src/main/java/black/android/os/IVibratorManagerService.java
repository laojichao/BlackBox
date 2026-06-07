package black.android.os;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.IVibratorManagerService.
 * AIDL interface for the vibrator manager system service.
 */
@BClassName("android.os.IVibratorManagerService")
public interface IVibratorManagerService {

    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.os.IVibratorManagerService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IVibratorManagerService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
