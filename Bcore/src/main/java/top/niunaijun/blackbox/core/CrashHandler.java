package top.niunaijun.blackbox.core;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Global uncaught exception handler for the BlackBox virtual engine.
 *
 * <p>Intercepts uncaught exceptions on any thread, forwards them to the
 * application-level exception handler (if registered via
 * {@link BlackBoxCore#getExceptionHandler()}), and then delegates to
 * the previously-installed default handler so that the standard crash
 * behaviour is preserved.</p>
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    /** The default handler that was active before this one was installed. */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * Convenience factory that installs a new {@code CrashHandler} as the
     * JVM-wide default uncaught exception handler.
     */
    public static void create() {
        new CrashHandler();
    }

    /**
     * Constructs and installs this handler as the default uncaught
     * exception handler, saving a reference to the previous handler
     * so it can still be invoked on crash.
     */
    public CrashHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * Called when an uncaught exception terminates a thread.
     *
     * <p>First forwards the exception to the BlackBox exception callback
     * if one is registered, then delegates to the original default handler.</p>
     *
     * @param t the thread that has an uncaught exception
     * @param e the exception that was thrown
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (BlackBoxCore.get().getExceptionHandler() != null) {
            BlackBoxCore.get().getExceptionHandler().uncaughtException(t, e);
        }
        mDefaultHandler.uncaughtException(t, e);
    }
}
