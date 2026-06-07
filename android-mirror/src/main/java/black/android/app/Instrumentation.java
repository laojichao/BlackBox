package black.android.app;

import android.app.Instrumentation.ActivityResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Reflection mirror for {@code android.app.Instrumentation}.
 * Provides access to hidden instrumentation methods used for intercepting
 * activity starts within the application process.
 */
@BClassName("android.app.Instrumentation")
public interface Instrumentation {
    /** Executes starting an activity, providing control over caller context, token, and options. */
    @BMethod
    ActivityResult execStartActivity(Context Context0, IBinder IBinder1, IBinder IBinder2, Activity Activity3, Intent Intent4, int int5, Bundle Bundle6);
}
