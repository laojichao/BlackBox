package black.android.os;

import android.os.Handler.Callback;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.os.Handler fields.
 * Provides access to the internal Callback field for message interception.
 */
@BClassName("android.os.Handler")
public interface Handler {
    /**
     * The internal Callback set on this Handler, if any.
     */
    @BField
    Callback mCallback();
}
