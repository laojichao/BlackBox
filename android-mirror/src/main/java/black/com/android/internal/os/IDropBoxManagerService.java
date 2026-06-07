package black.com.android.internal.os;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden com.android.internal.os.IDropBoxManagerService.
 * AIDL interface for the drop box (crash/log entry) system service.
 */
@BClassName("com.android.internal.os.IDropBoxManagerService")
public interface IDropBoxManagerService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("com.android.internal.os.IDropBoxManagerService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IDropBoxManagerService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
