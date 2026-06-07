package top.niunaijun.blackbox.core.system.notification;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Tracks all notification-related resources owned by a single virtual package within a
 * single virtual user.
 * <p>
 * Stores the notification channels, channel groups, and active notification IDs that
 * have been created or posted by a virtual app so they can be cleaned up when the
 * package is removed or the user is deleted.
 * </p>
 */
public class NotificationRecord {
    /** Map of original channel ID to the corresponding {@link NotificationChannel}. */
    public final Map<String, NotificationChannel> mNotificationChannels = new HashMap<>();
    /** Map of original group ID to the corresponding {@link NotificationChannelGroup}. */
    public final Map<String, NotificationChannelGroup> mNotificationChannelGroups = new HashMap<>();
    /** Set of host-side notification IDs currently active for this package. */
    public final Set<Integer> mIds = new HashSet<>();
}
