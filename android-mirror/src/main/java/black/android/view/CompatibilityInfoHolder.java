package black.android.view;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.view.CompatibilityInfoHolder.
 * Provides access to the internal set method for compatibility info.
 */
@BClassName("android.view.CompatibilityInfoHolder")
public interface CompatibilityInfoHolder {
    /** Reset or set the compatibility info holder. */
    @BMethod
    void set();
}
