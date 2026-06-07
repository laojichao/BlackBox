package black.android.rms.resource;

import java.util.List;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.rms.resource.ReceiverResource for Nougat (API 24-25).
 * Provides access to the broadcast receiver whitelist as a List.
 */
@BClassName("android.rms.resource.ReceiverResource")
public interface ReceiverResourceN {
    /** Whitelisted broadcast receiver package names. */
    @BField
    List<String> mWhiteList();
}
