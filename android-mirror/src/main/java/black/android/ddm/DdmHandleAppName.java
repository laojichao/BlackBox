package black.android.ddm;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.ddm.DdmHandleAppName.
 * Provides access to the hidden setAppName method for DDMS app name reporting.
 */
@BClassName("android.ddm.DdmHandleAppName")
public interface DdmHandleAppName {
    /**
     * Set the application name reported to DDMS debugger.
     */
    @BStaticMethod
    void setAppName(String String0, int i);
}
