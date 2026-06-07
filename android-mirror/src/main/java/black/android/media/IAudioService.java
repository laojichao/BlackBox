package black.android.media;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.media.IAudioService.
 * AIDL interface for the audio management system service.
 */
@BClassName("android.media.IAudioService")
public interface IAudioService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.media.IAudioService$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the IAudioService proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
