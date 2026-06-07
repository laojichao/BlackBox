package black.android.location;

import android.location.Location;
import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.location.ILocationListener.
 * AIDL interface for receiving location update callbacks from the system.
 */
@BClassName("android.location.ILocationListener")
public interface ILocationListener {
    /**
     * Called when the location has changed.
     */
    @BMethod
    void onLocationChanged(Location Location0);

    /**
     * Mirror of the Stub inner class for obtaining the listener proxy.
     */
    @BClassName("android.location.ILocationListener$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the ILocationListener proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
