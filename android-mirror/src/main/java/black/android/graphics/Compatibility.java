package black.android.graphics;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.graphics.Compatibility.
 * Provides access to the setTargetSdkVersion method for graphics compatibility.
 */
@BClassName("android.graphics.Compatibility")
public interface Compatibility {
    /**
     * Set the target SDK version for graphics compatibility decisions.
     */
    @BStaticMethod
    void setTargetSdkVersion(int targetSdkVersion);
}
