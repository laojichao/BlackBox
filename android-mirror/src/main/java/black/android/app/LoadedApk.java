package black.android.app;

import android.app.Application;
import android.app.IServiceConnection;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.IInterface;

import java.io.File;
import java.lang.ref.WeakReference;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Reflection mirror for {@code android.app.LoadedApk}.
 * Provides access to hidden fields and methods of the application's loaded APK,
 * including class loader, application info, service dispatchers, and receiver dispatchers.
 */
@BClassName("android.app.LoadedApk")
public interface LoadedApk {
    /** The Application instance for this loaded APK. */
    @BField
    Application mApplication();

    /** The ApplicationInfo metadata for this loaded APK. */
    @BField
    ApplicationInfo mApplicationInfo();

    /** The credential-protected data directory file. */
    @BField
    File mCredentialProtectedDataDirFile();

    /** The application data directory path. */
    @BField
    String mDataDir();

    /** The application data directory file. */
    @BField
    File mDataDirFile();

    /** The device-protected data directory file. */
    @BField
    File mDeviceProtectedDataDirFile();

    /** The native library directory path. */
    @BField
    String mLibDir();

    /** Whether a security violation was detected for this APK. */
    @BField
    boolean mSecurityViolation();

    /** The package name associated with this loaded APK. */
    @BField
    boolean mPackageName();

    /** Returns the Resources instance for this loaded APK. */
    @BMethod
    Object getResources();

    /** Returns the IServiceConnection for the given ServiceConnection, removing it from tracking. */
    @BMethod
    IServiceConnection forgetServiceDispatcher(Context Context0, ServiceConnection ServiceConnection1);

    /** Returns the ClassLoader used to load classes from this APK. */
    @BMethod
    ClassLoader getClassLoader();

    /** Returns the IServiceConnection proxy for the given ServiceConnection and context. */
    @BMethod
    IServiceConnection getServiceDispatcher(ServiceConnection ServiceConnection0, Context Context1, Handler Handler2, int int3);

    /** Creates or retrieves the Application object for this APK. */
    @BMethod
    Application makeApplication(boolean boolean0, Instrumentation Instrumentation1);

    /**
     * Reflection mirror for {@code android.app.LoadedApk.ServiceDispatcher}.
     * Manages the connection between a ServiceConnection and its IBinder.
     */
    @BClassName("android.app.LoadedApk$ServiceDispatcher")
    interface ServiceDispatcher {
        /** The underlying ServiceConnection being dispatched to. */
        @BField
        ServiceConnection mConnection();

        /** The Context associated with this service dispatcher. */
        @BField
        Context mContext();

        /**
         * Reflection mirror for {@code android.app.LoadedApk.ServiceDispatcher.InnerConnection}.
         * A lightweight IBinder wrapper holding a weak reference to the dispatcher.
         */
        @BClassName("android.app.LoadedApk$ServiceDispatcher$InnerConnection")
        interface InnerConnection {
            /** Weak reference to the parent ServiceDispatcher. */
            @BField
            WeakReference<?> mDispatcher();
        }
    }

    /**
     * Reflection mirror for {@code android.app.LoadedApk.ReceiverDispatcher}.
     * Manages the connection between a BroadcastReceiver and its IIntentReceiver.
     */
    @BClassName("android.app.LoadedApk$ReceiverDispatcher")
    interface ReceiverDispatcher {
        /** The IIntentReceiver used for IPC with the system. */
        @BField
        IIntentReceiver mIIntentReceiver();

        /** The BroadcastReceiver being dispatched to. */
        @BField
        BroadcastReceiver mReceiver();

        /** Returns the IIntentReceiver interface for this receiver dispatcher. */
        @BMethod
        IInterface getIIntentReceiver();

        /**
         * Reflection mirror for {@code android.app.LoadedApk.ReceiverDispatcher.InnerReceiver}.
         * A lightweight IBinder wrapper holding a weak reference to the dispatcher.
         */
        @BClassName("android.app.LoadedApk$ReceiverDispatcher$InnerReceiver")
        interface InnerReceiver {
            /** Weak reference to the parent ReceiverDispatcher. */
            @BField
            WeakReference<?> mDispatcher();
        }
    }
}
