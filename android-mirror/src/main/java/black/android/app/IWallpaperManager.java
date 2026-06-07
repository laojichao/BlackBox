package black.android.app;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Reflection mirror for {@code android.app.IWallpaperManager}.
 * Provides access to the Wallpaper Manager AIDL service stub.
 */
@BClassName("android.app.IWallpaperManager")
public interface IWallpaperManager {
    /**
     * Reflection mirror for {@code android.app.IWallpaperManager.Stub}.
     * Converts an IBinder to the IWallpaperManager interface.
     */
    @BClassName("android.app.IWallpaperManager$Stub")
    interface Stub {
        /** Converts a raw IBinder to the IWallpaperManager proxy interface. */
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
