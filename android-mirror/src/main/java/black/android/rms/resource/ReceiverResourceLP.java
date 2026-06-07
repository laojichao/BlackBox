package black.android.rms.resource;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.rms.resource.ReceiverResource for Lollipop (API 21-22).
 * Provides access to the internal resource configuration object.
 */
@BClassName("android.rms.resource.ReceiverResource")
public interface ReceiverResourceLP {
    /** The internal resource configuration for broadcast receiver management. */
    @BField
    Object mResourceConfig();
}
