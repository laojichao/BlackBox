package black.android.app;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.ActivityClient} (API 29+).
 * Provides access to the ActivityClient singleton and its controller,
 * which handles activity lifecycle operations on behalf of the application.
 */
@BClassName("android.app.ActivityClient")
public interface ActivityClient {
    /** Returns the hidden static {@code INTERFACE_SINGLETON} field. */
    @BField
    Object INTERFACE_SINGLETON();

    /** Returns the singleton ActivityClient instance via reflection. */
    @BStaticMethod
    Object getInstance();

    /** Returns the current ActivityClientController instance. */
    @BStaticMethod
    Object getActivityClientController();

    /** Sets a custom ActivityClientController implementation. */
    @BStaticMethod
    Object setActivityClientController(Object iInterface);

    /**
     * Reflection mirror for {@code android.app.ActivityClient$ActivityClientControllerSingleton}.
     * Provides access to the known instance of the controller.
     */
    @BClassName("android.app.ActivityClient$ActivityClientControllerSingleton")
    interface ActivityClientControllerSingleton {
        /** Returns the known IInterface instance of the controller. */
        @BField
        IInterface mKnownInstance();
    }
}
