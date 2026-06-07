package black.android.content.pm;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.content.pm.ApplicationInfo fields/methods for Pie (API 28).
 * Provides access to split class loader names and hidden API enforcement policy.
 */
@BClassName("android.content.pm.ApplicationInfo")
public interface ApplicationInfoP {
    /** Class loader names for each split APK. */
    @BField
    String[] splitClassLoaderNames();

    /** Sets the hidden API enforcement policy for this application. */
    @BMethod
    void setHiddenApiEnforcementPolicy(int int0);
}
