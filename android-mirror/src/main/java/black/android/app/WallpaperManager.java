package black.android.app;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Reflection mirror for {@code android.app.WallpaperManager}.
 * Provides access to the hidden static globals field and its inner
 * Globals helper class that holds the wallpaper service reference.
 */
@BClassName("android.app.WallpaperManager")
public interface WallpaperManager {
    /** The static WallpaperManager.Globals singleton instance. */
    @BStaticField
    Object sGlobals();

    /**
     * Reflection mirror for {@code android.app.WallpaperManager.Globals}.
     * Holds a reference to the IWallpaperManager service.
     */
    @BClassName("android.app.WallpaperManager$Globals")
    interface Globals {
        /** The IWallpaperManager service binder interface. */
        @BField
        Object mService();
    }
}
