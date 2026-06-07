package black.android.os;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.Message static methods.
 * Provides access to the internal recycle-check mechanism.
 */
@BClassName("android.os.Message")
public interface Message {
    /**
     * Update and check the recycle state for the given pool size.
     */
    @BStaticMethod
    void updateCheckRecycle(int int0);
}
