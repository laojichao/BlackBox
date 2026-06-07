package black.android.app;

import java.util.List;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Reflection mirror for {@code android.app.NotificationChannelGroup}.
 * Provides access to hidden fields of a notification channel group (Android O+).
 */
@BClassName("android.app.NotificationChannelGroup")
public interface NotificationChannelGroup {
    /** The list of NotificationChannels belonging to this group. */
    @BField
    List<android.app.NotificationChannel> mChannels();

    /** The unique identifier string for this notification channel group. */
    @BField
    String mId();
}
