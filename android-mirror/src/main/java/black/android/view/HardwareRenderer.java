package black.android.view;

import java.io.File;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Mirror of hidden android.view.HardwareRenderer.
 * Provides access to the setupDiskCache method for initializing the render cache.
 */
@BClassName("android.view.HardwareRenderer")
public interface HardwareRenderer {
    /**
     * Set up the disk cache directory for hardware rendering.
     */
    @BStaticMethod
    void setupDiskCache(File File0);
}
