package black.android.app;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.ApplicationThreadNative}.
 * Provides access to the static {@code asInterface} method that converts
 * an IBinder to an IApplicationThread proxy for IPC communication.
 */
@BClassName("android.app.ApplicationThreadNative")
public interface ApplicationThreadNative {
    /** Converts the given IBinder to an IApplicationThread interface proxy. */
    @BStaticMethod
    IInterface asInterface(IBinder IBinder0);
}
