package black.android.content;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.content.ClipboardManager internals for Android Oreo (API 26+).
 * Exposes both static and instance references to the IClipboard service.
 */
@BClassName("android.content.ClipboardManager")
public interface ClipboardManagerOreo {
    /** Static reference to the IClipboard system service. */
    @BStaticField
    IInterface sService();

    /** Instance reference to the IClipboard system service. */
    @BField
    IInterface mService();
}
