package black.android.app;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.IApplicationThread} (Oreo variant).
 * Provides Oreo-specific scheduling methods and the Stub for obtaining
 * the IApplicationThread proxy.
 */
@BClassName("android.app.IApplicationThread")
public interface IApplicationThreadOreo {
    /** Schedules service argument delivery (Oreo variant with no parameters reflected). */
    @BMethod
    void scheduleServiceArgs();

    /**
     * Reflection mirror for {@code android.app.IApplicationThread.Stub}.
     * Converts an IBinder to the IApplicationThread interface.
     */
    @BClassName("android.app.IApplicationThread$Stub")
    interface Stub {
        /** Converts a raw IBinder to the IApplicationThread proxy interface. */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
