package black.android.rms;

import java.util.Map;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.rms.HwSysResImpl for Huawei devices (Pie / API 28).
 * Provides access to the internal whitelist map for resource management.
 */
@BClassName("android.rms.HwSysResImpl")
public interface HwSysResImplP {
    /** Map of resource type IDs to their whitelisted package name lists. */
    @BField
    Map<Integer, java.util.ArrayList<String>> mWhiteListMap();
}
