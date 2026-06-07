package black.android.app;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Reflection mirror for {@code android.app.LoadedApk} (Huawei variant).
 * Exposes the Huawei-specific {@code mReceiverResource} field not present in AOSP.
 */
@BClassName("android.app.LoadedApk")
public interface LoadedApkHuaWei {
    /** Huawei-specific receiver resource object used for broadcast dispatch. */
    @BField
    Object mReceiverResource();
}
