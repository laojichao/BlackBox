package top.niunaijun.blackbox.fake.service;


import black.android.hardware.location.BRIContextHubServiceStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.service.base.ValueMethodProxy;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Proxy for the Android Context Hub system service (IContextHubService).
 * Intercepts context hub operations (nanoapp communication via sensor hubs)
 * and returns safe default values to prevent virtual apps from directly
 * interacting with the device's context hub hardware. Adapts the service
 * name based on the Android version.
 *
 * @author BlackBox
 */
public class IContextHubServiceProxy extends BinderInvocationStub {

    /**
     * Constructs a new proxy by obtaining the Context Hub binder service.
     * Uses the appropriate service name based on the Android version.
     */
    public IContextHubServiceProxy() {
        super(BRServiceManager.get().getService(getServiceName()));
    }

    /**
     * Returns the context hub service name based on Android version.
     *
     * @return "contexthub" on Android O and above, "contexthub_service" on earlier versions
     */
    private static String getServiceName() {
        return BuildCompat.isOreo() ? "contexthub" : "contexthub_service";
    }

    /**
     * Returns the IContextHubService interface instance from the system service.
     *
     * @return the original IContextHubService binder interface
     */
    @Override
    protected Object getWho() {
        return BRIContextHubServiceStub.get().asInterface(BRServiceManager.get().getService(getServiceName()));
    }

    /**
     * Replaces the system Context Hub service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(getServiceName());
    }

    /**
     * Binds default value method proxies for context hub operations.
     * registerCallback returns 0, getContextHubInfo returns null,
     * and getContextHubHandles returns an empty array.
     */
    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("registerCallback", 0));
        addMethodHook(new ValueMethodProxy("getContextHubInfo", null));
        addMethodHook(new ValueMethodProxy("getContextHubHandles",new int[]{}));
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
