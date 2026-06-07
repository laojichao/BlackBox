package black.android.content.pm;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * Mirror of hidden android.content.pm.ApplicationInfo fields introduced in Nougat (API 24).
 * Exposes credential and device protected data directories.
 */
@BClassName("android.content.pm.ApplicationInfo")
public interface ApplicationInfoN {
    /** The credential-encrypted data directory path. */
    @BField
    String credentialEncryptedDataDir();

    /** The credential-protected data directory path. */
    @BField
    String credentialProtectedDataDir();

    /** The device-encrypted data directory path. */
    @BField
    String deviceEncryptedDataDir();

    /** The device-protected data directory path. */
    @BField
    String deviceProtectedDataDir();
}
