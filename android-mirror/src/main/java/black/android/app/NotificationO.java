package black.android.app;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Reflection mirror for {@code android.app.Notification} (Oreo variant).
 * Provides access to the hidden channel ID and group key fields (Android O+).
 */
@BClassName("android.app.Notification")
public interface NotificationO {
    /** The notification channel ID this notification was posted to. */
    @BField
    String mChannelId();

    /** The notification group key this notification belongs to. */
    @BField
    String mGroupKey();
}
