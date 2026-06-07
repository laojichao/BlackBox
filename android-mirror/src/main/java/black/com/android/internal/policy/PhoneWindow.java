package black.com.android.internal.policy;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden com.android.internal.policy.PhoneWindow$WindowManagerHolder.
 * Provides access to the static window manager service binder held by PhoneWindow.
 */
@BClassName("com.android.internal.policy.PhoneWindow$WindowManagerHolder")
public interface PhoneWindow {
    /** The cached IWindowManager service binder. */
    @BStaticField
    IInterface sWindowManager();
}
