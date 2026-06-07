package black.android.view;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.view.IWindowManager.
 * AIDL interface for the window management system service.
 */
@BClassName("android.view.IWindowManager")
public interface IWindowManager {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.view.IWindowManager$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IWindowManager proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
