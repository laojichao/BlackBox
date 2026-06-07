package top.niunaijun.blackbox.fake.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Method;
import java.util.List;

import black.android.app.BRNotificationManager;
import black.android.content.pm.BRParceledListSlice;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.frameworks.BNotificationManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.ParceledListSliceCompat;

/**
 * Proxy for the Android Notification Manager system service (INotificationManager).
 * Intercepts notification operations to manage notifications, notification channels,
 * and channel groups within the virtual environment. Replaces package names in all
 * method arguments and delegates channel/group CRUD operations to the virtual
 * environment's notification manager.
 *
 * @author Milk
 */
public class INotificationManagerProxy extends BinderInvocationStub {
    /** Tag used for logging within this proxy. */
    public static final String TAG = "INotificationManagerProxy";

    /**
     * Constructs a new proxy by obtaining the Notification Manager binder service.
     */
    public INotificationManagerProxy() {
        super(BRNotificationManager.get().getService().asBinder());
    }

    /**
     * Returns the INotificationManager interface instance from the system service.
     *
     * @return the original INotificationManager binder interface
     */
    @Override
    protected Object getWho() {
        return BRNotificationManager.get().getService();
    }

    /**
     * Replaces the system Notification Manager service with this proxy instance,
     * updating both the internal sService reference and the system service registry.
     *
     * @param baseInvocation the original service invocation object
     * @param proxyInvocation the proxy invocation object to inject
     */
    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        BRNotificationManager.get()._set_sService(getProxyInvocation());
        replaceSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Intercepts all method invocations to replace all package name arguments
     * with the virtual environment's host package before delegation.
     *
     * @param proxy the proxy object the method was invoked on
     * @param method the method being invoked
     * @param args the method arguments; all package names are replaced
     * @return the result of the original method invocation
     * @throws Throwable if the underlying method call fails
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        Slog.d(TAG, "call: " + method.getName());
        MethodParameterUtils.replaceAllAppPkg(args);
        return super.invoke(proxy, method, args);
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
     * Hook that intercepts {@code getNotificationChannel} to return the
     * notification channel from the virtual environment's notification manager.
     */
    @ProxyMethod("getNotificationChannel")
    public static class GetNotificationChannel extends MethodHook {

        /**
         * Retrieves the notification channel by its tag from the virtual notification manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; the last element is the channel tag
         * @return the NotificationChannel from the virtual environment
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            NotificationChannel notificationChannel = BNotificationManager.get().getNotificationChannel((String) args[args.length - 1]);
            return notificationChannel;
        }
    }

    /**
     * Hook that intercepts {@code getNotificationChannels} to return all
     * notification channels for the current virtual app.
     */
    @ProxyMethod("getNotificationChannels")
    public static class GetNotificationChannels extends MethodHook {

        /**
         * Retrieves all notification channels for the current app package
         * from the virtual notification manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return a ParceledListSlice containing the notification channels
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            List<NotificationChannel> notificationChannels = BNotificationManager.get().getNotificationChannels(BActivityThread.getAppPackageName());
            return ParceledListSliceCompat.create(notificationChannels);
        }
    }

    /**
     * Hook that intercepts {@code cancelNotificationWithTag} to cancel
     * a notification through the virtual environment's notification manager.
     */
    @ProxyMethod("cancelNotificationWithTag")
    public static class CancelNotificationWithTag extends MethodHook {

        /**
         * Extracts the tag and ID from the arguments and cancels the notification
         * in the virtual notification manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments containing tag and notification ID
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String tag = (String) args[getTagIndex()];
            int id = (int) args[getIdIndex()];
            BNotificationManager.get().cancelNotificationWithTag(id, tag);
            return 0;
        }

        /**
         * Returns the argument index of the notification tag, accounting for
         * API level differences (Android R adds a leading parameter).
         *
         * @return the index of the tag in the arguments array
         */
        public int getTagIndex() {
            if (BuildCompat.isR()) {
                return 2;
            }
            return 1;
        }

