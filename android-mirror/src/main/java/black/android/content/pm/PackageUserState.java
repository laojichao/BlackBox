package black.android.content.pm;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;

/**
 * Mirror of hidden android.content.pm.PackageUserState.
 * Represents the per-user state of an installed package.
 */
@BClassName("android.content.pm.PackageUserState")
public interface PackageUserState {
    /** Creates a new default PackageUserState instance. */
    @BConstructor
    PackageUserState _new();
}
