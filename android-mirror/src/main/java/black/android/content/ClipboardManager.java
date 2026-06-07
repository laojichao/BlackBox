package black.android.content;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.content.ClipboardManager internals.
 * Provides access to the static IClipboard service binder.
 */
@BClassName("android.content.ClipboardManager")
public interface ClipboardManager {
    /** Static reference to the IClipboard system service. */
    @BStaticField
    IInterface sService();

    /** Returns the IClipboard system service instance. */
    @BStaticMethod
    IInterface getService();
}
