package black.android.os;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.UserHandle static methods.
 * Provides access to the current user ID.
 */
@BClassName("android.os.UserHandle")
public interface UserHandle {
    /**
     * Return the user ID of the current process.
     */
    @BStaticMethod
    Integer myUserId();
}
