package black.android.view;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.view.autofill.IAutoFillManager.
 * AIDL interface for the autofill management system service.
 */
@BClassName("android.view.autofill.IAutoFillManager")
public interface IAutoFillManager {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.view.autofill.IAutoFillManager$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IAutoFillManager proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
