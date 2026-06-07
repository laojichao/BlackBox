package black.android.view;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.view.WindowManagerGlobal fields.
 * Provides access to the window manager service binder and permission constants.
 */
@BClassName("android.view.WindowManagerGlobal")
public interface WindowManagerGlobal {
    /** Error code for permission denied when adding a window. */
    @BStaticField
    int ADD_PERMISSION_DENIED();

    /** The cached IWindowManager service binder. */
    @BStaticField
    IInterface sWindowManagerService();
}
