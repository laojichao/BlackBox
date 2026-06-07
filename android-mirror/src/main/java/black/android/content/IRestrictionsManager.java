package black.android.content;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.content.IRestrictionsManager.
 * AIDL interface for the device restrictions management system service.
 */
@BClassName("android.content.IRestrictionsManager")
public interface IRestrictionsManager {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.content.IRestrictionsManager$Stub")
    interface Stub {
        /** Converts a raw IBinder to an IRestrictionsManager proxy interface. */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
