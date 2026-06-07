package black.android.app;

import android.os.IBinder;

import java.util.List;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Reflection mirror for {@code android.app.ActivityThread} (Q / API 29+).
 * Provides the {@code handleNewIntent} variant introduced in Android Q,
 * replacing the older {@code performNewIntents} path.
 */
@BClassName("android.app.ActivityThread")
public interface ActivityThreadQ {
    /** Handles delivery of new intents to the activity identified by the binder token. */
    @BMethod
    void handleNewIntent(IBinder IBinder0, List List1);
}
