package black.android.view;

import java.io.File;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.view.ThreadedRenderer.
 * Provides access to the setupDiskCache method for threaded rendering cache.
 */
@BClassName("android.view.ThreadedRenderer")
public interface ThreadedRenderer {
    /**
     * Set up the disk cache directory for threaded hardware rendering.
     */
    @BStaticMethod
    void setupDiskCache(File File0);
}
