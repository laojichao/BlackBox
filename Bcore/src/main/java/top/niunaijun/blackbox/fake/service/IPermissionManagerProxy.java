package top.niunaijun.blackbox.fake.service;

import android.content.pm.PackageManager;

import black.android.app.BRActivityThread;
import black.android.app.BRContextImpl;
import black.android.os.BRServiceManager;
import black.android.permission.BRIPermissionManagerStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.service.base.PkgMethodProxy;
import top.niunaijun.blackbox.fake.service.base.ValueMethodProxy;
import top.niunaijun.blackbox.utils.Reflector;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Proxy for the Android Permission Manager system service (IPermissionManager).
 * Intercepts permission-related operations, DEX optimization calls, instant app
 * queries, and device identifier access checks to return safe default values
 * within the virtual environment. Replaces the system service and the
 * ApplicationPackageManager's mPermissionManager field via reflection.
 *
 * @author BlackBox
 */
public class IPermissionManagerProxy extends BinderInvocationStub {
    /** Tag used for logging within this proxy. */
    public static final String TAG = "IPermissionManagerProxy";

    /** The system service name for the permission manager. */
    private static final String P = "permissionmgr";

    /**
     * Constructs a new proxy by obtaining the Permission Manager binder service.
     */
    public IPermissionManagerProxy() {
        super(BRServiceManager.get().getService(P));
    }

    /**
     * Returns the IPermissionManager interface instance from the system service.
     *
     * @return the original IPermissionManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRIPermissionManagerStub.get().asInterface(BRServiceManager.get().getService(P));
    }

    /**
     * Replaces the system Permission Manager service with this proxy instance,
     * updating both the ActivityThread's sPermissionManager reference and the
     * ApplicationPackageManager's mPermissionManager field via reflection.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("permissionmgr");
        BRActivityThread.getWithException()._set_sPermissionManager(proxyInvocation);
        Object systemContext = BRActivityThread.get(BlackBoxCore.mainThread()).getSystemContext();
        PackageManager packageManager = BRContextImpl.get(systemContext).mPackageManager();
        if (packageManager != null) {
            try {
                Reflector.on("android.app.ApplicationPackageManager")
                        .field("mPermissionManager")
                        .set(packageManager, proxyInvocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Binds value-returning method hooks for permission operations, DEX optimization,
     * instant app checks, and device identifier access control. Additional hooks are
     * registered conditionally for Android Oreo and above.
     */
    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("addPermissionAsync", true));
        addMethodHook(new ValueMethodProxy("addPermission", true));
        addMethodHook(new ValueMethodProxy("performDexOpt", true));
        addMethodHook(new ValueMethodProxy("performDexOptIfNeeded", false));
        addMethodHook(new ValueMethodProxy("performDexOptSecondary", true));
        addMethodHook(new ValueMethodProxy("addOnPermissionsChangeListener", 0));
        addMethodHook(new ValueMethodProxy("removeOnPermissionsChangeListener", 0));
        addMethodHook(new ValueMethodProxy("checkDeviceIdentifierAccess", false));
        addMethodHook(new PkgMethodProxy("shouldShowRequestPermissionRationale"));
        if (BuildCompat.isOreo()) {
            addMethodHook(new ValueMethodProxy("notifyDexLoad", 0));
            addMethodHook(new ValueMethodProxy("notifyPackageUse", 0));
            addMethodHook(new ValueMethodProxy("setInstantAppCookie", false));
            addMethodHook(new ValueMethodProxy("isInstantApp", false));
        }
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

}
