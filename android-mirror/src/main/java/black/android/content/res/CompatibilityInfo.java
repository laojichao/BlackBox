package black.android.content.res;

import android.content.pm.ApplicationInfo;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.content.res.CompatibilityInfo.
 * Controls display scaling and compatibility mode for applications.
 */
@BClassName("android.content.res.CompatibilityInfo")
public interface CompatibilityInfo {
    /** Creates a CompatibilityInfo with application info and scaling parameters. */
    @BConstructor
    CompatibilityInfo _new(ApplicationInfo ApplicationInfo0, int int1, int int2, boolean boolean3);

    /** Creates a CompatibilityInfo with an additional density parameter. */
    @BConstructor
    CompatibilityInfo _new(ApplicationInfo ApplicationInfo0, int int1, int int2, boolean boolean3, int int4);

    /** The default compatibility info with no scaling applied. */
    @BStaticField
    Object DEFAULT_COMPATIBILITY_INFO();
}
