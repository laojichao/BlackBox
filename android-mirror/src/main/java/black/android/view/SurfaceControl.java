package black.android.view;

import android.graphics.Bitmap;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.view.SurfaceControl.
 * Provides access to the hidden screenshot method for capturing screen content.
 */
@BClassName("android.view.SurfaceControl")
public interface SurfaceControl {
    /**
     * Take a screenshot of the specified width and height.
     */
    @BStaticMethod
    Bitmap screnshot(int int0, int int1);
}
