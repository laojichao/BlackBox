package black.android.providers;

import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * Mirror of hidden android.provider.Settings inner classes.
 * Provides access to internal settings caches and content provider holders.
 */
@BClassName("android.provider.Settings")
public interface Settings {
    /**
     * Mirror of Settings.System for accessing system settings cache.
     */
    @BClassName("android.provider.Settings$System")
    interface System {
        /** The NameValueCache for system settings. */
        @BStaticField
        Object sNameValueCache();
    }

    /**
     * Mirror of Settings.Secure for accessing secure settings cache.
     */
    @BClassName("android.provider.Settings$Secure")
    interface Secure {
        /** The NameValueCache for secure settings. */
        @BStaticField
        Object sNameValueCache();
    }

    /**
     * Mirror of Settings.ContentProviderHolder.
     * Holds the IContentProvider for settings access.
     */
    @BClassName("android.provider.Settings$ContentProviderHolder")
    interface ContentProviderHolder {
        /** The IContentProvider for the settings provider. */
        @BField
        IInterface mContentProvider();
    }

    /**
     * Mirror of Settings.NameValueCache for Oreo (API 26+).
     * Uses a provider holder instead of a direct content provider reference.
     */
    @BClassName("android.provider.Settings$NameValueCache")
    interface NameValueCacheOreo {
        /** The ContentProviderHolder for settings access. */
        @BField
        Object mProviderHolder();
    }

    /**
     * Mirror of Settings.NameValueCache for pre-Oreo.
     * Directly holds the IContentProvider reference.
     */
    @BClassName("android.provider.Settings$NameValueCache")
    interface NameValueCache {
        /** The IContentProvider for settings access. */
        @BField
        Object mContentProvider();
    }

    /**
     * Mirror of Settings.Global for accessing global settings cache.
     */
    @BClassName("android.provider.Settings$Global")
    interface Global {
        /** The NameValueCache for global settings. */
        @BStaticField
        Object sNameValueCache();
    }
}
