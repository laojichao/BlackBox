package top.niunaijun.blackbox.proxy;

import android.net.VpnService;

/**
 * Proxy VpnService stub declared in the host manifest to support VPN functionality
 * for virtual apps. Extends Android's {@link VpnService} to provide a registered
 * VPN service component that can be used by virtual applications requiring VPN
 * connections within the virtual environment.
 *
 * @author BlackBox
 */
public class ProxyVpnService extends VpnService {

}
