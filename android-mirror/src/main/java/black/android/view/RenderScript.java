package black.android.view;

import java.io.File;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.renderscript.RenderScript.
 * Provides access to the setupDiskCache method for RenderScript cache initialization.
 */
@BClassName("android.renderscript.RenderScript")
public interface RenderScript {
    /**
     * Set up the disk cache directory for RenderScript.
     */
    @BStaticMethod
    void setupDiskCache(File File0);
}
