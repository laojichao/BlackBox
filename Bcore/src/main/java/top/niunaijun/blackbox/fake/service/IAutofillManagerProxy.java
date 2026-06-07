package top.niunaijun.blackbox.fake.service;

import android.content.ComponentName;

import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.android.view.BRIAutoFillManagerStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.proxy.ProxyManifest;

/**
 * Proxy for the Android Autofill Manager system service (IAutoFillManager).
 * Intercepts autofill session creation to replace the ComponentName in the
 * request with the virtual environment's proxy activity. This ensures that
 * autofill requests from virtual apps are correctly routed through the
 * virtual environment.
 *
 * @author Milk
 */
public class IAutofillManagerProxy extends BinderInvocationStub {
    /** Tag for logging. */
    public static final String TAG = "AutofillManagerStub";

    /**
     * Constructs a new proxy by obtaining the Autofill Manager binder service.
     */
    public IAutofillManagerProxy() {
        super(BRServiceManager.get().getService("autofill"));
    }

    /**
     * Returns the IAutoFillManager interface instance from the system service.
     *
     * @return the original IAutoFillManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRIAutoFillManagerStub.get().asInterface(BRServiceManager.get().getService("autofill"));
    }

    /**
     * Replaces the system Autofill Manager service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("autofill");
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
     * Hook that intercepts {@code startSession} to replace the ComponentName
     * in the autofill request with the virtual environment's proxy activity.
     */
    @ProxyMethod("startSession")
    public static class StartSession extends MethodHook {

        /**
         * Replaces any ComponentName argument with the host package's proxy activity.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; ComponentName values are replaced
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] == null)
                        continue;
                    if (args[i] instanceof ComponentName) {
                        args[i] = new ComponentName(BlackBoxCore.getHostPkg(), ProxyManifest.getProxyActivity(BActivityThread.getAppPid()));
                    }
                }
            }
            return method.invoke(who, args);
        }
    }
}
