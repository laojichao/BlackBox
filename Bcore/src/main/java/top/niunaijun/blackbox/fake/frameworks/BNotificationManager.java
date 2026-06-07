package top.niunaijun.blackbox.fake.frameworks;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.notification.IBNotificationManagerService;

/**
 * Client-side manager for notification operations within the virtual environment.
 * Provides a facade over {@link IBNotificationManagerService} for creating, querying,
 * and deleting notification channels and groups, as well as enqueuing and canceling
 * notifications scoped to the current virtual user.
 */
public class BNotificationManager extends BlackManager<IBNotificationManagerService> {
    private static final BNotificationManager sNotificationManager = new BNotificationManager();

    /**
     * Returns the singleton instance of {@link BNotificationManager}.
     *
     * @return the singleton BNotificationManager instance
     */
    public static BNotificationManager get() {
        return sNotificationManager;
    }

    @Override
    protected String getServiceName() {
        return ServiceManager.NOTIFICATION_MANAGER;
    }

    /**
     * Gets a notification channel by its ID.
     *
     * @param channelId the notification channel ID
     * @return the NotificationChannel, or null on failure
     */
    public NotificationChannel getNotificationChannel(String channelId) {
        try {
            return getService().getNotificationChannel(channelId, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets all notification channel groups for a package.
     *
     * @param packageName the package name
     * @return the list of NotificationChannelGroup objects, or null on failure
     */
    public List<NotificationChannelGroup> getNotificationChannelGroups(String packageName) {
        try {
            return getService().getNotificationChannelGroups(packageName, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a notification channel within the virtual environment.
     *
     * @param notificationChannel the NotificationChannel to create
     */
    public void createNotificationChannel(NotificationChannel notificationChannel) {
        try {
            getService().createNotificationChannel(notificationChannel, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a notification channel by its ID.
     *
     * @param channelId the notification channel ID to delete
     */
    public void deleteNotificationChannel(String channelId) {
        try {
            getService().deleteNotificationChannel(channelId, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a notification channel group within the virtual environment.
     *
     * @param notificationChannelGroup the NotificationChannelGroup to create
     */
    public void createNotificationChannelGroup(NotificationChannelGroup notificationChannelGroup) {
        try {
            getService().createNotificationChannelGroup(notificationChannelGroup, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a notification channel group by its ID.
     *
     * @param groupId the group ID to delete
     */
    public void deleteNotificationChannelGroup(String groupId) {
        try {
            getService().deleteNotificationChannelGroup(groupId, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enqueues a notification with the given ID and tag.
     *
     * @param id           the notification ID
     * @param tag          the notification tag
     * @param notification the Notification to enqueue
     */
    public void enqueueNotificationWithTag(int id, String tag, Notification notification) {
        try {
            getService().enqueueNotificationWithTag(id, tag, notification, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancels a notification by its ID and tag.
     *
     * @param id  the notification ID
     * @param tag the notification tag
     */
    public void cancelNotificationWithTag(int id, String tag) {
        try {
            getService().cancelNotificationWithTag(id, tag, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all notification channels for a package.
     *
     * @param packageName the package name
     * @return the list of NotificationChannel objects, or an empty list on failure
     */
    public List<NotificationChannel> getNotificationChannels(String packageName) {
        try {
            return getService().getNotificationChannels(packageName, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
