package black.android.permission;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.permission.IPermissionManager.
 * AIDL interface for the permission management system service.
 */
@BClassName("android.permission.IPermissionManager")
public interface IPermissionManager {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.permission.IPermissionManager$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IPermissionManager proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
