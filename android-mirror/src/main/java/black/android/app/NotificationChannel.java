package black.android.app;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Reflection mirror for {@code android.app.NotificationChannel}.
 * Provides access to hidden fields of a notification channel (Android O+).
 */
@BClassName("android.app.NotificationChannel")
public interface NotificationChannel {
    /** The unique identifier string for this notification channel. */
    @BField
    String mId();
}
