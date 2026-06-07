package black.android.app;

import android.content.Intent;
import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Reflection mirror for {@code android.app.IActivityManager} (Nougat variant).
 * Provides the {@code finishActivity} method with the N+ signature.
 */
@BClassName("android.app.IActivityManager")
public interface IActivityManagerN {
    /** Finishes the activity associated with the given token (Nougat API variant). */
    @BMethod
    Boolean finishActivity(IBinder IBinder0, int int1, Intent Intent2, int int3);
}
