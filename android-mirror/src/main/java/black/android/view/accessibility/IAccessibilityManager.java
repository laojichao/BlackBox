package black.android.view.accessibility;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.view.accessibility.IAccessibilityManager.
 * AIDL interface for the accessibility management system service.
 */
@BClassName("android.view.accessibility.IAccessibilityManager")
public interface IAccessibilityManager {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.view.accessibility.IAccessibilityManager$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IAccessibilityManager proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
