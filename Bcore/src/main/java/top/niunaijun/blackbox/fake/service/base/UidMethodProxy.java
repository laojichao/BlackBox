package top.niunaijun.blackbox.fake.service.base;

import java.lang.reflect.Method;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.MethodHook;

/**
 * A reusable method hook that replaces a virtual application's UID with
 * the host UID at a specified argument index before delegating the call.
 * <p>
 * This proxy is used by service proxies that need to transparently remap
 * the virtual environment UID to the real host UID in service method arguments.
 */
public class UidMethodProxy extends MethodHook {
    private final int index;
    private final String name;

    /**
     * Constructs a new UID method proxy.
     *
     * @param name  the name of the method to hook
     * @param index the index of the UID argument to replace
     */
    public UidMethodProxy(String name, int index) {
        this.index = index;
        this.name = name;
    }

    /**
     * Returns the method name this proxy intercepts.
     *
     * @return the target method name
     */
    @Override
    protected String getMethodName() {
        return name;
    }

    /**
     * Replaces the UID at the configured argument index with the host UID
     * if it matches the virtual app's UID, then delegates to the real method.
     *
     * @param who    the target object
     * @param method the original method
     * @param args   the method arguments
     * @return the result of the delegated method call
     * @throws Throwable if invocation fails
     */
    @Override
    protected Object hook(Object who, Method method, Object[] args) throws Throwable {
        int uid = (int) args[index];
        if (uid == BActivityThread.getBUid()) {
            args[index] = BlackBoxCore.getHostUid();
        }
        return method.invoke(who, args);
    }
}
