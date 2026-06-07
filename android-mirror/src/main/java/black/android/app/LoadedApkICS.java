package black.android.app;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Reflection mirror for {@code android.app.LoadedApk} (ICS variant).
 * Exposes the {@code mCompatibilityInfo} field present in Ice Cream Sandwich.
 */
@BClassName("android.app.LoadedApk")
public interface LoadedApkICS {
    /** The CompatibilityInfo object for this loaded APK (ICS). */
    @BField
    Object mCompatibilityInfo();
}
