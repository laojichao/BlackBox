package black.android.app;

import android.content.Intent;
import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Reflection mirror for {@code android.app.IActivityManager} (Lollipop variant).
 * Provides the {@code finishActivity} method with the pre-N signature.
 */
@BClassName("android.app.IActivityManager")
public interface IActivityManagerL {
    /** Finishes the activity associated with the given token (Lollipop API variant). */
    @BMethod
    Boolean finishActivity(IBinder IBinder0, int int1, Intent Intent2, boolean boolean3);
}
