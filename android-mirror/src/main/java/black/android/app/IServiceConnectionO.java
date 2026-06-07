package black.android.app;

import android.content.ComponentName;
import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Reflection mirror for {@code android.app.IServiceConnection}.
 * Provides access to the hidden service connection callback used by the system
 * when a service is connected or disconnected.
 */
@BClassName("android.app.IServiceConnection")
public interface IServiceConnectionO {
    /** Called when a service connection has been established to the given ComponentName. */
    @BMethod
    void connected(ComponentName ComponentName0, IBinder IBinder1, boolean boolean2);
}
