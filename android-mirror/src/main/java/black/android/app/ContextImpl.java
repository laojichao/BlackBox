package black.android.app;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.ContextImpl}.
 * Provides access to hidden fields and methods of the internal Context
 * implementation, including the package info, content resolver, and
 * receiver-restricted context.
 */
@BClassName("android.app.ContextImpl")
public interface ContextImpl {
    /** Returns the hidden {@code mBasePackageName} field. */
    @BField
    String mBasePackageName();

    /** Returns the hidden {@code mContentResolver} field. */
    @BField
    ContentResolver mContentResolver();

    /** Returns the hidden {@code mPackageInfo} field (LoadedApk instance). */
    @BField
    Object mPackageInfo();

    /** Returns the hidden {@code mPackageManager} field. */
    @BField
    PackageManager mPackageManager();

    /** Creates a new application context via the hidden static factory method. */
    @BStaticMethod
    Object createAppContext();

    /** Returns a restricted context suitable for receiving broadcasts. */
    @BMethod
    Context getReceiverRestrictedContext();

    /** Sets the outer (wrapping) context for this ContextImpl. */
    @BMethod
    void setOuterContext(Context Context0);

    /** Returns the AttributionSource for this context (API 31+). */
    @BMethod
    Object getAttributionSource();
}
