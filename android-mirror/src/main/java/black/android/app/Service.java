package black.android.app;


import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BParamClass;

/**
 * Reflection mirror for {@code android.app.Service}.
 * Provides access to the hidden {@code attach} method used to bind a Service
 * to its host process and ActivityManager.
 */
@BClassName("android.app.Service")
public interface Service {
    /**
     * Attaches this service to its context and hosting thread.
     *
     * @param context         the application context
     * @param thread          the ActivityThread hosting this service
     * @param className       the fully-qualified class name of the service
     * @param token           the IBinder token identifying this service
     * @param application     the parent Application instance
     * @param activityManager the IActivityManager interface
     */
    @BMethod
    void attach(Context context,
                @BParamClass(ActivityThread.class) Object thread, String className, IBinder token,
                Application application, Object activityManager);
}
