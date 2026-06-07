package black.android.media;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.media.IMediaRouterService.
 * AIDL interface for the media routing system service.
 */
@BClassName("android.media.IMediaRouterService")
public interface IMediaRouterService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.media.IMediaRouterService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IMediaRouterService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
