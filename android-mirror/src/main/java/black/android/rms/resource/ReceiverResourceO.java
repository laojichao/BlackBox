package black.android.rms.resource;

import java.util.Map;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.rms.resource.ReceiverResource for Oreo (API 26-27).
 * Provides access to the broadcast receiver whitelist map.
 */
@BClassName("android.rms.resource.ReceiverResource")
public interface ReceiverResourceO {
    /** Map of resource type IDs to their whitelisted receiver package name lists. */
    @BField
    Map<Integer, java.util.List<String>> mWhiteListMap();
}
