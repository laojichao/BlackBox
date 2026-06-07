package black.android.security.net.config;

import android.content.Context;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.security.net.config.NetworkSecurityConfigProvider.
 * Provides access to the install method for network security configuration.
 */
@BClassName("android.security.net.config.NetworkSecurityConfigProvider")
public interface NetworkSecurityConfigProvider {
    /**
     * Install the network security config provider for the given context.
     */
    @BStaticMethod
    void install(Context Context0);
}
