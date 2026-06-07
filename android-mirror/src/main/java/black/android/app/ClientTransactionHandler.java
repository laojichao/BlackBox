package black.android.app;

import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Reflection mirror for {@code android.app.ClientTransactionHandler}.
 * Provides access to the hidden {@code getActivityClient} method,
 * which retrieves the ActivityClientController for a given activity token.
 * ActivityThread extends ClientTransactionHandler in Android P+.
 */
@BClassName("android.app.ClientTransactionHandler")
public interface ClientTransactionHandler {
    /** Returns the ActivityClientController for the activity identified by the given IBinder token. */
    @BMethod
    Object getActivityClient(IBinder IBinder0);
}
