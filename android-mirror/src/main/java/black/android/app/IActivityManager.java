package black.android.app;

import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BParamClassName;

/**
 * Reflection mirror for {@code android.app.IActivityManager}.
 * Provides access to hidden methods on the Activity Manager system service.
 */
@BClassName("android.app.IActivityManager")
public interface IActivityManager {
    /** Returns the task ID associated with the given activity token. */
    @BMethod
    Integer getTaskForActivity(IBinder IBinder0, boolean boolean1);

    /** Overrides the pending transition animation for the given activity. */
    @BMethod
    void overridePendingTransition(IBinder IBinder0, String String1, int int2, int int3);

    /** Sets the requested screen orientation for the given activity. */
    @BMethod
    void setRequestedOrientation(IBinder IBinder0, int int1);

    /** Starts multiple activities as a batch operation. */
    @BMethod
    Integer startActivities();

    /** Starts an activity with full control over caller, flags, and options. */
    @BMethod
    Integer startActivity(@BParamClassName("android.app.IApplicationThread") Object caller, String callingPackage,
                          Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
                          int startFlags, @BParamClassName("android.app.ProfilerInfo") Object profilerInfo, Bundle bOptions);

    /**
     * Reflection mirror for {@code android.app.IActivityManager.ContentProviderHolder}
     * with MIUI-specific extra fields.
     */
    @BClassName("android.app.IActivityManager$ContentProviderHolder")
    interface ContentProviderHolderMIUI {
        /** The provider info for this content provider. */
        @BField
        ProviderInfo info();

        /** Whether this holder does not require a release call. */
        @BField
        boolean noReleaseNeeded();

        /** The content provider IInterface instance. */
        @BField
        IInterface provider();

        /** MIUI-specific flag indicating whether to wait for process start. */
        @BField
        boolean waitProcessStart();
    }

    /**
     * Reflection mirror for {@code android.app.IActivityManager.ContentProviderHolder}.
     * Holds a reference to a content provider and its metadata.
     */
    @BClassName("android.app.IActivityManager$ContentProviderHolder")
    interface ContentProviderHolder {
        /** The provider info for this content provider. */
        @BField
        ProviderInfo info();

        /** Whether this holder does not require a release call. */
        @BField
        boolean noReleaseNeeded();

        /** The content provider IInterface instance. */
        @BField
        IInterface provider();
    }
}
