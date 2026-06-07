package black.android.view;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.view.IGraphicsStats.
 * AIDL interface for the graphics statistics system service.
 */
@BClassName("android.view.IGraphicsStats")
public interface IGraphicsStats {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.view.IGraphicsStats$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IGraphicsStats proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
