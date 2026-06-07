package black.android.content;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.content.IntentSender internals.
 * Provides access to the underlying IIntentSender binder target.
 */
@BClassName("android.content.IntentSender")
public interface IntentSender {
    /** The underlying IIntentSender binder interface. */
    @BField
    IInterface mTarget();
}
