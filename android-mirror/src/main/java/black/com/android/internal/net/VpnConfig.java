package black.com.android.internal.net;

import java.util.List;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden com.android.internal.net.VpnConfig fields.
 * Provides access to VPN configuration including allowed/disallowed apps.
 */
@BClassName("com.android.internal.net.VpnConfig")
public interface VpnConfig {
    /** The VPN user identifier. */
    @BField
    String user();

    /** List of applications disallowed from using this VPN. */
    @BField
    List<String> disallowedApplications();

    /** List of applications allowed to use this VPN. */
    @BField
    List<String> allowedApplications();
}
