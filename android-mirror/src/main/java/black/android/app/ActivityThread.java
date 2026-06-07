package black.android.app;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;

import java.util.List;
import java.util.Map;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BParamClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.ActivityThread}.
 * Provides access to the core application thread that manages the lifecycle
 * of activities, services, content providers, and broadcast receivers.
 * Includes inner class mirrors for AppBindData, CreateServiceData, H handler,
 * ActivityClientRecord, ProviderClientRecord, and ProviderKey.
 */
@BClassName("android.app.ActivityThread")
public interface ActivityThread {
    /** Returns the hidden {@code mAppThread} field (ApplicationThread binder). */
    @BField
    Object mAppThread();

    /** Returns the hidden static {@code sPackageManager} field for IPC with the package manager. */
    @BStaticField
    IInterface sPackageManager();

    /** Returns the hidden static {@code sPermissionManager} field for permission queries. */
    @BStaticField
    IInterface sPermissionManager();

    /** Returns the hidden {@code mActivities} map of activity tokens to client records. */
    @BField
    Map<IBinder, Object> mActivities();

    /** Returns the hidden {@code mBoundApplication} field with the app binding data. */
    @BField
    Object mBoundApplication();

    /** Returns the hidden {@code mH} internal handler. */
    @BField
    Handler mH();

    /** Returns the hidden {@code mInitialApplication} field. */
    @BField
    Application mInitialApplication();

    /** Returns the hidden {@code mInstrumentation} field. */
    @BField
    Instrumentation mInstrumentation();

    /** Returns the hidden {@code mPackages} map of package names to LoadedApk weak references. */
    @BField
    Map<String, java.lang.ref.WeakReference<?>> mPackages();

    /** Returns the hidden {@code mProviderMap} field mapping provider keys to records. */
    @BField
    Map<?, ?> mProviderMap();

    /** Returns the hidden {@code mLocalProvidersByName} map for local content providers. */
    @BField
    Map<?, ?> mLocalProvidersByName();

    /** Returns the current ActivityThread instance (static accessor). */
    @BStaticMethod
    Object currentActivityThread();

    /** Returns the current Application instance. */
    @BStaticMethod
    Application currentApplication();

    /** Returns the current process package name. */
    @BStaticMethod
    String currentPackageName();

    /** Returns the IApplicationThread binder for IPC callbacks. */
    @BMethod
    IBinder getApplicationThread();

    /** Returns the Handler associated with this activity thread. */
    @BMethod
    Handler getHandler();

    /** Returns the process name for this activity thread. */
    @BMethod
    String getProcessName();

    /** Returns the system context for this activity thread. */
    @BMethod
    Object getSystemContext();

    /** Returns the ActivityClientController for the given activity token. */
    @BMethod
    Object getActivityClient(IBinder token);

    /** Returns the LaunchActivityItem or similar transaction for the given token. */
    @BMethod
    Object getLaunchingActivity(IBinder token);

    /** Returns a LoadedApk for the given ApplicationInfo, compatibility info, and flags. */
    @BMethod
    Object getPackageInfo(ApplicationInfo ai, @BParamClassName("android.content.res.CompatibilityInfo") Object compatInfo,
                          int flags);

    /** Delivers new intents to the activity identified by the given binder token. */
    @BMethod
    void performNewIntents(IBinder IBinder0, List List1);

    /** Sends an activity result to the specified activity. */
    @BMethod
    void sendActivityResult(IBinder IBinder0, String String1, int int2, int int3, Intent Intent4);

    /**
     * Reflection mirror for {@code android.app.ActivityThread$CreateServiceData}.
     * Data structure holding parameters for creating a service instance.
     */
    @BClassName("android.app.ActivityThread$CreateServiceData")
    interface CreateServiceData {
        /** Returns the compatibility info for the service. */
        @BField
        Object compatInfo();

        /** Returns the ServiceInfo metadata for the service to be created. */
        @BField
        ServiceInfo info();

        /** Returns the Intent that triggered the service creation. */
        @BField
        Intent intent();

        /** Returns the IBinder token identifying the service. */
        @BField
        IBinder token();
    }

