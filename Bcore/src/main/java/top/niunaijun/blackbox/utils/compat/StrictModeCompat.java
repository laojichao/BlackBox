package top.niunaijun.blackbox.utils.compat;

import black.android.os.BRStrictMode;

/**
 * Compatibility wrapper for {@link android.os.StrictMode} file URI exposure policies.
 * <p>
 * Provides constants and a method to disable the {@code StrictMode} death penalty for
 * file URI exposure, which is enforced by default on Android 7.0 (Nougat / API 24) and
 * above. On older versions where the relevant constants or methods do not exist, this
 * class uses hardcoded fallback values and direct VM policy mask manipulation.
 */
public class StrictModeCompat {
    /** StrictMode VM policy bit for detecting file URI exposure violations. */
    public static int DETECT_VM_FILE_URI_EXPOSURE = BRStrictMode.get().DETECT_VM_FILE_URI_EXPOSURE() == null ?
            (0x20 << 8) : BRStrictMode.get().DETECT_VM_FILE_URI_EXPOSURE();

    /** StrictMode VM policy bit for penalizing file URI exposure with process death. */
    public static int PENALTY_DEATH_ON_FILE_URI_EXPOSURE = BRStrictMode.get().PENALTY_DEATH_ON_FILE_URI_EXPOSURE() == null ?
            (0x04 << 24) : BRStrictMode.get().PENALTY_DEATH_ON_FILE_URI_EXPOSURE();

    /**
     * Disables the StrictMode death penalty for file URI exposure.
     * <p>
     * First attempts to use the native {@code disableDeathOnFileUriExposure()} method.
     * If that fails (e.g. on devices where the method is not available), falls back to
     * directly clearing the relevant bits from the VM policy mask via reflection.
     *
     * @return true if the penalty was successfully disabled, false otherwise
     */
    public static boolean disableDeathOnFileUriExposure(){
        try {
            BRStrictMode.get().disableDeathOnFileUriExposure();
            return true;
        } catch (Throwable e) {
            try {
                int sVmPolicyMask = BRStrictMode.get().sVmPolicyMask();
                sVmPolicyMask &= ~(DETECT_VM_FILE_URI_EXPOSURE | PENALTY_DEATH_ON_FILE_URI_EXPOSURE);
                BRStrictMode.get()._set_sVmPolicyMask(sVmPolicyMask);
                return true;
            } catch (Throwable e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }
}
