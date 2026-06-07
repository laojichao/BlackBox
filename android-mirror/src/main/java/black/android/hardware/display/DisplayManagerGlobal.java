package black.android.hardware.display;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.hardware.display.DisplayManagerGlobal.
 * Singleton providing global access to the display management system service.
 */
@BClassName("android.hardware.display.DisplayManagerGlobal")
public interface DisplayManagerGlobal {
    /** The IDisplayManager binder service interface. */
    @BField
    IInterface mDm();

    /** Returns the global DisplayManagerGlobal singleton instance. */
    @BStaticMethod
    Object getInstance();
}