    /**
     * Reflection mirror for {@code android.app.ActivityThread$H}.
     * Exposes hidden message constants used by the internal Handler.
     */
    @BClassName("android.app.ActivityThread$H")
    interface H {
        /** Message code for creating a service. */
        @BStaticField
        int CREATE_SERVICE();

        /** Message code for executing a ClientTransaction. */
        @BStaticField
        int EXECUTE_TRANSACTION();

        /** Message code for launching an activity (pre-P). */
        @BStaticField
        int LAUNCH_ACTIVITY();

        /** Message code for scheduling a process crash. */
        @BStaticField
        int SCHEDULE_CRASH();
    }

    /**
     * Reflection mirror for {@code android.app.ActivityThread$AppBindData}.
     * Data structure populated when the application is bound to the process.
     */
    @BClassName("android.app.ActivityThread$AppBindData")
    interface AppBindData {
        /** Returns the ApplicationInfo for the bound application. */
        @BField
        ApplicationInfo appInfo();

        /** Returns the LoadedApk info for the application. */
        @BField
        Object info();

        /** Returns the ComponentName of the instrumentation, if any. */
        @BField
        ComponentName instrumentationName();

        /** Returns the process name for this binding. */
        @BField
        String processName();

        /** Returns the list of content providers to install. */
        @BField
        List<android.content.pm.ProviderInfo> providers();
    }

    /**
     * Reflection mirror for {@code android.app.ActivityThread$ProviderKey} (JB MR1+).
     * Composite key used to look up content providers by name and user ID.
     */
    @BClassName("android.app.ActivityThread$ProviderKey")
    interface ProviderKeyJBMR1 {
        /** Creates a new ProviderKey with the given authority name and user ID. */
        @BConstructor
        ProviderKeyJBMR1 _new(String String0, int int1);
    }

    /**
     * Reflection mirror for {@code android.app.ActivityThread$ProviderClientRecord} (JB).
     * Client-side record for a published content provider.
     */
    @BClassName("android.app.ActivityThread$ProviderClientRecord")
    interface ProviderClientRecordJB {
        /** Returns the IContentProviderHolder for this record. */
        @BField
        Object mHolder();

        /** Returns the IContentProvider interface for this provider. */
        @BField
        IInterface mProvider();
    }

    /**
     * Reflection mirror for {@code android.app.ActivityThread$ProviderClientRecord} (P+).
     * Client-side record variant supporting multiple provider names.
     */
    @BClassName("android.app.ActivityThread$ProviderClientRecord")
    interface ProviderClientRecordP {
        /** Creates a new ProviderClientRecord instance. */
        @BConstructor
        ProviderClientRecordP _new();

        /** Returns the array of names associated with this provider. */
        @BField
        String[] mNames();

        /** Returns the IContentProvider interface for this provider. */
        @BField
        IInterface mProvider();
    }

    /**
     * Reflection mirror for {@code android.app.ActivityThread$ProviderClientRecord}.
     * Client-side record for a published content provider with a single name.
     */
    @BClassName("android.app.ActivityThread$ProviderClientRecord")
    interface ProviderClientRecord {
        /** Creates a new ProviderClientRecord instance. */
        @BConstructor
        ProviderClientRecord _new();

        /** Returns the provider authority name. */
        @BField
        String mName();

        /** Returns the IContentProvider interface for this provider. */
        @BField
        IInterface mProvider();
    }

    /**
     * Reflection mirror for {@code android.app.ActivityThread$ActivityClientRecord}.
     * Client-side record holding the state and metadata of a running activity.
     */
    @BClassName("android.app.ActivityThread$ActivityClientRecord")
    interface ActivityClientRecord {
        /** Returns the Activity instance associated with this record. */
        @BField
        Activity activity();

        /** Returns the ActivityInfo metadata for this activity. */
        @BField
        ActivityInfo activityInfo();

        /** Returns the Intent that launched this activity. */
        @BField
        Intent intent();

        /** Returns whether this activity is currently the top resumed activity. */
        @BField
        Boolean isTopResumedActivity();

        /** Returns the IBinder token identifying this activity. */
        @BField
        IBinder token();

        /** Returns the LoadedApk package info for this activity. */
        @BField
        Object packageInfo();
    }
}
