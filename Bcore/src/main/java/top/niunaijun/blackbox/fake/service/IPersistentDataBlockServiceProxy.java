package top.niunaijun.blackbox.fake.service;


import black.android.os.BRServiceManager;
import black.android.service.persistentdata.BRIPersistentDataBlockServiceStub;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.service.base.ValueMethodProxy;

/**
 * Proxy for the Android {@code persistent_data_block} system service.
 * <p>
 * Intercepts persistent data block operations such as read, write, wipe, and OEM unlock
 * configuration, returning safe default values to the virtual environment.
 */
public class IPersistentDataBlockServiceProxy extends BinderInvocationStub {

    /** The system service name used to look up the persistent data block service. */
    public static final String NAME = "persistent_data_block";

    /**
     * Constructs a new proxy by acquiring the real persistent data block binder service.
     */
    public IPersistentDataBlockServiceProxy() {
        super(BRServiceManager.get().getService(NAME));
    }

    /**
     * Returns the underlying persistent data block service interface.
     *
     * @return the real {@code IPersistentDataBlockService} binder interface
     */
    @Override
    protected Object getWho() {
        return BRIPersistentDataBlockServiceStub.get().asInterface(BRServiceManager.get().getService(NAME));
    }

    /**
     * Injects this proxy into the system service registry.
     *
     * @param baseInvocation  the original service binder object
     * @param proxyInvocation the proxy binder object to register
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(NAME);
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
     * Binds method hooks that return safe default values for all persistent data block operations.
     */
    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("write", -1));
        addMethodHook(new ValueMethodProxy("read", new byte[0]));
        addMethodHook(new ValueMethodProxy("wipe", null));
        addMethodHook(new ValueMethodProxy("getDataBlockSize", 0));
        addMethodHook(new ValueMethodProxy("getMaximumDataBlockSize", 0));
        addMethodHook(new ValueMethodProxy("setOemUnlockEnabled", 0));
        addMethodHook(new ValueMethodProxy("getOemUnlockEnabled", false));
    }
}
