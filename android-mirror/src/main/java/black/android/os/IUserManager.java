package black.android.os;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.IUserManager.
 * AIDL interface for the user management system service.
 */
@BClassName("android.os.IUserManager")
public interface IUserManager {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.os.IUserManager$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IUserManager proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
