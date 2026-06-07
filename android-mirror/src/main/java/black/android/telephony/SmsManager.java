package black.android.telephony;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.telephony.SmsManager methods.
 * Provides access to the auto-persisting flag for SMS storage.
 */
@BClassName("android.telephony.SmsManager")
public interface SmsManager {
    /** Check whether sent SMS messages are automatically persisted. */
    @BMethod
    Boolean getAutoPersisting();

    /** Set whether sent SMS messages should be automatically persisted. */
    @BMethod
    void setAutoPersisting(boolean boolean0);
}
