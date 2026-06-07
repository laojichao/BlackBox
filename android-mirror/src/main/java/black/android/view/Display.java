package black.android.view;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.view.Display fields.
 * Provides access to the static IWindowManager binder.
 */
@BClassName("android.view.Display")
public interface Display {
    /** The cached IWindowManager service binder. */
    @BStaticField
    IInterface sWindowManager();
}
