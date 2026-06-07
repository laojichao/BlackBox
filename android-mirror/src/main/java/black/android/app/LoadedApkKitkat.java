package black.android.app;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Reflection mirror for {@code android.app.LoadedApk} (KitKat variant).
 * Exposes the {@code mDisplayAdjustments} field present in KitKat.
 */
@BClassName("android.app.LoadedApk")
public interface LoadedApkKitkat {
    /** The DisplayAdjustments object for this loaded APK (KitKat). */
    @BField
    Object mDisplayAdjustments();
}
