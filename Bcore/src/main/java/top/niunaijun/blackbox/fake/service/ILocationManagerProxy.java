package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.location.LocationManager;
import android.os.IInterface;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import black.android.location.BRILocationListener;
import black.android.location.BRILocationManagerStub;
import black.android.location.provider.BRProviderProperties;
import black.android.location.provider.ProviderProperties;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.entity.location.BLocation;
import top.niunaijun.blackbox.fake.frameworks.BLocationManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Proxy for the Android Location Manager system service (ILocationManager).
 * Intercepts location-related operations to support fake location injection
 * within the virtual environment. When fake location is enabled, this proxy
 * returns spoofed GPS coordinates, manages location update listeners through
 * the virtual environment's location manager, and overrides provider
 * properties to hide network/cell requirements.
 *
 * @author Milk
 */
public class ILocationManagerProxy extends BinderInvocationStub {
    /** Tag used for logging within this proxy. */
    public static final String TAG = "ILocationManagerProxy";

    /**
     * Constructs a new proxy by obtaining the Location Manager binder service.
     */
    public ILocationManagerProxy() {
        super(BRServiceManager.get().getService(Context.LOCATION_SERVICE));
    }

    /**
     * Returns the ILocationManager interface instance from the system service.
     *
     * @return the original ILocationManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRILocationManagerStub.get().asInterface(BRServiceManager.get().getService(Context.LOCATION_SERVICE));
    }

    /**
     * Replaces the system Location Manager service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Checks if the environment has been corrupted by another proxy.
     *
     * @return always returns false
     */
    @Override
    public boolean isBadEnv() {
        return false;
    }

    /**
     * Intercepts all method invocations to replace the first package name
     * argument before delegating to the original method.
     *
     * @param proxy the proxy object the method was invoked on
     * @param method the method being invoked
     * @param args the method arguments; the first package name is replaced
     * @return the result of the original method invocation
     * @throws Throwable if the underlying method call fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        Log.d(TAG, "call: " + method.getName());
        MethodParameterUtils.replaceFirstAppPkg(args);
        return super.invoke(proxy, method, args);
    }

    /**
     * Hook that intercepts {@code registerGnssStatusCallback} to suppress
     * GNSS status callback registration within the virtual environment.
     */
    @ProxyMethod("registerGnssStatusCallback")
    public static class RegisterGnssStatusCallback extends MethodHook {

        /**
         * Suppresses the GNSS status callback registration by returning true immediately.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns true
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            // todo
            return true;
        }
    }

    /**
     * Hook that intercepts {@code getLastLocation} to return a fake location
     * when virtual location injection is enabled.
     */
    @ProxyMethod("getLastLocation")
    public static class GetLastLocation extends MethodHook {

        /**
         * Returns the virtual environment's fake location if enabled, otherwise
         * delegates to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return a system Location object from the virtual or real location manager
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BLocationManager.isFakeLocationEnable()) {
                return BLocationManager.get().getLocation(BActivityThread.getUserId(), BActivityThread.getAppPackageName()).convert2SystemLocation();
            }
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code getLastKnownLocation} to return a fake location
     * when virtual location injection is enabled.
     */
    @ProxyMethod("getLastKnownLocation")
    public static class GetLastKnownLocation extends MethodHook {

        /**
         * Returns the virtual environment's fake last known location if enabled,
         * otherwise delegates to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return a system Location object from the virtual or real location manager
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BLocationManager.isFakeLocationEnable()) {
                return BLocationManager.get().getLocation(BActivityThread.getUserId(), BActivityThread.getAppPackageName()).convert2SystemLocation();
            }
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code requestLocationUpdates} to register the listener
     * with the virtual environment's location manager when fake location is enabled.
     */
    @ProxyMethod("requestLocationUpdates")
    public static class RequestLocationUpdates extends MethodHook {

        /**
         * Registers the location listener with the virtual location manager if fake
         * location is enabled; otherwise delegates to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[1] is the location listener
         * @return 0 if intercepted by the virtual manager, otherwise the original result
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BLocationManager.isFakeLocationEnable()) {
                if (args[1] instanceof IInterface) {
                    IInterface listener = (IInterface) args[1];
                    BLocationManager.get().requestLocationUpdates(listener.asBinder());
                    return 0;
                }
            }
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code removeUpdates} to unregister the location listener
     * from the virtual environment's location manager.
     */
    @ProxyMethod("removeUpdates")
    public static class RemoveUpdates extends MethodHook {

        /**
         * Removes the location listener from the virtual location manager if it
         * implements IInterface; otherwise delegates to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is the location listener
         * @return 0 if intercepted, otherwise the original result
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args[0] instanceof IInterface) {
                IInterface listener = (IInterface) args[0];
                BLocationManager.get().removeUpdates(listener.asBinder());
                return 0;
            }
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code getProviderProperties} to modify provider
     * characteristics when fake location is enabled, hiding network and cell
     * requirements as needed.
     */
    @ProxyMethod("getProviderProperties")
    public static class GetProviderProperties extends MethodHook {

        /**
         * Modifies provider properties to hide network/cell requirements when
         * fake location is enabled, then delegates to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object providerProperties = method.invoke(who, args);
            if (BLocationManager.isFakeLocationEnable()) {
                BRProviderProperties.get(providerProperties)._set_mHasNetworkRequirement(false);
                if (BLocationManager.get().getCell(BActivityThread.getUserId(), BActivityThread.getAppPackageName()) == null) {
                    BRProviderProperties.get(providerProperties)._set_mHasCellRequirement(false);
                }
            }
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code removeGpsStatusListener} to suppress
     * GPS status listener removal within the virtual environment.
     */
    @ProxyMethod("removeGpsStatusListener")
    public static class RemoveGpsStatusListener extends MethodHook {

        /**
         * Suppresses the GPS status listener removal by returning 0 immediately.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            // todo
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code getBestProvider} to return GPS_PROVIDER
     * when fake location is enabled, ensuring consistent provider selection.
     */
    @ProxyMethod("getBestProvider")
    public static class GetBestProvider extends MethodHook {

        /**
         * Returns GPS_PROVIDER when fake location is enabled; otherwise delegates
         * to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return the best location provider name
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BLocationManager.isFakeLocationEnable()) {
                return LocationManager.GPS_PROVIDER;
            }
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code getAllProviders} to return a fixed list
     * of GPS and Network providers for the virtual environment.
     */
    @ProxyMethod("getAllProviders")
    public static class GetAllProviders extends MethodHook {

        /**
         * Returns a list containing only GPS and Network providers.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return a list of GPS_PROVIDER and NETWORK_PROVIDER
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return Arrays.asList(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER);
        }
    }

    /**
     * Hook that intercepts {@code isProviderEnabledForUser} to return true
     * only for the GPS provider within the virtual environment.
     */
    @ProxyMethod("isProviderEnabledForUser")
    public static class isProviderEnabledForUser extends MethodHook {

        /**
         * Returns true if the requested provider is GPS, false otherwise.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is the provider name
         * @return true if the provider is GPS_PROVIDER
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String provider = (String) args[0];
            return Objects.equals(provider, LocationManager.GPS_PROVIDER);
        }
    }

    /**
     * Hook that intercepts {@code setExtraLocationControllerPackageEnabled}
     * to suppress extra location controller configuration within the virtual environment.
     */
    @ProxyMethod("setExtraLocationControllerPackageEnabled")
    public static class setExtraLocationControllerPackageEnabled extends MethodHook {

        /**
         * Suppresses the call by returning 0 immediately.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }
}
