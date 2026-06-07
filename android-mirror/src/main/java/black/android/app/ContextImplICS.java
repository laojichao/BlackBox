package black.android.app;

import java.io.File;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Reflection mirror for {@code android.app.ContextImpl} (ICS / API 14+).
 * Provides access to hidden fields for external cache and files directories
 * introduced in Ice Cream Sandwich.
 */
@BClassName("android.app.ContextImpl")
public interface ContextImplICS {
    /** Returns the hidden {@code mExternalCacheDir} field pointing to the app's external cache directory. */
    @BField
    File mExternalCacheDir();

    /** Returns the hidden {@code mExternalFilesDir} field pointing to the app's external files directory. */
    @BField
    File mExternalFilesDir();
}
