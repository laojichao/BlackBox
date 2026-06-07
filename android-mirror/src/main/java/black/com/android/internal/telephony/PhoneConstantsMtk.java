package black.com.android.internal.telephony;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden com.android.internal.telephony.PhoneConstants for MediaTek devices.
 * Provides access to MediaTek-specific dual SIM constants.
 */
@BClassName("com.android.internal.telephony.PhoneConstants")
public interface PhoneConstantsMtk {
    /** The number of Gemini SIM slots on MediaTek chipsets. */
    @BStaticField
    int GEMINI_SIM_NUM();
}
