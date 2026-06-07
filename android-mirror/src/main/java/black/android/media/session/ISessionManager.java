package black.android.media.session;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.media.session.ISessionManager.
 * AIDL interface for the media session management system service.
 */
@BClassName("android.media.session.ISessionManager")
public interface ISessionManager {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.media.session.ISessionManager$Stub")
    interface Stub {
        /**
         * Convert a raw IBinder to the ISessionManager proxy.
         */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
