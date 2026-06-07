package black.android.app;

import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;

/**
 * Reflection mirror for {@code android.app.PendingIntent} (Oreo variant).
 * Provides access to the hidden PendingIntent constructor with an extra user handle parameter.
 */
@BClassName("android.app.PendingIntent")
public interface PendingIntentO {
    /** Creates a new PendingIntent from an IBinder token and a user handle (Oreo variant). */
    @BConstructor
    PendingIntentO _new(IBinder IBinder0, Object Object1);
}
