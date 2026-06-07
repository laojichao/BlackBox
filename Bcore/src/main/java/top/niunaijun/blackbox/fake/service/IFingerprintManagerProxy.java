package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.os.IBinder;

import black.android.os.BRServiceManager;
import black.android.view.BRIGraphicsStatsStub;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.service.base.PkgMethodProxy;

/**
 * Proxy for the Android Fingerprint Manager system service (IFingerprintManager).
 * Intercepts fingerprint-related operations to replace package names in method
 * arguments, ensuring fingerprint API calls work correctly within the virtual
 * environment. Delegates hardware detection, authentication, and enrollment
 * queries to the real system service after parameter substitution.
 *
 * @author Findger
 */
public class IFingerprintManagerProxy extends BinderInvocationStub {
    /**
     * Constructs a new proxy by obtaining the Fingerprint Manager binder service.
     */
    public IFingerprintManagerProxy() {
        super(BRServiceManager.get().getService(Context.FINGERPRINT_SERVICE));
    }

    /**
     * Returns the IFingerprintManager interface instance from the system service.
     *
     * @return the original IFingerprintManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRIGraphicsStatsStub.get().asInterface(BRServiceManager.get().getService(Context.FINGERPRINT_SERVICE));
    }

    /**
     * Replaces the system Fingerprint Manager service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.FINGERPRINT_SERVICE);
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
     * Binds package-name-aware method hooks for fingerprint API calls.
     * Registers proxies for hardware detection, authentication, enrollment,
     * and related operations to ensure correct package resolution.
     */
    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new PkgMethodProxy("isHardwareDetected"));
        addMethodHook(new PkgMethodProxy("hasEnrolledFingerprints"));
        addMethodHook(new PkgMethodProxy("authenticate"));
        addMethodHook(new PkgMethodProxy("cancelAuthentication"));
        addMethodHook(new PkgMethodProxy("getEnrolledFingerprints"));
        addMethodHook(new PkgMethodProxy("getAuthenticatorId"));
    }
}
