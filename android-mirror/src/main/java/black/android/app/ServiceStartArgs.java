package black.android.app;

import android.content.Intent;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Reflection mirror for {@code android.app.ServiceStartArgs}.
 * Represents the arguments passed when starting or restarting a service.
 */
@BClassName("android.app.ServiceStartArgs")
public interface ServiceStartArgs {
    /** Creates a new ServiceStartArgs with the given parameters. */
    @BConstructor
    ServiceStartArgs _new(boolean boolean0, int int1, int int2, Intent Intent3);

    /** The intent passed to onStartCommand. */
    @BField
    Intent args();

    /** The flags passed to onStartCommand. */
    @BField
    int flags();

    /** The unique start ID for this service invocation. */
    @BField
    int startId();

    /** Whether the task that started this service has been removed. */
    @BField
    boolean taskRemoved();
}
