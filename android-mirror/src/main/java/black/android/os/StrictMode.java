package black.android.os;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.os.StrictMode fields and methods.
 * Used to suppress file URI exposure crashes at runtime.
 */
@BClassName("android.os.StrictMode")
public interface StrictMode {
    /**
     * Detection flag for VM file URI exposure violations.
     */
    @BStaticField
    int DETECT_VM_FILE_URI_EXPOSURE();

    /**
     * Penalty flag for dying on file URI exposure.
     */
    @BStaticField
    int PENALTY_DEATH_ON_FILE_URI_EXPOSURE();

    /**
     * The current VM policy bitmask.
     */
    @BStaticField
    int sVmPolicyMask();

    /**
     * Disable the death-on-file-URI-exposure penalty.
     */
    @BStaticMethod
    void disableDeathOnFileUriExposure();
}
