package black.android.app;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.ISearchManager}.
 * Provides access to the Search Manager AIDL service stub.
 */
@BClassName("android.app.ISearchManager")
public interface ISearchManager {
    /**
     * Reflection mirror for {@code android.app.ISearchManager.Stub}.
     * Converts an IBinder to the ISearchManager interface.
     */
    @BClassName("android.app.ISearchManager$Stub")
    interface Stub {
        /** Converts a raw IBinder to the ISearchManager proxy interface. */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
