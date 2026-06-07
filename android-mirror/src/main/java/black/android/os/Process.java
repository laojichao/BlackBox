package black.android.os;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.Process methods.
 * Provides access to the hidden setArgV0 method for renaming the process.
 */
@BClassName("android.os.Process")
public interface Process {
    /**
     * Change the process name shown in tools like ps/top.
     */
    @BStaticMethod
    void setArgV0(String String0);
}
