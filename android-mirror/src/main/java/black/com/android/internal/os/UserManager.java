package black.com.android.internal.os;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.IUserManager (via internal Stub).
 * Provides access to the user manager service binder through the Stub pattern.
 */
@BClassName("android.os.UserManager")
public interface UserManager {
    /**
     * Mirror of the IUserManager.Stub inner class for obtaining the service proxy.
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
