package black.android.app;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.NotificationManager}.
 * Provides access to the hidden static service field and getter
 * of the system notification manager.
 */
@BClassName("android.app.NotificationManager")
public interface NotificationManager {
    /** The static cached INotificationManager service binder. */
    @BStaticField
    IInterface sService();

    /** Returns the INotificationManager system service interface. */
    @BStaticMethod
    IInterface getService();
}
