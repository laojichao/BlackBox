package top.niunaijun.blackbox.fake.service;

import java.lang.reflect.Method;
import java.util.List;

import black.com.android.internal.net.BRVpnConfig;
import black.com.android.internal.net.VpnConfigContext;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.proxy.ProxyVpnService;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Common VPN method hooks shared across VPN-related proxies.
 * <p>
 * Provides hooks for VPN authorization, preparation, and establishment operations.
 * Replaces application package names in VPN requests and ensures that VPN
 * configurations include the host package in their allowed/disallowed application lists.
 */
public class VpnCommonProxy {
    /**
     * Hook that replaces the package name in VPN package authorization requests.
     */
    @ProxyMethod("setVpnPackageAuthorization")
    public static class setVpnPackageAuthorization extends MethodHook {

        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return the result of the delegated method call with replaced package name
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that replaces the package name in VPN preparation requests.
     */
    @ProxyMethod("prepareVpn")
    public static class PrepareVpn extends MethodHook {

        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return the result of the delegated method call with replaced package name
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts VPN establishment, replacing the VPN user with the proxy
     * service class and ensuring the host package is included in application lists.
     */
    @ProxyMethod("establishVpn")
    public static class establishVpn extends MethodHook {

        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments (first element is VpnConfig)
         * @return the result of the delegated method call with modified VPN config
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            VpnConfigContext vpnConfigContext = BRVpnConfig.get(args[0]);
            vpnConfigContext._set_user(ProxyVpnService.class.getName());

            handlePackage(vpnConfigContext.allowedApplications());
            handlePackage(vpnConfigContext.disallowedApplications());
            return method.invoke(who, args);
        }

        /**
         * Adds the host package to the application list if the virtual app package is present.
         *
         * @param applications the list of allowed or disallowed VPN application packages
         */
        private void handlePackage(List<String> applications) {
            if (applications == null)
                return;
            if (applications.contains(BActivityThread.getAppPackageName())) {
                applications.add(BlackBoxCore.getHostPkg());
            }
        }
    }

}
