package black.android.widget;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.widget.Toast fields.
 * Provides access to the internal INotificationManager service binder.
 */
@BClassName("android.widget.Toast")
public interface Toast {
    /** The static INotificationManager service used for toast display. */
    @BStaticField
    IInterface sService();
}
