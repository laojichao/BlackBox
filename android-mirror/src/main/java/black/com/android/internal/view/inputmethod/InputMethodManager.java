package black.com.android.internal.view.inputmethod;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.view.inputmethod.InputMethodManager.
 * Provides access to the internal IInputMethod service binder.
 */
@BClassName("android.view.inputmethod.InputMethodManager")
public interface InputMethodManager {
    /** The underlying IInputMethod service binder for IME communication. */
    @BField
    IInterface mService();
}
