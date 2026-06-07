package black.android.content.pm;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden mirror.android.content.pm.ILauncherApps.
 * AIDL interface for the launcher apps system service.
 */
@BClassName("mirror.android.content.pm.ILauncherApps")
public interface ILauncherApps {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.content.pm.ILauncherApps$Stub")
    interface Stub {
        /** Converts a raw IBinder to an ILauncherApps proxy interface. */
        @BStaticMethod
        IInterface asInterface(IBinder binder);
    }
}