        /**
         * Returns the argument index of the notification ID.
         *
         * @return the index of the ID in the arguments array (tag index + 1)
         */
        public int getIdIndex() {
            return getTagIndex() + 1;
        }
    }


    /**
     * Hook that intercepts {@code enqueueNotificationWithTag} to enqueue
     * a notification through the virtual environment's notification manager.
     */
    @ProxyMethod("enqueueNotificationWithTag")
    public static class EnqueueNotificationWithTag extends MethodHook {

        /**
         * Extracts the tag, ID, and Notification object from the arguments and
         * enqueues the notification in the virtual notification manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments containing tag, ID, and Notification
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String tag = (String) args[getTagIndex()];
            int id = (int) args[getIdIndex()];
            Notification notification = MethodParameterUtils.getFirstParam(args, Notification.class);
            BNotificationManager.get().enqueueNotificationWithTag(id, tag, notification);
            return 0;
        }

        /**
         * Returns the argument index of the notification tag.
         *
         * @return always returns 2
         */
        public int getTagIndex() {
            return 2;
        }

        /**
         * Returns the argument index of the notification ID.
         *
         * @return the index of the ID in the arguments array (tag index + 1)
         */
        public int getIdIndex() {
            return getTagIndex() + 1;
        }
    }

    /**
     * Hook that intercepts {@code createNotificationChannels} to create
     * notification channels in the virtual environment (Android O+).
     */
    @ProxyMethod("createNotificationChannels")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static class CreateNotificationChannels extends MethodHook {

        /**
         * Extracts the notification channels from the ParceledListSlice argument
         * and creates each one in the virtual notification manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[1] is a ParceledListSlice of channels
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            List<?> list = BRParceledListSlice.get(args[1]).getList();
            if (list == null)
                return 0;
            for (Object o : list) {
                BNotificationManager.get().createNotificationChannel((NotificationChannel) o);
            }
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code deleteNotificationChannel} to delete
     * a notification channel in the virtual environment.
     */
    @ProxyMethod("deleteNotificationChannel")
    public static class DeleteNotificationChannel extends MethodHook {

        /**
         * Deletes the notification channel by its ID from the virtual notification manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[1] is the channel ID
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BNotificationManager.get().deleteNotificationChannel((String) args[1]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code createNotificationChannelGroups} to create
     * notification channel groups in the virtual environment (Android O+).
     */
    @ProxyMethod("createNotificationChannelGroups")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static class CreateNotificationChannelGroups extends MethodHook {

        /**
         * Extracts the notification channel groups from the ParceledListSlice argument
         * and creates each one in the virtual notification manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[1] is a ParceledListSlice of groups
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            List<?> list = BRParceledListSlice.get(args[1]).getList();
            for (Object o : list) {
                BNotificationManager.get().createNotificationChannelGroup((NotificationChannelGroup) o);
            }
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code deleteNotificationChannelGroup} to delete
     * a notification channel group in the virtual environment.
     */
    @ProxyMethod("deleteNotificationChannelGroup")
    public static class DeleteNotificationChannelGroup extends MethodHook {

        /**
         * Deletes the notification channel group by its ID from the virtual notification manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments; args[1] is the group ID
         * @return always returns 0
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BNotificationManager.get().deleteNotificationChannelGroup((String) args[1]);
            return 0;
        }
    }

    /**
     * Hook that intercepts {@code getNotificationChannelGroups} to return all
     * notification channel groups for the current virtual app.
     */
    @ProxyMethod("getNotificationChannelGroups")
    public static class GetNotificationChannelGroups extends MethodHook {

        /**
         * Retrieves all notification channel groups for the current app package
         * from the virtual notification manager.
         *
         * @param who the original object being hooked
         * @param method the method being intercepted
         * @param args the method arguments (unused)
         * @return a ParceledListSlice containing the notification channel groups
         * @throws Throwable if the underlying method call fails
         */
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            List<NotificationChannelGroup> notificationChannelGroups = BNotificationManager.get().getNotificationChannelGroups(BActivityThread.getAppPackageName());
            return ParceledListSliceCompat.create(notificationChannelGroups);
        }
    }
}
