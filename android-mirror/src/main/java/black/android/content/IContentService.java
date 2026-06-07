package black.android.content;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.content.IContentService.
 * AIDL interface for the content synchronization system service.
 */
@BClassName("android.content.IContentService")
public interface IContentService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.content.IContentService$Stub")
    interface Stub {
        /** Converts a raw IBinder to an IContentService proxy interface. */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
