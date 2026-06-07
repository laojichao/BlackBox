package black.android.app;

import android.app.Notification;
import android.content.Context;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.Notification} (Lollipop variant).
 * Provides access to the hidden {@code Notification.Builder.rebuild} static method.
 */
@BClassName("android.app.Notification")
public interface NotificationL {
    /**
     * Reflection mirror for {@code android.app.Notification.Builder} (Lollipop).
     * Rebuilds a notification from its persisted form.
     */
    @BClassName("android.app.Notification$Builder")
    interface Builder {
        /** Reconstructs a Notification from the given context and persisted notification. */
        @BStaticMethod
        Notification rebuild(Context Context0, Notification Notification1);
    }
}
