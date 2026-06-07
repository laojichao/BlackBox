package top.canyie.pine.callback;

import java.lang.reflect.Member;

import top.canyie.pine.Pine;

/**
 * Abstract base class for method hook callbacks that are invoked before and/or after
 * a hooked method executes.
 * <p>
 * Subclasses can override {@link #beforeCall(Pine.CallFrame)} and/or {@link #afterCall(Pine.CallFrame)}
 * to intercept method calls. Setting a result or exception in {@code beforeCall} will prevent
 * the original method from being called and skip remaining before-callbacks.
 * </p>
 *
 * @author canyie
 * @see Pine#hook(Member, MethodHook)
 * @see MethodReplacement
 */
public abstract class MethodHook {
    /**
     * Invoked before the hooked method gets called. You can inspect or modify call info
     * via the given {@code callFrame}.
     * <p>
     * Setting a result or exception via {@link Pine.CallFrame#setResult(Object)} or
     * {@link Pine.CallFrame#setThrowable(Throwable)} will prevent the original method call
     * and skip remaining before-callbacks. Throwing any exception from this method will cause
     * the result/exception you set to be reset; use {@link Pine.CallFrame#setThrowable(Throwable)}
     * instead if you want to propagate an exception.
     * </p>
     *
     * @param callFrame object that stores call info including method, thisObject, args, and result.
     * @throws Throwable if an unexpected error occurs; will be caught and logged.
     */
    public void beforeCall(Pine.CallFrame callFrame) throws Throwable {
    }

    /**
     * Invoked after the hooked method gets called. You can inspect or modify the result
     * via the given {@code callFrame}.
     * <p>
     * Throwing any exception from this method will cause the result or exception you set to
     * be reset. Use {@link Pine.CallFrame#setThrowable(Throwable)} to propagate an exception.
     * </p>
     *
     * @param callFrame object that stores call info including method, thisObject, args, and result.
     * @throws Throwable if an unexpected error occurs; will be caught and logged.
     */
    public void afterCall(Pine.CallFrame callFrame) throws Throwable {
    }

    /**
     * Handle for unregistering a previously registered hook. Obtain an instance via
     * {@link Pine#hook(Member, MethodHook)}.
     */
    public class Unhook {
        private final Pine.HookRecord hookRecord;

        /**
         * Creates a new Unhook handle associated with the given hook record.
         *
         * @param hookRecord the hook record this unhook handle is associated with.
         */
        public Unhook(Pine.HookRecord hookRecord) {
            this.hookRecord = hookRecord;
        }

        /**
         * Returns the target method or constructor that was hooked.
         *
         * @return the hooked {@link Member}.
         */
        public Member getTarget() {
            return hookRecord.target;
        }

        /**
         * Returns the callback (hook) associated with this unhook handle.
         *
         * @return the {@link MethodHook} callback instance.
         */
        public MethodHook getCallback() {
            return MethodHook.this;
        }

        /**
         * Unregisters this hook. After calling this method, the associated callback will no longer
         * be invoked when the target method is called.
         */
        public void unhook() {
            Pine.getHookHandler().handleUnhook(hookRecord, MethodHook.this);
        }
    }
}
