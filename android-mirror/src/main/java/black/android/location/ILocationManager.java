package black.android.location;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.location.ILocationManager.
 * AIDL interface for the location management system service.
 */
@BClassName("android.location.ILocationManager")
public interface ILocationManager {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.location.ILocationManager$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the ILocationManager proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
