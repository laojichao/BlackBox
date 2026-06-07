package black.android.app;

import android.app.PendingIntent;
import android.content.Context;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Reflection mirror for {@code android.app.Notification}.
 * Provides access to the deprecated {@code setLatestEventInfo} method
 * removed in newer Android versions.
 */
@BClassName("android.app.Notification")
public interface Notification {
    /** Sets the latest event info displayed in the notification (deprecated in Lollipop). */
    @BMethod
    void setLatestEventInfo(Context Context0, CharSequence CharSequence1, CharSequence CharSequence2, PendingIntent PendingIntent3);
}
