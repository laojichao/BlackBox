package black.android.renderscript;

import java.io.File;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.renderscript.RenderScriptCacheDir.
 * Provides access to the setupDiskCache method for RenderScript cache.
 */
@BClassName("android.renderscript.RenderScriptCacheDir")
public interface RenderScriptCacheDir {
    /**
     * Set up the disk cache directory for RenderScript operations.
     */
    @BStaticMethod
    void setupDiskCache(File File0);
}
