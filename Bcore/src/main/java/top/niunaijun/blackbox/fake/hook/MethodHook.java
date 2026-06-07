package top.niunaijun.blackbox.fake.hook;

import java.lang.reflect.Method;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Abstract base class for individual method hooks within a {@link ClassInvocationStub}.
 * Subclasses implement the {@link #hook} method to provide custom behavior for a
 * specific intercepted method call. Supports pre-hook interception and post-hook
 * result transformation.
 */
public abstract class MethodHook {
    /**
     * Returns the name of the method this hook intercepts.
     *
     * @return the method name, or null if not set
     */
    protected String getMethodName() {
        return null;
    }

    /**
     * Called after the hook method executes. Can transform the result.
     *
     * @param result the result from the hook method
     * @return the potentially transformed result
     * @throws Throwable if post-processing fails
     */
    protected Object afterHook(Object result) throws Throwable {
        return result;
    }

    /**
     * Called before the hook method executes. Can short-circuit the hook by
     * returning a non-null value.
     *
     * @param who    the original object being invoked
     * @param method the method being called
     * @param args   the method arguments
     * @return a non-null value to short-circuit, or null to continue to hook()
     * @throws Throwable if pre-processing fails
     */
    protected Object beforeHook(Object who, Method method, Object[] args) throws Throwable {
        return null;
    }

    /**
     * The main hook method that provides custom behavior for the intercepted call.
     *
     * @param who    the original object being invoked
     * @param method the method being called
     * @param args   the method arguments
     * @return the result of the hooked method
     * @throws Throwable if the hook fails
     */
    protected abstract Object hook(Object who, Method method, Object[] args) throws Throwable;

    /**
     * Checks whether this hook is enabled. By default, hooks are only active
     * in the black (virtual) process.
     *
     * @return true if this hook is enabled
     */
    protected boolean isEnable() {
        return BlackBoxCore.get().isBlackProcess();
    }
}
