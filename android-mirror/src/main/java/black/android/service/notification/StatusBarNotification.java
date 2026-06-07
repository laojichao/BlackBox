package black.android.service.notification;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.service.notification.StatusBarNotification fields.
 * Provides access to internal status bar notification identifiers.
 */
@BClassName("android.service.notification.StatusBarNotification")
public interface StatusBarNotification {
    /** The notification ID. */
    @BField
    Integer id();

    /** The package that posted the notification. */
    @BField
    String opPkg();

    /** The target package of the notification. */
    @BField
    String pkg();

    /** The notification tag, or null if untagged. */
    @BField
    String tag();
}
