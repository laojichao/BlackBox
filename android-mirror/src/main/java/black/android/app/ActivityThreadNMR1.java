package black.android.app;

import android.os.IBinder;

import java.util.List;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Reflection mirror for {@code android.app.ActivityThread} (N MR1 / API 25+).
 * Provides the overloaded {@code performNewIntents} variant that includes
 * a boolean parameter for resumed-state notification.
 */
@BClassName("android.app.ActivityThread")
public interface ActivityThreadNMR1 {
    /** Delivers new intents with a resumed-state flag (N MR1+ variant). */
    @BMethod
    void performNewIntents(IBinder IBinder0, List List1, boolean boolean2);
}
