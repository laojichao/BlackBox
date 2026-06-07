package top.niunaijun.blackbox.fake.service;

import android.app.job.JobInfo;
import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.app.job.BRIJobSchedulerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;

/**
 * Proxy for the Android Job Scheduler system service (IJobScheduler).
 * Intercepts job scheduling, cancellation, and enqueue operations to
 * route them through the virtual environment's job manager. This ensures
 * that scheduled jobs are tracked per-app within the virtual container
 * and do not leak into the host system's job scheduler.
 *
 * @author Milk
 */
public class IJobServiceProxy extends BinderInvocationStub {
    /** Tag used for logging within this proxy. */
    public static final String TAG = "JobServiceStub";

    /**
     * Constructs a new proxy by obtaining the Job Scheduler binder service.
     */
    public IJobServiceProxy() {
        super(BRServiceManager.get().getService(Context.JOB_SCHEDULER_SERVICE));
    }

    /**
     * Returns the IJobScheduler interface instance from the system service.
     *
     * @return the original IJobScheduler binder interface
     */
    @Override
    protected Object getWho() {
        IBinder jobScheduler = BRServiceManager.get().getService("jobscheduler");
        return BRIJobSchedulerStub.get().asInterface(jobScheduler);
    }

    /**
     * Replaces the system Job Scheduler service with this proxy instance.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    /**
     * Hook that intercepts {@code schedule} to route job scheduling through
     * the virtual environment's job manager before delegating to the system.
     */
    @ProxyMethod("schedule")
    public static class Schedule extends MethodHook {

        /**
         * Replaces the JobInfo with a proxied version managed by the virtual environment.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is replaced with the proxied JobInfo
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            JobInfo jobInfo = (JobInfo) args[0];
            JobInfo proxyJobInfo = BlackBoxCore.getBJobManager()
                    .schedule(jobInfo);
            args[0] = proxyJobInfo;
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code cancel} to route job cancellation through
     * the virtual environment's job manager using the current app's process name.
     */
    @ProxyMethod("cancel")
    public static class Cancel extends MethodHook {

        /**
         * Translates the job ID using the virtual environment's job manager and
         * delegates the cancellation to the original method.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is replaced with the translated job ID
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            args[0] = BlackBoxCore.getBJobManager()
                    .cancel(BActivityThread.getAppConfig().processName, (Integer) args[0]);
            return method.invoke(who, args);
        }
    }

    /**
     * Hook that intercepts {@code cancelAll} to cancel all jobs for the current
     * virtual app through the virtual environment's job manager.
     */
    @ProxyMethod("cancelAll")
    public static class CancelAll extends MethodHook {

        /**
         * Cancels all scheduled jobs for the current app's process in the virtual environment.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BlackBoxCore.getBJobManager().cancelAll(BActivityThread.getAppConfig().processName);
            return method.invoke(who, args);
        }
    }


    /**
     * Hook that intercepts {@code enqueue} to route job enqueueing through
     * the virtual environment's job manager before delegating to the system.
     */
    @ProxyMethod("enqueue")
    public static class Enqueue extends MethodHook {

        /**
         * Replaces the JobInfo with a proxied version managed by the virtual environment.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[0] is replaced with the proxied JobInfo
         * @return the result of the original method invocation
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            JobInfo jobInfo = (JobInfo) args[0];
            JobInfo proxyJobInfo = BlackBoxCore.getBJobManager()
                    .schedule(jobInfo);
            args[0] = proxyJobInfo;
            return method.invoke(who, args);
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
