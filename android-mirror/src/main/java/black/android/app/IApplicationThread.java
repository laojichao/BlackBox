package black.android.app;

import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.IBinder;

import java.util.List;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Reflection mirror for {@code android.app.IApplicationThread}.
 * Provides access to hidden application thread scheduling methods used by the Activity Manager
 * to dispatch lifecycle callbacks to an application process.
 */
@BClassName("android.app.IApplicationThread")
public interface IApplicationThread {
    /** Schedules binding a service with the given intent. */
    @BMethod
    void scheduleBindService(IBinder IBinder0, Intent Intent1, boolean boolean2);

    /** Schedules creation of a service with the given ServiceInfo. */
    @BMethod
    void scheduleCreateService(IBinder IBinder0, ServiceInfo ServiceInfo1);

    /** Schedules delivery of a new intent to the given activity token. */
    @BMethod
    void scheduleNewIntent(List List0, IBinder IBinder1);

    /** Schedules a service start command with start ID, flags, and intent. */
    @BMethod
    void scheduleServiceArgs(IBinder IBinder0, int int1, int int2, Intent Intent3);

    /** Schedules stopping a service identified by the given token. */
    @BMethod
    void scheduleStopService(IBinder IBinder0);

    /** Schedules unbinding a service with the given intent. */
    @BMethod
    void scheduleUnbindService(IBinder IBinder0, Intent Intent1);
}
