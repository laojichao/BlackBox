package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import black.android.os.BRServiceManager;
import black.com.android.internal.telephony.BRITelephonyStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.entity.location.BCell;
import top.niunaijun.blackbox.fake.frameworks.BLocationManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.Md5Utils;

/**
 * Proxy for the Android telephony manager system service.
 * <p>
 * Intercepts telephony-related method calls including device ID, IMEI, MEID,
 * subscriber ID, cell location, and network information queries. Returns
 * privacy-safe values derived from the host package name and supports
 * fake location cell data injection.
 */
public class ITelephonyManagerProxy extends BinderInvocationStub {
    /** Logging tag for this proxy class. */
    public static final String TAG = "ITelephonyManagerProxy";

    /**
     * Constructs a new proxy by acquiring the real telephony manager binder service.
     */
    public ITelephonyManagerProxy() {
        super(BRServiceManager.get().getService(Context.TELEPHONY_SERVICE));
    }

    /**
     * Returns the underlying telephony manager service interface.
     *
     * @return the real telephony binder interface
     */
    @Override
    protected Object getWho() {
        IBinder telephony = BRServiceManager.get().getService(Context.TELEPHONY_SERVICE);
        return BRITelephonyStub.get().asInterface(telephony);
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * Checks whether the current environment is invalid for this proxy.
     *
     * @return always {@code false}, indicating the environment is always valid
     */
    @Override
    public boolean isBadEnv() {
        return false;
    }

    /**
     * Hook that returns a hashed device ID based on the host package name.
     */
    @ProxyMethod("getDeviceId")
    public static class GetDeviceId extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return an MD5 hash of the host package name as a fake device ID
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//                MethodParameterUtils.replaceFirstAppPkg(args);
//                return method.invoke(who, args);
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    /**
     * Hook that returns a hashed IMEI based on the host package name.
     */
    @ProxyMethod("getImeiForSlot")
    public static class getImeiForSlot extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return an MD5 hash of the host package name as a fake IMEI
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//                MethodParameterUtils.replaceFirstAppPkg(args);
//                return method.invoke(who, args);
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    /**
     * Hook that returns a hashed MEID based on the host package name.
     */
    @ProxyMethod("getMeidForSlot")
    public static class GetMeidForSlot extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return an MD5 hash of the host package name as a fake MEID
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//                MethodParameterUtils.replaceFirstAppPkg(args);
//                return method.invoke(who, args);
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    /**
     * Hook that always reports user data as enabled.
     */
    @ProxyMethod("isUserDataEnabled")
    public static class IsUserDataEnabled extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return always {@code true}
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }


    /**
     * Hook that returns {@code null} for the line number display to prevent leaking real data.
     */
    @ProxyMethod("getLine1NumberForDisplay")
    public static class getLine1NumberForDisplay extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return always {@code null}
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return null;
        }
    }

    /**
     * Hook that returns a hashed subscriber ID based on the host package name.
     */
    @ProxyMethod("getSubscriberId")
    public static class GetSubscriberId extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return an MD5 hash of the host package name as a fake subscriber ID
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    /**
     * Hook that returns a hashed device ID for feature requests based on the host package name.
     */
    @ProxyMethod("getDeviceIdWithFeature")
    public static class GetDeviceIdWithFeature extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return an MD5 hash of the host package name as a fake device ID
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    /**
     * Hook that intercepts cell location queries, returning fake cell data
     * when virtual location is enabled.
     */
    @ProxyMethod("getCellLocation")
    public static class GetCellLocation extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return fake cell location if available, or the real cell location as fallback
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getCellLocation");
            if (BLocationManager.isFakeLocationEnable()) {
                BCell cell = BLocationManager.get().getCell(BActivityThread.getUserId(), BActivityThread.getAppPackageName());
                if (cell != null) {
                    // TODO Transfer BCell to CdmaCellLocation/GsmCellLocation
                    return null;
                }
            }
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts all cell info queries, returning fake cell data
     * when virtual location is enabled.
     */
    @ProxyMethod("getAllCellInfo")
    public static class GetAllCellInfo extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return a list of fake cell info if available, or the real data as fallback
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BLocationManager.isFakeLocationEnable()) {
                List<BCell> cell = BLocationManager.get().getAllCell(BActivityThread.getUserId(), BActivityThread.getAppPackageName());
                // TODO Transfer BCell to CdmaCellLocation/GsmCellLocation
                return cell;
            }
            try {
                return method.invoke(who, args);
            } catch (Throwable e) {
                return null;
            }
        }
    }

    /**
     * Hook that logs and delegates network operator queries to the real service.
     */
    @ProxyMethod("getNetworkOperator")
    public static class GetNetworkOperator extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return the real network operator string
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getNetworkOperator");
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that safely delegates network type queries, returning 0 on failure.
     */
    @ProxyMethod("getNetworkTypeForSubscriber")
    public static class GetNetworkTypeForSubscriber extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return the network type integer, or 0 on error
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(who, args);
            } catch (Throwable e) {
                return 0;
            }
        }
    }

    /**
     * Hook that intercepts neighboring cell info queries, returning fake data
     * when virtual location is enabled.
     */
    @ProxyMethod("getNeighboringCellInfo")
    public static class GetNeighboringCellInfo extends MethodHook {
        /**
         * @param who    the target object
         * @param method the original method
         * @param args   the method arguments
         * @return fake neighboring cell info list if available, or the real data as fallback
         * @throws Throwable if invocation fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getNeighboringCellInfo");
            if (BLocationManager.isFakeLocationEnable()) {
                List<BCell> cell = BLocationManager.get().getNeighboringCell(BActivityThread.getUserId(), BActivityThread.getAppPackageName());
                // TODO Transfer BCell to CdmaCellLocation/GsmCellLocation
                return null;
            }
            return method.invoke(who, args);
        }
    }
}
