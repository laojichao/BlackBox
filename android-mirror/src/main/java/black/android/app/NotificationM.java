package black.android.app;

import android.graphics.drawable.Icon;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Reflection mirror for {@code android.app.Notification} (Marshmallow variant).
 * Provides access to the hidden icon fields of a Notification.
 */
@BClassName("android.app.Notification")
public interface NotificationM {
    /** The large icon displayed in the notification. */
    @BField
    Icon mLargeIcon();

    /** The small icon displayed in the notification status bar. */
    @BField
    Icon mSmallIcon();
}
