package black.android.content.pm;

import android.graphics.Bitmap;
import android.net.Uri;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.content.pm.PackageInstaller internals.
 * Provides access to SessionParams and SessionInfo fields for package installation.
 */
@BClassName("mirror.android.content.pm.PackageInstaller")
public interface PackageInstaller {
    /**
     * Mirror of PackageInstaller.SessionParams for Marshmallow (API 23+).
     * Includes grantedRuntimePermissions and volumeUuid fields.
     */
    @BClassName("android.content.pm.PackageInstaller$SessionParams")
    interface SessionParamsMarshmallow {
        /** The ABI override for installation. */
        @BField
        String abiOverride();

        /** The application icon bitmap. */
        @BField
        Bitmap appIcon();

        /** Timestamp of the last icon modification. */
        @BField
        long appIconLastModified();

        /** The application label for display. */
        @BField
        String appLabel();

        /** The package name being installed. */
        @BField
        String appPackageName();

        /** Granted runtime permissions for the install. */
        @BField
        String[] grantedRuntimePermissions();

        /** Installation flags bitmask. */
        @BField
        int installFlags();

        /** Installation location (internal, external, auto). */
        @BField
        int installLocation();

        /** Session mode (full, inherit, rapid). */
        @BField
        int mode();

        /** URI of the originating request. */
        @BField
        Uri originatingUri();

        /** URI of the referrer. */
        @BField
        Uri referrerUri();

        /** Expected size of the install in bytes. */
        @BField
        long sizeBytes();

        /** The storage volume UUID for installation. */
        @BField
        String volumeUuid();
    }

    /**
     * Mirror of PackageInstaller.SessionParams for Lollipop (API 21-22).
     * Lacks grantedRuntimePermissions and volumeUuid fields.
     */
    @BClassName("android.content.pm.PackageInstaller$SessionParams")
    interface SessionParamsLOLLIPOP {
        /** The ABI override for installation. */
        @BField
        String abiOverride();

        /** The application icon bitmap. */
        @BField
        Bitmap appIcon();

        /** Timestamp of the last icon modification. */
        @BField
        long appIconLastModified();

        /** The application label for display. */
        @BField
        String appLabel();

        /** The package name being installed. */
        @BField
        String appPackageName();

        /** Installation flags bitmask. */
        @BField
        int installFlags();

        /** Installation location (internal, external, auto). */
        @BField
        int installLocation();

        /** Session mode (full, inherit, rapid). */
        @BField
        int mode();

        /** URI of the originating request. */
        @BField
        Uri originatingUri();

        /** URI of the referrer. */
        @BField
        Uri referrerUri();

        /** Expected size of the install in bytes. */
        @BField
        long sizeBytes();
    }

    /**
     * Mirror of PackageInstaller.SessionInfo.
     * Provides read access to an active installation session's state.
     */
    @BClassName("android.content.pm.PackageInstaller$SessionInfo")
    interface SessionInfo {
        /** Creates a new SessionInfo instance. */
        @BConstructor
        SessionInfo _new();

        /** Whether the session is currently active. */
        @BField
        boolean active();

        /** The application icon bitmap. */
        @BField
        Bitmap appIcon();

        /** The application label for display. */
        @BField
        CharSequence appLabel();

        /** The package name being installed. */
        @BField
        String appPackageName();

        /** The package name of the installer application. */
        @BField
        String installerPackageName();

        /** Session mode (full, inherit, rapid). */
        @BField
        int mode();

        /** Installation progress as a fraction (0.0 to 1.0). */
        @BField
        float progress();

        /** The resolved base code path after installation. */
        @BField
        String resolvedBaseCodePath();

        /** Whether the session has been sealed. */
        @BField
        boolean sealed();

        /** The unique session identifier. */
        @BField
        int sessionId();

        /** Expected size of the install in bytes. */
        @BField
        long sizeBytes();
    }
}
