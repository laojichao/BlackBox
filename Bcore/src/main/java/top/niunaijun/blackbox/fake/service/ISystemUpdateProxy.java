package top.niunaijun.blackbox.fake.service;


import black.android.os.BRServiceManager;
import black.android.view.BRIAutoFillManagerStub;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;

/**
 * Proxy for the Android {@code system_update} system service.
 * <p>
 * Intercepts system update service calls to prevent virtual environment apps
 * from querying or triggering real system update operations on the host device.
 */
public class ISystemUpdateProxy extends BinderInvocationStub {
    /**
     * Constructs a new proxy by acquiring the real system update binder service.
     */
    public ISystemUpdateProxy() {
        super(BRServiceManager.get().getService("system_update"));
    }

    /**
     * Returns the underlying system update service interface.
     *
     * @return the real system update binder interface
     */
    @Override
    protected Object getWho() {
        return BRIAutoFillManagerStub.get().asInterface(BRServiceManager.get().getService("system_update"));
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("system_update");
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
}
