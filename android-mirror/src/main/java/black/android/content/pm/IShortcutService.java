package black.android.content.pm;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.content.pm.IShortcutService.
 * AIDL interface for the shortcut management system service.
 */
@BClassName("android.content.pm.IShortcutService")
public interface IShortcutService {
    /**
     * Mirror of the Stub inner class for obtaining the service proxy.
     */
    @BClassName("android.content.pm.IShortcutService$Stub")
    interface Stub {
        /** Converts a raw IBinder to an IShortcutService proxy interface. */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
