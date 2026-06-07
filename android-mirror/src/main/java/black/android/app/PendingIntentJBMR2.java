package black.android.app;

import android.content.Intent;
import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Reflection mirror for {@code android.app.PendingIntent} (JellyBean MR2 variant).
 * Provides access to the hidden constructor and the internal intent getter.
 */
@BClassName("android.app.PendingIntent")
public interface PendingIntentJBMR2 {
    /** Creates a new PendingIntent wrapping the given IBinder token. */
    @BConstructor
    PendingIntentJBMR2 _new(IBinder IBinder0);

    /** Returns the Intent associated with this PendingIntent. */
    @BMethod
    Intent getIntent();
}
