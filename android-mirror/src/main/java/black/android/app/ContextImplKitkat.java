package black.android.app;

import java.io.File;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Reflection mirror for {@code android.app.ContextImpl} (KitKat / API 19+).
 * Provides access to hidden fields for multi-user external storage directories,
 * display adjustments, and the operation package name introduced in KitKat.
 */
@BClassName("android.app.ContextImpl")
public interface ContextImplKitkat {
    /** Returns the hidden {@code mDisplayAdjustments} field for display scaling. */
    @BField
    Object mDisplayAdjustments();

    /** Returns the hidden {@code mExternalCacheDirs} array for multi-user external cache paths. */
    @BField
    File[] mExternalCacheDirs();

    /** Returns the hidden {@code mExternalFilesDirs} array for multi-user external file paths. */
    @BField
    File[] mExternalFilesDirs();

    /** Returns the hidden {@code mOpPackageName} field used for app-ops enforcement. */
    @BField
    String mOpPackageName();
}
