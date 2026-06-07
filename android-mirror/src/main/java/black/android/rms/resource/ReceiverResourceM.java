package black.android.rms.resource;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.rms.resource.ReceiverResource for Marshmallow (API 23).
 * Provides access to the broadcast receiver whitelist as a string array.
 */
@BClassName("android.rms.resource.ReceiverResource")
public interface ReceiverResourceM {
    /** Whitelisted broadcast receiver package names. */
    @BField
    String[] mWhiteList();
}
