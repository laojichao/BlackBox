package black.android.content.res;

import android.content.res.Configuration;
import android.util.DisplayMetrics;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Mirror of hidden android.content.res.AssetManager internals.
 * Provides access to asset path management and display configuration.
 */
@BClassName("android.content.res.AssetManager")
public interface AssetManager {
    /** Creates a new AssetManager instance via reflection. */
    @BConstructor
    android.content.res.AssetManager _new();

    /** Adds an asset path and returns the cookie identifier. */
    @BMethod
    Integer addAssetPath(String String0);

    /** Returns the current device configuration. */
    @BMethod
    Configuration getConfiguration();

    /** Returns the current display metrics. */
    @BMethod
    DisplayMetrics getDisplayMetrics();
}
