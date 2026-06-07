package black.android.content.pm;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.content.pm.UserInfo.
 * Represents a user profile on a multi-user Android device.
 */
@BClassName("android.content.pm.UserInfo")
public interface UserInfo {
    /** Creates a new UserInfo with the given ID, name, and flags. */
    @BConstructor
    Object _new(int id, String name, int flags);

    /** Flag indicating the primary user of the device. */
    @BStaticField
    int FLAG_PRIMARY();
}
