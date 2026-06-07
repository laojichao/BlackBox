package black.android.view;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.view.DisplayAdjustments.
 * Provides access to the internal setCompatibilityInfo method.
 */
@BClassName("android.view.DisplayAdjustments")
public interface DisplayAdjustments {
    /** Set the compatibility info for display adjustments. */
    @BMethod
    void setCompatibilityInfo();
}
