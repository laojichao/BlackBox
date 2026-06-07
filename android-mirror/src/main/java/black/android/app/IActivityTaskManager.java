package black.android.app;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.IActivityTaskManager}.
 * Provides access to the Activity Task Manager AIDL service (Android Q+).
 */
@BClassName("android.app.IActivityTaskManager")
public interface IActivityTaskManager {
    /**
     * Reflection mirror for {@code android.app.IActivityTaskManager.Stub}.
     * Converts an IBinder to the IActivityTaskManager interface.
     */
    @BClassName("android.app.IActivityTaskManager$Stub")
    interface Stub {
        /** Converts a raw IBinder to the IActivityTaskManager proxy interface. */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
